package com.resource_farm.common.item.ResourceTree;

import com.resource_farm.api.ResourceTree.ResourceTreeType;
import com.resource_farm.api.block.ColoringSettings;
import com.resource_farm.common.item.ColoringItem;
import com.resource_farm.data.ResourceFarmBlocks;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;

import org.jetbrains.annotations.NotNull;

public class ResourceClumpItem extends ColoringItem {

    private final String treeId;
    private final Lazy<String> translateKey;
    private final ResourceTreeType treeType;

    protected ResourceClumpItem(String treeId,
                                Properties properties,
                                ColoringSettings coloringSettings) {
        super(properties, coloringSettings);
        this.treeId = treeId;
        this.translateKey = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
    }

    public static ResourceClumpItem create(String treeId,
                                           Properties properties,
                                           ColoringSettings coloringSettings) {
        return new ResourceClumpItem(treeId, properties, coloringSettings);
    }

    @Override
    public @NotNull MutableComponent getName(@NotNull ItemStack stack) {
        return Component.translatable(treeType.clumpTranslateKey(), Component.translatable(translateKey.get()));
    }
}
