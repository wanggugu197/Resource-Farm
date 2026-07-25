package com.maple.resource_farm.ResourceTree.data.dataMaps;

import com.maple.resource_farm.ResourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.ResourceTree.data.grower.ResourceTreeFeatures;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 共享资源树生长器。
 */
public class ResourceTreeGrower {

    public static final Codec<ResourceTreeGrower> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("secondary_chance", 0.0F).forGetter(ResourceTreeGrower::secondaryChance),
            Identifier.CODEC.optionalFieldOf("mega_tree").forGetter(g -> Optional.ofNullable(g.megaTree)),
            Identifier.CODEC.optionalFieldOf("secondary_mega_tree").forGetter(g -> Optional.ofNullable(g.secondaryMegaTree)),
            Identifier.CODEC.optionalFieldOf("tree").forGetter(g -> Optional.ofNullable(g.tree)),
            Identifier.CODEC.optionalFieldOf("secondary_tree").forGetter(g -> Optional.ofNullable(g.secondaryTree)),
            Identifier.CODEC.optionalFieldOf("flowers").forGetter(g -> Optional.ofNullable(g.flowers)),
            Identifier.CODEC.optionalFieldOf("secondary_flowers").forGetter(g -> Optional.ofNullable(g.secondaryFlowers))).apply(instance,
                    (secondaryChance, megaTree, secondaryMegaTree, tree, secondaryTree, flowers, secondaryFlowers) -> new ResourceTreeGrower(
                            secondaryChance,
                            megaTree.orElse(null),
                            secondaryMegaTree.orElse(null),
                            tree.orElse(null),
                            secondaryTree.orElse(null),
                            flowers.orElse(null),
                            secondaryFlowers.orElse(null))));

    private final float secondaryChance;
    private final Identifier megaTree;
    private final Identifier secondaryMegaTree;
    private final Identifier tree;
    private final Identifier secondaryTree;
    private final Identifier flowers;
    private final Identifier secondaryFlowers;

    public ResourceTreeGrower(float secondaryChance,
                              Identifier megaTree,
                              Identifier secondaryMegaTree,
                              Identifier tree,
                              Identifier secondaryTree,
                              Identifier flowers,
                              Identifier secondaryFlowers) {
        this.secondaryChance = secondaryChance;
        this.megaTree = megaTree;
        this.secondaryMegaTree = secondaryMegaTree;
        this.tree = tree;
        this.secondaryTree = secondaryTree;
        this.flowers = flowers;
        this.secondaryFlowers = secondaryFlowers;
    }

    public float secondaryChance() {
        return secondaryChance;
    }

    private Identifier getConfiguredFeatureKey(RandomSource random, boolean hasFlowers) {
        if (random.nextFloat() < this.secondaryChance) {
            if (hasFlowers && this.secondaryFlowers != null) return this.secondaryFlowers;
            if (this.secondaryTree != null) return this.secondaryTree;
        }
        return (hasFlowers && this.flowers != null) ? this.flowers : this.tree;
    }

    private Identifier getConfiguredMegaFeatureKey(RandomSource random) {
        return (this.secondaryMegaTree != null && random.nextFloat() < this.secondaryChance) ? this.secondaryMegaTree : this.megaTree;
    }

    /**
     * @param treeId {@link ResourceTreeAccessManagement#ResourceTreeMap} 键，
     *               用于注入该资源树的原木/树叶
     */
    public boolean growTree(String treeId,
                            ServerLevel level,
                            ChunkGenerator generator,
                            BlockPos pos,
                            BlockState state,
                            RandomSource random) {
        Identifier megaFeatureKey = this.getConfiguredMegaFeatureKey(random);
        if (megaFeatureKey != null && ResourceTreeFeatures.isAvailable(megaFeatureKey)) {
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

                        boolean growSuccess = configuredMegaFeature.place(level, generator, random, pos.offset(i, 0, j));

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

        Identifier featureKey = this.getConfiguredFeatureKey(random, this.hasFlowers(level, pos));
        if (featureKey == null || !ResourceTreeFeatures.isAvailable(featureKey)) {
            return false;
        }

        ConfiguredFeature<?, ?> configuredFeature = ResourceTreeFeatures.getResourceTreeConfiguredFeature(treeId, featureKey);

        BlockGrowFeatureEvent normalEvent = EventHooks.fireBlockGrowFeature(level, random, pos, null);
        if (normalEvent.isCanceled()) return false;

        BlockPos normalBasePos = pos.below();
        BlockState normalOriginalState = level.getBlockState(normalBasePos);

        BlockState fluidState = level.getFluidState(pos).createLegacyBlock();
        level.setBlock(pos, fluidState, 4);

        boolean normalGrowSuccess = configuredFeature.place(level, generator, random, pos);
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
            level.setBlock(entry.getKey(), entry.getValue(), 3);
        }
    }

    private static boolean isTwoByTwoSapling(BlockState state, BlockGetter level, BlockPos pos, int xOffset, int yOffset) {
        Block block = state.getBlock();
        return level.getBlockState(pos.offset(xOffset, 0, yOffset)).is(block) && level.getBlockState(pos.offset(xOffset + 1, 0, yOffset)).is(block) && level.getBlockState(pos.offset(xOffset, 0, yOffset + 1)).is(block) && level.getBlockState(pos.offset(xOffset + 1, 0, yOffset + 1)).is(block);
    }

    private boolean hasFlowers(LevelAccessor level, BlockPos pos) {
        for (BlockPos blockpos : BlockPos.MutableBlockPos.betweenClosed(
                pos.below().north(2).west(2), pos.above().south(2).east(2))) {
            if (level.getBlockState(blockpos).is(BlockTags.FLOWERS)) return true;
        }
        return false;
    }
}
