package com.maple.resource_farm.common.block.ResourceTree;

import com.maple.resource_farm.api.ResourceTree.ResourceTreeType;
import com.maple.resource_farm.api.block.ColoringSettings;
import com.maple.resource_farm.api.block.LightEmittingBlock;
import com.maple.resource_farm.api.block.TintableBlock;
import com.maple.resource_farm.data.ResourceFarmBlocks;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.util.Lazy;

import org.jetbrains.annotations.NotNull;

public class ResourceLeavesBlock extends UntintedParticleLeavesBlock implements LightEmittingBlock, TintableBlock {

    private final String treeId;
    private final Lazy<String> translateKey;
    private final ResourceTreeType treeType;

    private final int lightLevel;
    private final ColoringSettings coloringSettings;

    public ResourceLeavesBlock(String treeId, BlockBehaviour.Properties properties,
                               int lightLevel, ColoringSettings coloringSettings) {
        super(0.01F, ParticleTypes.CHERRY_LEAVES, LightEmittingBlock.applyLightLevel(properties, lightLevel));
        this.treeId = treeId;
        this.translateKey = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
        this.lightLevel = lightLevel;
        this.coloringSettings = coloringSettings;
    }

    public static ResourceLeavesBlock create(String treeId, Properties properties,
                                             int lightLevel, ColoringSettings coloringSettings) {
        return new ResourceLeavesBlock(treeId, properties, lightLevel, coloringSettings);
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

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.leavesTranslateKey(), Component.translatable(translateKey.get()));
    }
}
