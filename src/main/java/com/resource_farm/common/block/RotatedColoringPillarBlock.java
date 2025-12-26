package com.resource_farm.common.block;

import com.resource_farm.api.block.ColoringSettings;
import com.resource_farm.api.block.LightEmittingBlock;
import com.resource_farm.api.block.TintableBlock;

import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class RotatedColoringPillarBlock extends RotatedPillarBlock implements LightEmittingBlock, TintableBlock {

    private final int lightLevel;
    private final ColoringSettings coloringSettings;

    public RotatedColoringPillarBlock(Properties properties,
                                      int lightLevel,
                                      ColoringSettings coloringSettings) {
        super(LightEmittingBlock.applyLightLevel(properties, lightLevel));
        this.lightLevel = lightLevel;
        this.coloringSettings = coloringSettings;
    }

    public static RotatedColoringPillarBlock create(BlockBehaviour.Properties properties,
                                                    int lightLevel, ColoringSettings coloringSettings) {
        return new RotatedColoringPillarBlock(properties, lightLevel, coloringSettings);
    }

    @Override
    public int getLightLevel() {
        return lightLevel;
    }

    @Override
    public boolean[] getTintLayers() {
        return coloringSettings.tintLayers();
    }

    @Override
    public int[] getColors() {
        return coloringSettings.colors();
    }
}
