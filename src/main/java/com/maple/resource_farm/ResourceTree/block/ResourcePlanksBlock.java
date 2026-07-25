package com.maple.resource_farm.ResourceTree.block;

import com.maple.resource_farm.ResourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.ResourceTree.data.dataMaps.ResourceTreeBaseType;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.util.Lazy;

import org.jetbrains.annotations.NotNull;

public class ResourcePlanksBlock extends Block {

    private final Lazy<String> translateKey;
    private final ResourceTreeBaseType treeType;

    public ResourcePlanksBlock(String treeId, Properties properties) {
        super(properties);
        this.translateKey = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
    }

    public static ResourcePlanksBlock create(String treeId, Properties properties) {
        return new ResourcePlanksBlock(treeId, properties);
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.planksTranslateKey(), Component.translatable(translateKey.get()));
    }
}
