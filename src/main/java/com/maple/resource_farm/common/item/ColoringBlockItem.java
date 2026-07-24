package com.maple.resource_farm.common.item;

import com.maple.resource_farm.api.block.ColoringSettings;
import com.maple.resource_farm.api.item.TintableItem;
import com.maple.resource_farm.common.block.ResourceTree.ResourceSaplingBlock;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ColoringBlockItem extends BlockItem implements TintableItem {

    private final ColoringSettings coloringSettings;

    protected ColoringBlockItem(Block block, Properties properties, ColoringSettings coloringSettings) {
        super(block, properties);
        this.coloringSettings = coloringSettings;
    }

    @Override
    public boolean[] getTintLayers() {
        return coloringSettings.tintLayers();
    }

    @Override
    public int[] getColors() {
        return coloringSettings.colors();
    }

    public static ColoringBlockItem create(Block block, Properties properties, ColoringSettings coloringSettings) {
        return new ColoringBlockItem(block, properties, coloringSettings);
    }

    @Override
    public @NotNull MutableComponent getName(@NotNull ItemStack stack) {
        return getBlock().getName();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltipComponents,
                                @NotNull TooltipFlag tooltipFlag) {
        if (getBlock() instanceof ResourceSaplingBlock sapling) {
            sapling.appendSaplingTooltip(tooltipComponents);
        }
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }
}
