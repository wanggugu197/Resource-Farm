package com.maple.resource_farm.common.block.ResourceTree;

import com.maple.resource_farm.api.ResourceTree.ResourceTreeType;
import com.maple.resource_farm.api.block.ColoringSettings;
import com.maple.resource_farm.api.block.FertilizeSettings;
import com.maple.resource_farm.api.block.LightEmittingBlock;
import com.maple.resource_farm.api.block.TintableBlock;
import com.maple.resource_farm.common.block.grower.ResourceTreeGrower;
import com.maple.resource_farm.data.ResourceFarmBlocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

import java.util.function.Consumer;

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
        if (customPlaceBlock.get() == Blocks.BARRIER && customPlaceBlockTag == null) return super.mayPlaceOn(state, level, pos);
        return (customPlaceBlock.get() != Blocks.BARRIER && state.is(customPlaceBlock.get())) ||
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
    protected @NotNull InteractionResult useItemOn(ItemStack stack, @NotNull BlockState state, @NotNull Level level,
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
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;
            RandomSource random = serverLevel.getRandom();
            if (random.nextDouble() < successChance) {
                this.advanceTree(serverLevel, pos, state, random);
                if (!player.isCreative()) stack.shrink(1);
                level.levelEvent(player, 2005, pos, 0);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.saplingTranslateKey(), Component.translatable(translateKey.get()));
    }

    /**
     * 26.1 起方块不再提供 appendHoverText。
     */
    public void appendSaplingTooltip(@NotNull Consumer<Component> tooltipComponents) {
        if (customPlaceBlock.get() != Blocks.BARRIER) {
            tooltipComponents.accept(Component.translatable("tooltip.resource_farm.sapling.placed_on_block",
                    customPlaceBlock.get().getName().copy().withStyle(ChatFormatting.YELLOW)));
        }
        if (customPlaceBlockTag != null) {
            Identifier tagRL = customPlaceBlockTag.location();
            tooltipComponents.accept(Component.translatable("tooltip.resource_farm.sapling.placed_on_block_tag",
                    Component.translatable(String.format("tag.block.%s.%s", tagRL.getNamespace(), tagRL.getPath()))
                            .withStyle(ChatFormatting.YELLOW)));
        }

        appendFertilizeTooltip(tooltipComponents);
    }

    /**
     * 显示催熟物品名称。
     * <p>
     * 26.1 中 {@link Item#getName(ItemStack)} 仅读取栈上 {@code DataComponents.ITEM_NAME}，
     * 对 {@link ItemStack#EMPTY} 会返回 {@link net.minecraft.network.chat.CommonComponents#EMPTY}，
     * 导致提示里催熟物品名称为空白。应使用 {@link Item#getDescriptionId()} 翻译键。
     */
    private void appendFertilizeTooltip(@NotNull Consumer<Component> tooltipComponents) {
        if (fertilizeSetting == null || fertilizeSetting == FertilizeSettings.NULL) {
            return;
        }
        Lazy<Item> mainLazy = fertilizeSetting.mainRipeningItem();
        Lazy<Item> secondaryLazy = fertilizeSetting.secondaryRipeningItem();
        if (mainLazy == null) {
            return;
        }

        Item mainItem = mainLazy.get();
        if (mainItem == null || mainItem == Items.BARRIER || mainItem == Items.AIR) {
            return;
        }

        Component mainItemName = itemDisplayName(mainItem).copy().withStyle(ChatFormatting.GREEN);
        Item secondaryItem = secondaryLazy != null ? secondaryLazy.get() : null;
        if (secondaryItem != null && secondaryItem != Items.BARRIER && secondaryItem != Items.AIR && secondaryItem != mainItem) {
            Component secondaryItemName = itemDisplayName(secondaryItem).copy().withStyle(ChatFormatting.GREEN);
            tooltipComponents.accept(Component.translatable(
                    "tooltip.resource_farm.sapling.fertilize_2", mainItemName, secondaryItemName));
        } else {
            tooltipComponents.accept(Component.translatable(
                    "tooltip.resource_farm.sapling.fertilize_1", mainItemName));
        }
    }

    /** 安全获取物品显示名（不依赖 ItemStack 组件） */
    private static Component itemDisplayName(Item item) {
        return Component.translatable(item.getDescriptionId());
    }
}
