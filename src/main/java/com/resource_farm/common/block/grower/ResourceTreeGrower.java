package com.resource_farm.common.block.grower;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static com.resource_farm.common.block.grower.ResourceTreeFeatures.ResourceTreeConfigurationMap;

public class ResourceTreeGrower {

    private final String treeId;
    private final float secondaryChance;
    private final String megaTree;
    private final String secondaryMegaTree;
    private final String tree;
    private final String secondaryTree;
    private final String flowers;
    private final String secondaryFlowers;

    public ResourceTreeGrower(String treeId,
                              float secondaryChance,
                              String megaTree,
                              String secondaryMegaTree,
                              String tree,
                              String secondaryTree,
                              String flowers,
                              String secondaryFlowers) {
        this.treeId = treeId;
        this.secondaryChance = secondaryChance;
        this.megaTree = megaTree;
        this.secondaryMegaTree = secondaryMegaTree;
        this.tree = tree;
        this.secondaryTree = secondaryTree;
        this.flowers = flowers;
        this.secondaryFlowers = secondaryFlowers;
    }

    public static class ResourceTreeGrowerBuilder {

        private String treeId;
        private final float secondaryChance;
        private final String megaTree;
        private final String secondaryMegaTree;
        private final String tree;
        private final String secondaryTree;
        private final String flowers;
        private final String secondaryFlowers;

        public ResourceTreeGrowerBuilder(float secondaryChance,
                                         String megaTree,
                                         String secondaryMegaTree,
                                         String tree,
                                         String secondaryTree,
                                         String flowers,
                                         String secondaryFlowers) {
            this.secondaryChance = secondaryChance;
            this.megaTree = megaTree;
            this.secondaryMegaTree = secondaryMegaTree;
            this.tree = tree;
            this.secondaryTree = secondaryTree;
            this.flowers = flowers;
            this.secondaryFlowers = secondaryFlowers;
        }

        public ResourceTreeGrowerBuilder id(String treeId) {
            this.treeId = treeId;
            return this;
        }

        public ResourceTreeGrower build() {
            return new ResourceTreeGrower(this.treeId, this.secondaryChance,
                    this.megaTree, this.secondaryMegaTree,
                    this.tree, this.secondaryTree,
                    this.flowers, this.secondaryFlowers);
        }
    }

    @Nullable
    private String getConfiguredFeatureKey(RandomSource random, boolean flowers) {
        if (random.nextFloat() < this.secondaryChance) {
            if (flowers && this.secondaryFlowers != null) return this.secondaryFlowers;
            if (this.secondaryTree != null) return this.secondaryTree;
        }
        return flowers && this.flowers != null ? this.flowers : this.tree;
    }

    @Nullable
    private String getConfiguredMegaFeatureKey(RandomSource random) {
        return this.secondaryMegaTree != null && random.nextFloat() < this.secondaryChance ?
                this.secondaryMegaTree : this.megaTree;
    }

    public boolean growTree(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random) {
        String megaFeatureKey = this.getConfiguredMegaFeatureKey(random);
        if (megaFeatureKey != null && ResourceTreeConfigurationMap.containsKey(megaFeatureKey)) {
            ConfiguredFeature<?, ?> configuredMegaFeature = ResourceTreeFeatures.getResourceTreeConfiguredFeature(treeId, megaFeatureKey);

            BlockGrowFeatureEvent event = EventHooks.fireBlockGrowFeature(level, random, pos, null);
            if (event.isCanceled()) return false;

            for (int i = 0; i >= -1; --i) {
                for (int j = 0; j >= -1; --j) {
                    if (isTwoByTwoSapling(state, level, pos, i, j)) {
                        Map<BlockPos, BlockState> originalMegaBlocks = recordMegaTreeBaseBlocks(level, pos, i, j);
                        BlockState airState = Blocks.AIR.defaultBlockState();
                        level.setBlock(pos.offset(i, 0, j), airState, 4);
                        level.setBlock(pos.offset(i + 1, 0, j), airState, 4);
                        level.setBlock(pos.offset(i, 0, j + 1), airState, 4);
                        level.setBlock(pos.offset(i + 1, 0, j + 1), airState, 4);

                        boolean growSuccess = configuredMegaFeature.place(level, chunkGenerator, random, pos.offset(i, 0, j));

                        if (growSuccess) {
                            restoreOriginalBlocks(level, originalMegaBlocks);
                            return true;
                        }

                        level.setBlock(pos.offset(i, 0, j), state, 4);
                        level.setBlock(pos.offset(i + 1, 0, j), state, 4);
                        level.setBlock(pos.offset(i, 0, j + 1), state, 4);
                        level.setBlock(pos.offset(i + 1, 0, j + 1), state, 4);
                        return false;
                    }
                }
            }
        }

        String featureKey = this.getConfiguredFeatureKey(random, this.hasFlowers(level, pos));
        if (featureKey == null || !ResourceTreeConfigurationMap.containsKey(featureKey)) return false;

        ConfiguredFeature<?, ?> configuredFeature = ResourceTreeFeatures.getResourceTreeConfiguredFeature(treeId, featureKey);

        BlockGrowFeatureEvent normalEvent = EventHooks.fireBlockGrowFeature(level, random, pos, null);
        if (normalEvent.isCanceled()) return false;

        BlockPos normalBasePos = pos.below();
        BlockState normalOriginalState = level.getBlockState(normalBasePos);

        BlockState fluidState = level.getFluidState(pos).createLegacyBlock();
        level.setBlock(pos, fluidState, 4);

        boolean normalGrowSuccess = configuredFeature.place(level, chunkGenerator, random, pos);
        if (normalGrowSuccess) {
            level.setBlock(normalBasePos, normalOriginalState, 3);
            if (level.getBlockState(pos) == fluidState) {
                level.sendBlockUpdated(pos, state, fluidState, 2);
            }
            return true;
        } else {
            level.setBlock(pos, state, 4);
            return false;
        }
    }

    private Map<BlockPos, BlockState> recordMegaTreeBaseBlocks(ServerLevel level, BlockPos originPos, int xOffset, int zOffset) {
        Map<BlockPos, BlockState> originalBlocks = new HashMap<>();
        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                BlockPos saplingPos = originPos.offset(xOffset + x, 0, zOffset + z);
                BlockPos basePos = saplingPos.below();
                originalBlocks.put(basePos, level.getBlockState(basePos));
            }
        }
        return originalBlocks;
    }

    private void restoreOriginalBlocks(ServerLevel level, Map<BlockPos, BlockState> originalBlocks) {
        for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
            BlockPos targetPos = entry.getKey();
            BlockState originalState = entry.getValue();
            level.setBlock(targetPos, originalState, 3);
        }
    }

    private static boolean isTwoByTwoSapling(BlockState state, BlockGetter level, BlockPos pos, int xOffset, int yOffset) {
        Block block = state.getBlock();
        return level.getBlockState(pos.offset(xOffset, 0, yOffset)).is(block) && level.getBlockState(pos.offset(xOffset + 1, 0, yOffset)).is(block) &&
                level.getBlockState(pos.offset(xOffset, 0, yOffset + 1)).is(block) && level.getBlockState(pos.offset(xOffset + 1, 0, yOffset + 1)).is(block);
    }

    private boolean hasFlowers(LevelAccessor level, BlockPos pos) {
        for (BlockPos blockpos : BlockPos.MutableBlockPos.betweenClosed(pos.below().north(2).west(2), pos.above().south(2).east(2))) {
            if (level.getBlockState(blockpos).is(BlockTags.FLOWERS)) return true;
        }
        return false;
    }
}
