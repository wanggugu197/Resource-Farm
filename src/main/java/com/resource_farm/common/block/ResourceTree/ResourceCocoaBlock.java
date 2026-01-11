package com.resource_farm.common.block.ResourceTree;

import com.resource_farm.api.ResourceTree.ResourceTreeType;
import com.resource_farm.api.block.ColoringSettings;
import com.resource_farm.api.block.FertilizeSettings;
import com.resource_farm.api.block.LightEmittingBlock;
import com.resource_farm.api.block.TintableBlock;
import com.resource_farm.data.ResourceFarmBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.Lazy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourceCocoaBlock extends CocoaBlock implements TintableBlock, LightEmittingBlock {

    private final String treeId;
    private final Lazy<String> translateKey;
    private final ResourceTreeType treeType;

    private static final int GROWTH_CHANCE_DENOMINATOR = 10;

    private final FertilizeSettings fertilizeSetting;

    @Nullable
    private final Block customAttachBlock;
    @Nullable
    private final TagKey<Block> customAttachBlockTag;

    private final int lightLevel;
    private final ColoringSettings coloringSettings;

    protected static final VoxelShape[] EAST_AABB = new VoxelShape[] {
            Block.box(11.0D, 7.0D, 6.0D, 15.0D, 12.0D, 10.0D),
            Block.box(9.0D, 5.0D, 5.0D, 15.0D, 12.0D, 11.0D),
            Block.box(7.0D, 3.0D, 4.0D, 15.0D, 12.0D, 12.0D)
    };
    protected static final VoxelShape[] WEST_AABB = new VoxelShape[] {
            Block.box(1.0D, 7.0D, 6.0D, 5.0D, 12.0D, 10.0D),
            Block.box(1.0D, 5.0D, 5.0D, 7.0D, 12.0D, 11.0D),
            Block.box(1.0D, 3.0D, 4.0D, 9.0D, 12.0D, 12.0D)
    };
    protected static final VoxelShape[] NORTH_AABB = new VoxelShape[] {
            Block.box(6.0D, 7.0D, 1.0D, 10.0D, 12.0D, 5.0D),
            Block.box(5.0D, 5.0D, 1.0D, 11.0D, 12.0D, 7.0D),
            Block.box(4.0D, 3.0D, 1.0D, 12.0D, 12.0D, 9.0D)
    };
    protected static final VoxelShape[] SOUTH_AABB = new VoxelShape[] {
            Block.box(6.0D, 7.0D, 11.0D, 10.0D, 12.0D, 15.0D),
            Block.box(5.0D, 5.0D, 9.0D, 11.0D, 12.0D, 15.0D),
            Block.box(4.0D, 3.0D, 7.0D, 12.0D, 12.0D, 15.0D)
    };

    public ResourceCocoaBlock(String treeId,
                              Properties properties,
                              FertilizeSettings fertilizeSetting,
                              @Nullable Block customAttachBlock,
                              @Nullable TagKey<Block> customAttachBlockTag,
                              int lightLevel, ColoringSettings coloringSettings) {
        super(LightEmittingBlock.applyLightLevel(properties, lightLevel));
        this.treeId = treeId;
        this.translateKey = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
        this.fertilizeSetting = fertilizeSetting;
        this.customAttachBlock = customAttachBlock;
        this.customAttachBlockTag = customAttachBlockTag;
        this.lightLevel = lightLevel;
        this.coloringSettings = coloringSettings;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(AGE, 0));
    }

    public static ResourceCocoaBlock create(String treeId,
                                            Properties properties,
                                            @Nullable Block customAttachBlock,
                                            @Nullable TagKey<Block> customAttachBlockTag,
                                            int lightLevel, ColoringSettings coloringSettings) {
        return new ResourceCocoaBlock(treeId, properties, FertilizeSettings.DEFAULT,
                customAttachBlock, customAttachBlockTag, lightLevel, coloringSettings);
    }

    @Override
    public boolean[] getTintLayers() {
        return coloringSettings.tintLayers();
    }

    @Override
    public int[] getColors() {
        return coloringSettings.colors();
    }

    @Override
    public int getLightLevel() {
        return lightLevel;
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos,
                                     @NotNull BlockState state) {
        return false;
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos,
                                @NotNull BlockState state) {}

    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        if (customAttachBlock == null && customAttachBlockTag == null) {
            return super.canSurvive(state, level, pos);
        }
        BlockState attachState = level.getBlockState(pos.relative(state.getValue(FACING)));
        return (customAttachBlock != null && attachState.is(customAttachBlock)) ||
                (customAttachBlockTag != null && attachState.is(customAttachBlockTag));
    }

    @Override
    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos,
                              @NotNull RandomSource random) {
        int currentAge = state.getValue(AGE);
        if (currentAge < 2 && level.isAreaLoaded(pos, 1) && random.nextInt(GROWTH_CHANCE_DENOMINATOR) == 0) {
            level.setBlock(pos, state.setValue(AGE, currentAge + 1), 2);
        }
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, @NotNull BlockState state, @NotNull Level level,
                                                       @NotNull BlockPos pos, @NotNull Player player,
                                                       @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hitResult) {
        Item heldItem = stack.getItem();
        double successChance;

        if (fertilizeSetting.mainRipeningItem() != null && heldItem == fertilizeSetting.mainRipeningItem().get()) {
            successChance = fertilizeSetting.mainChance();
        } else if (fertilizeSetting.secondaryRipeningItem() != null && heldItem == fertilizeSetting.secondaryRipeningItem().get()) {
            successChance = fertilizeSetting.secondaryChance();
        } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            RandomSource random = serverLevel.getRandom();
            int currentAge = state.getValue(AGE);
            if (currentAge < 2 && random.nextDouble() < successChance) {
                level.setBlock(pos, state.setValue(AGE, currentAge + 1), 2);
                if (!player.isCreative()) stack.shrink(1);
                level.levelEvent(player, 2005, pos, 0);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                                           @NotNull CollisionContext context) {
        int age = state.getValue(AGE);
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH_AABB[age];
            case WEST -> WEST_AABB[age];
            case EAST -> EAST_AABB[age];
            default -> NORTH_AABB[age];
        };
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.fruitTranslateKey(), Component.translatable(translateKey.get()));
    }
}
