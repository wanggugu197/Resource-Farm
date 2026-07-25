package com.maple.resource_farm.ResourceTree.item;

import com.maple.resource_farm.ResourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.ResourceTree.data.dataMaps.ResourceTreeBaseType;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;

import org.jetbrains.annotations.NotNull;

/**
 * 资源树碎块物品。
 */
public class ResourceClumpItem extends Item {

    private final Lazy<String> translateKey;
    private final ResourceTreeBaseType treeType;

    protected ResourceClumpItem(String treeId, Properties properties) {
        super(properties);
        this.translateKey = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
    }

    public static ResourceClumpItem create(String treeId, Properties properties) {
        return new ResourceClumpItem(treeId, properties);
    }

    @Override
    public @NotNull MutableComponent getName(@NotNull ItemStack stack) {
        return Component.translatable(treeType.clumpTranslateKey(), Component.translatable(translateKey.get()));
    }
}
