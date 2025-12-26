package com.resource_farm.common.item;

import com.resource_farm.api.block.ColoringSettings;
import com.resource_farm.api.item.TintableItem;

import net.minecraft.world.item.Item;

public class ColoringItem extends Item implements TintableItem {

    private final ColoringSettings coloringSettings;

    protected ColoringItem(Properties properties, ColoringSettings coloringSettings) {
        super(properties);
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

    /** 便捷创建方法：自定义多图层+多颜色 */
    public static ColoringItem create(Properties properties, ColoringSettings coloringSettings) {
        return new ColoringItem(properties, coloringSettings);
    }
}
