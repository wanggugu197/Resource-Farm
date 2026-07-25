package com.maple.resource_farm.common.block.ResourceTree;

import com.maple.resource_farm.api.ResourceTree.ResourceTreeType;
import com.maple.resource_farm.data.ResourceFarmBlocks;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.util.Lazy;

import org.jetbrains.annotations.NotNull;

public class ResourceStrippedWoodBlock extends Block {

    private final Lazy<String> translateKey;
    private final ResourceTreeType treeType;

    public ResourceStrippedWoodBlock(String treeId, Properties properties) {
        super(properties);
        this.translateKey = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
    }

    public static ResourceStrippedWoodBlock create(String treeId, Properties properties) {
        return new ResourceStrippedWoodBlock(treeId, properties);
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.strippedWoodTranslateKey(), Component.translatable(translateKey.get()));
    }
}
