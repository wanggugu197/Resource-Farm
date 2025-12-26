package com.resource_farm.api;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

/**
 * 物品信息持有者记录类，封装「物品/方块 + 整数值（权重/优先级等） + 数量提供者」三个核心参数
 */
public record ItemWeightCountHolder(
                                    ItemLike item,
                                    int number,
                                    NumberProvider countProvider) {

    public static ItemWeightCountHolder of(ItemLike item, int number, NumberProvider countProvider) {
        return new ItemWeightCountHolder(item, number, countProvider);
    }

    public static ItemWeightCountHolder of(ItemLike item, NumberProvider countProvider) {
        return new ItemWeightCountHolder(item, 1, countProvider);
    }
}
