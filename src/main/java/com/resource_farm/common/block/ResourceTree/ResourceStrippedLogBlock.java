package com.resource_farm.common.block.ResourceTree;

import com.resource_farm.api.ResourceTree.ResourceTreeType;
import com.resource_farm.api.block.ColoringSettings;
import com.resource_farm.common.block.RotatedColoringPillarBlock;
import com.resource_farm.data.ResourceFarmBlocks;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import org.jetbrains.annotations.NotNull;

public class ResourceStrippedLogBlock extends RotatedColoringPillarBlock {

    private final String treeId;
    private final String translateKey;
    private final ResourceTreeType treeType;

    public ResourceStrippedLogBlock(String treeId,
                                    Properties properties,
                                    int lightLevel,
                                    ColoringSettings coloringSettings) {
        super(properties, lightLevel, coloringSettings);
        this.treeId = treeId;
        this.translateKey = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
    }

    public static ResourceStrippedLogBlock create(String treeId,
                                                  Properties properties,
                                                  int lightLevel,
                                                  ColoringSettings coloringSettings) {
        return new ResourceStrippedLogBlock(treeId, properties, lightLevel, coloringSettings);
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.strippedLogTranslateKey(), Component.translatable(translateKey));
    }
}
