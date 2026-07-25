package com.maple.resource_farm.common.item.ResourceTree;

import com.maple.resource_farm.api.ResourceTree.ResourceTreeType;
import com.maple.resource_farm.data.ResourceFarmBlocks;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;

import org.jetbrains.annotations.NotNull;

/**
 * 资源树树脂物品。着色由动态包多层 tinted 模型烘焙，无需运行时着色接口。
 */
public class ResourceResinItem extends Item {

    private final Lazy<String> translateKey;
    private final ResourceTreeType treeType;

    protected ResourceResinItem(String treeId, Properties properties) {
        super(properties);
        this.translateKey = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
    }

    public static ResourceResinItem create(String treeId, Properties properties) {
        return new ResourceResinItem(treeId, properties);
    }

    @Override
    public @NotNull MutableComponent getName(@NotNull ItemStack stack) {
        return Component.translatable(treeType.resinTranslateKey(), Component.translatable(translateKey.get()));
    }
}
