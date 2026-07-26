package com.maple.resource_farm.resourceTree.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

/**
 * 资源树方块物品。
 */
public class ResourceBlockItem extends BlockItem {

    public ResourceBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return getBlock().getName();
    }
}
