package com.resource_farm.common.item;

import com.resource_farm.api.block.ColoringSettings;
import com.resource_farm.api.item.TintableItem;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

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
}
