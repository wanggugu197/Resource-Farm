package com.resource_farm.common.block.ResourceTree;

import com.resource_farm.api.ResourceTree.ResourceTreeType;
import com.resource_farm.api.block.ColoringSettings;
import com.resource_farm.common.block.ColoringBlock;
import com.resource_farm.data.ResourceFarmBlocks;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.common.util.Lazy;

import org.jetbrains.annotations.NotNull;

public class ResourceStrippedWoodBlock extends ColoringBlock {

    private final String treeId;
    private final Lazy<String> translateKey;
    private final ResourceTreeType treeType;

    public ResourceStrippedWoodBlock(String treeId,
                                     Properties properties,
                                     int lightLevel,
                                     ColoringSettings coloringSettings) {
        super(properties, lightLevel, coloringSettings);
        this.treeId = treeId;
        this.translateKey = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
    }

    public static ResourceStrippedWoodBlock create(String treeId,
                                                   Properties properties,
                                                   int lightLevel,
                                                   ColoringSettings coloringSettings) {
        return new ResourceStrippedWoodBlock(treeId, properties, lightLevel, coloringSettings);
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.strippedWoodTranslateKey(), Component.translatable(translateKey.get()));
    }
}
