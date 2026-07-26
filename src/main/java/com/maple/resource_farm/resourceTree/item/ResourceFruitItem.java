package com.maple.resource_farm.resourceTree.item;

import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeBaseType;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;

import org.jetbrains.annotations.NotNull;

/**
 * 资源树果实物品。
 */
public class ResourceFruitItem extends Item {

    private final Lazy<String> translateKey;
    private final ResourceTreeBaseType treeType;

    protected ResourceFruitItem(String treeId, Properties properties) {
        super(properties);
        this.translateKey = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
    }

    public static ResourceFruitItem create(String treeId, Properties properties) {
        return new ResourceFruitItem(treeId, properties);
    }

    @Override
    public @NotNull MutableComponent getName(@NotNull ItemStack stack) {
        return Component.translatable(treeType.fruitTranslateKey(), Component.translatable(translateKey.get()));
    }
}
