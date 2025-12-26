package com.resource_farm.api.item;

import net.minecraft.client.color.item.ItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 可着色物品的核心接口
 * 定义：支持多图层着色的物品需实现的规范
 */
public interface TintableItem {

    /**
     * 获取标记哪些图层需要着色的数组（返回拷贝，避免外部修改内部状态）
     * 
     * @return boolean数组（索引=图层号，值=是否着色）
     */
    boolean[] getTintLayers();

    /**
     * 获取对应图层的着色颜色数组（返回拷贝，避免外部修改内部状态）
     * 
     * @return int数组（索引=图层号，值=对应颜色）
     */
    int[] getColors();

    /**
     * 客户端着色器（统一提供ItemColor实现，所有实现类复用该逻辑）
     */
    @OnlyIn(Dist.CLIENT)
    static ItemColor tintColor() {
        return (itemStack, index) -> {
            if (!(itemStack.getItem() instanceof TintableItem tintableItem)) return -1;
            if (index < 0 || index >= tintableItem.getTintLayers().length) return -1;
            return tintableItem.getTintLayers()[index] ? tintableItem.getColors()[index] : -1;
        };
    }
}
