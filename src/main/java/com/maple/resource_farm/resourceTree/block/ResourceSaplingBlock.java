package com.maple.resource_farm.resourceTree.block;

import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeBaseType;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeFertilizeSettings;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeGrower;

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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.Lazy;

import com.gto.registrylib.tooltip.SubNode;
import com.gto.registrylib.tooltip.TooltipNodeCollector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ResourceSaplingBlock extends SaplingBlock {

    private final String treeId;
    private final Lazy<String> translateKey;
    private final ResourceTreeBaseType treeType;

    /** 共享生长器（来自 ResourceFarmMaps.GROWERS）；具体方块由 treeId 在生长时注入。 */
    protected final ResourceTreeGrower resourceTreeGrower;

    private final ResourceTreeFertilizeSettings fertilizeSetting;
    public final int growthFrequency;

    private final Lazy<Block> customPlaceBlock;
    @Nullable
    private final TagKey<Block> customPlaceBlockTag;

    public ResourceSaplingBlock(String treeId,
                                ResourceTreeGrower resourceTreeGrower,
                                Properties properties,
                                ResourceTreeFertilizeSettings fertilizeSetting,
                                int growthFrequency,
                                Lazy<Block> customPlaceBlock,
                                @Nullable TagKey<Block> customPlaceBlockTag) {
        super(null, properties);
        this.treeId = treeId;
        this.translateKey = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
        this.resourceTreeGrower = resourceTreeGrower;
        this.fertilizeSetting = fertilizeSetting;
        this.growthFrequency = growthFrequency;
        this.customPlaceBlock = customPlaceBlock;
        this.customPlaceBlockTag = customPlaceBlockTag;
    }

    public static ResourceSaplingBlock create(String treeId, ResourceTreeGrower treeGrower, Properties properties,
                                              ResourceTreeFertilizeSettings fertilizeSetting, int growthFrequency,
                                              Lazy<Block> customPlaceBlock, TagKey<Block> customPlaceBlockTag) {
        return new ResourceSaplingBlock(treeId, treeGrower, properties, fertilizeSetting, growthFrequency, customPlaceBlock,
                customPlaceBlockTag);
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
            this.resourceTreeGrower.growTree(
                    this.treeId, level, level.getChunkSource().getGenerator(), pos, state, random);
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

        if (!level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;
            RandomSource random = serverLevel.getRandom();
            if (random.nextDouble() < successChance) {
                this.advanceTree(serverLevel, pos, state, random);
                if (!player.isCreative()) stack.shrink(1);
                level.levelEvent(player, 2005, pos, 0);
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.saplingTranslateKey(), Component.translatable(translateKey.get()));
    }

    /**
     * 写入树苗物品 tooltip（RegistryLib {@code addTooltip} / {@link TooltipNodeCollector}）。
     * <p>
     * 催熟物名称用 {@link Item#getDescriptionId()}，避免 26.1 下
     * {@code Item.getName(EMPTY)} 读 {@code ITEM_NAME} 得到空组件。
     */
    public void appendSaplingTooltip(@NotNull TooltipNodeCollector collector) {
        if (customPlaceBlock.get() != Blocks.BARRIER) {
            collector.node(new SubNode.Basic(Component.translatable(
                    "tooltip.resource_farm.sapling.placed_on_block",
                    customPlaceBlock.get().getName().copy().withStyle(ChatFormatting.YELLOW))));
        }
        if (customPlaceBlockTag != null) {
            ResourceLocation tagRL = customPlaceBlockTag.location();
            collector.node(new SubNode.Basic(Component.translatable(
                    "tooltip.resource_farm.sapling.placed_on_block_tag",
                    Component.translatable(String.format("tag.block.%s.%s", tagRL.getNamespace(), tagRL.getPath()))
                            .withStyle(ChatFormatting.YELLOW))));
        }

        if (fertilizeSetting == null || fertilizeSetting == ResourceTreeFertilizeSettings.NULL) return;
        Lazy<Item> mainLazy = fertilizeSetting.mainRipeningItem();
        Lazy<Item> secondaryLazy = fertilizeSetting.secondaryRipeningItem();
        if (mainLazy == null) return;
        Item mainItem = mainLazy.get();
        if (mainItem == Items.BARRIER || mainItem == Items.AIR) return;
        Component mainItemName = itemDisplayName(mainItem).copy().withStyle(ChatFormatting.GREEN);
        Item secondaryItem = secondaryLazy != null ? secondaryLazy.get() : null;
        if (secondaryItem != null && secondaryItem != Items.BARRIER && secondaryItem != Items.AIR && secondaryItem != mainItem) {
            Component secondaryItemName = itemDisplayName(secondaryItem).copy().withStyle(ChatFormatting.GREEN);
            collector.node(new SubNode.Basic(Component.translatable("tooltip.resource_farm.sapling.fertilize_2", mainItemName, secondaryItemName)));
        } else {
            collector.node(new SubNode.Basic(Component.translatable("tooltip.resource_farm.sapling.fertilize_1", mainItemName)));
        }
    }

    private static Component itemDisplayName(Item item) {
        return Component.translatable(item.getDescriptionId());
    }
}
