package com.resource_farm.common.block;

import com.resource_farm.api.block.ColoringSettings;
import com.resource_farm.api.block.LightEmittingBlock;
import com.resource_farm.api.block.TintableBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ColoringBlock extends Block implements TintableBlock, LightEmittingBlock {

    private final int lightLevel;
    private final ColoringSettings coloringSettings;

    public ColoringBlock(BlockBehaviour.Properties properties, int lightLevel, ColoringSettings coloringSettings) {
        super(LightEmittingBlock.applyLightLevel(properties, lightLevel));
        this.lightLevel = lightLevel;
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

    @Override
    public int getLightLevel() {
        return lightLevel;
    }

    public static ColoringBlock create(BlockBehaviour.Properties properties, int lightLevel, ColoringSettings coloringSettings) {
        return new ColoringBlock(properties, lightLevel, coloringSettings);
    }
}
