package com.resource_farm.common.block.ResourceTree;

import com.resource_farm.api.ResourceTree.ResourceTreeType;
import com.resource_farm.api.block.ColoringSettings;
import com.resource_farm.api.block.FertilizeSettings;
import com.resource_farm.api.block.LightEmittingBlock;
import com.resource_farm.api.block.TintableBlock;
import com.resource_farm.common.block.grower.ResourceTreeGrower;
import com.resource_farm.data.ResourceFarmBlocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.Lazy;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.annotation.Nullable;

public class ResourceSaplingBlock extends SaplingBlock implements TintableBlock, LightEmittingBlock {

    private final String treeId;
    private final Lazy<String> translateKey;
    private final ResourceTreeType treeType;

    protected final ResourceTreeGrower resourceTreeGrower;

    private final FertilizeSettings fertilizeSetting;
    public final int growthFrequency;

    private final Lazy<Block> customPlaceBlock;
    @Nullable
    private final TagKey<Block> customPlaceBlockTag;

    private final int lightLevel;

    private final ColoringSettings coloringSettings;

    public ResourceSaplingBlock(String treeId,
                                ResourceTreeGrower resourceTreeGrower,
                                Properties properties,
                                FertilizeSettings fertilizeSetting,
                                int growthFrequency,
                                Lazy<Block> customPlaceBlock,
                                @Nullable TagKey<Block> customPlaceBlockTag,
                                int lightLevel,
                                ColoringSettings coloringSettings) {
        super(null, LightEmittingBlock.applyLightLevel(properties, lightLevel));
        this.treeId = treeId;
        this.translateKey = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
        this.resourceTreeGrower = resourceTreeGrower;
        this.fertilizeSetting = fertilizeSetting;
        this.growthFrequency = growthFrequency;
        this.customPlaceBlock = customPlaceBlock;
        this.customPlaceBlockTag = customPlaceBlockTag;
        this.lightLevel = lightLevel;
        this.coloringSettings = coloringSettings;
    }

    public static ResourceSaplingBlock create(String treeId, ResourceTreeGrower treeGrower, Properties properties,
                                              FertilizeSettings fertilizeSetting, int growthFrequency,
                                              Lazy<Block> customPlaceBlock, TagKey<Block> customPlaceBlockTag,
                                              int lightLevel, ColoringSettings coloringSettings) {
        return new ResourceSaplingBlock(treeId, treeGrower, properties, fertilizeSetting, growthFrequency, customPlaceBlock,
                customPlaceBlockTag, lightLevel, coloringSettings);
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
    protected boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        if (customPlaceBlock == null && customPlaceBlockTag == null) return super.mayPlaceOn(state, level, pos);
        return (customPlaceBlock != null && state.is(customPlaceBlock.get())) ||
                (customPlaceBlockTag != null && state.is(customPlaceBlockTag));
    }

    @Override
    protected void randomTick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos,
                              @NotNull RandomSource random) {
        if (level.isAreaLoaded(pos, 1)) {
            if (random.nextInt(growthFrequency) == 0) this.advanceTree(level, pos, state, random);
        }
    }

    @Override
    public void advanceTree(@NotNull ServerLevel level, @NotNull BlockPos pos, BlockState state, @NotNull RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            level.setBlock(pos, state.cycle(STAGE), 4);
        } else {
            this.resourceTreeGrower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random);
        }
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, @NotNull BlockState state, @NotNull Level level,
                                                       @NotNull BlockPos pos, @NotNull Player player,
                                                       @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hitResult) {
        Item heldItem = stack.getItem();
        double successChance;

        if (heldItem == fertilizeSetting.mainRipeningItem().get()) {
            successChance = fertilizeSetting.mainChance();
        } else if (heldItem == fertilizeSetting.secondaryRipeningItem().get()) {
            successChance = fertilizeSetting.secondaryChance();
        } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            RandomSource random = serverLevel.getRandom();
            if (random.nextDouble() < successChance) {
                this.advanceTree(serverLevel, pos, state, random);
                if (!player.isCreative()) stack.shrink(1);
                level.levelEvent(player, 2005, pos, 0);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.saplingTranslateKey(), Component.translatable(translateKey.get()));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if (customPlaceBlock.get() != Blocks.BARRIER) {
            tooltipComponents
                    .add(Component.translatable("tooltip.resource_farm.sapling.placed_on_block",
                            customPlaceBlock.get().getName().withStyle(ChatFormatting.YELLOW)));
        }
        if (customPlaceBlockTag != null) {
            ResourceLocation tagRL = customPlaceBlockTag.location();
            tooltipComponents.add(Component.translatable("tooltip.resource_farm.sapling.placed_on_block_tag",
                    Component.translatable(String.format("tag.block.%s.%s", tagRL.getNamespace(), tagRL.getPath())).withStyle(ChatFormatting.YELLOW)));
        }

        if (fertilizeSetting != null) {
            Item mainItem = fertilizeSetting.mainRipeningItem().get();
            Item secondaryItem = fertilizeSetting.secondaryRipeningItem().get();
            if (mainItem != Items.BARRIER) {
                Component mainItemName = mainItem.getName(ItemStack.EMPTY).copy().withStyle(ChatFormatting.GREEN);
                if (secondaryItem != Items.BARRIER && secondaryItem != mainItem) {
                    Component secondaryItemName = secondaryItem.getName(ItemStack.EMPTY).copy().withStyle(ChatFormatting.GREEN);
                    tooltipComponents.add(Component.translatable("tooltip.resource_farm.sapling.fertilize_2", mainItemName, secondaryItemName));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.resource_farm.sapling.fertilize_1", mainItemName));
                }
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
