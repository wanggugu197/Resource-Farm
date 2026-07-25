package com.maple.resource_farm.common.item.ResourceTree;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

/**
 * 资源树方块物品：名称走 {@link Block#getName()}。
 * <p>
 * 26.1 起 {@link net.minecraft.world.item.Item#getName(ItemStack)} 默认只读
 * {@code DataComponents.ITEM_NAME}（注册时的固定 descriptionId），不会调用方块上的
 * {@code getName()} 覆盖。此处恢复与旧 {@code ColoringBlockItem} 一致的行为。
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
