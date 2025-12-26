package com.resource_farm.api.block;

import net.minecraft.client.color.block.BlockColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 可着色方块的核心接口
 * 定义：支持多图层着色的方块需实现的规范
 */
public interface TintableBlock {

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
     * 客户端方块着色器（所有实现类复用该逻辑）
     */
    @OnlyIn(Dist.CLIENT)
    static BlockColor tintColor() {
        return (state, level, pos, index) -> {
            if (!(state.getBlock() instanceof TintableBlock tintableBlock)) return -1;
            if (index < 0 || index >= tintableBlock.getTintLayers().length) return -1;
            return tintableBlock.getTintLayers()[index] ? tintableBlock.getColors()[index] : -1;
        };
    }
}
