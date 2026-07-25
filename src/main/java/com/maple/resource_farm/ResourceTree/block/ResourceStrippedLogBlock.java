package com.maple.resource_farm.ResourceTree.block;

import com.maple.resource_farm.ResourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.ResourceTree.data.dataMaps.ResourceTreeBaseType;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.neoforged.neoforge.common.util.Lazy;

import org.jetbrains.annotations.NotNull;

public class ResourceStrippedLogBlock extends RotatedPillarBlock {

    private final Lazy<String> translateKey;
    private final ResourceTreeBaseType treeType;

    public ResourceStrippedLogBlock(String treeId, Properties properties) {
        super(properties);
        this.translateKey = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
    }

    public static ResourceStrippedLogBlock create(String treeId, Properties properties) {
        return new ResourceStrippedLogBlock(treeId, properties);
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.strippedLogTranslateKey(), Component.translatable(translateKey.get()));
    }
}
