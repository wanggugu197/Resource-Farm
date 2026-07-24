package com.maple.resource_farm.api.block;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

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
     * 26.1+：按图层注册 {@link BlockTintSource} 列表（每层一个）。
     */
    static List<BlockTintSource> tintSources() {
        // 支持最多 8 个图层；未启用着色的层返回 -1（不染色）
        List<BlockTintSource> sources = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            final int layer = i;
            sources.add(new BlockTintSource() {

                @Override
                public int color(BlockState state) {
                    if (!(state.getBlock() instanceof TintableBlock tintableBlock)) {
                        return -1;
                    }
                    boolean[] layers = tintableBlock.getTintLayers();
                    int[] colors = tintableBlock.getColors();
                    if (layer < 0 || layer >= layers.length || layer >= colors.length) {
                        return -1;
                    }
                    return layers[layer] ? colors[layer] : -1;
                }
            });
        }
        return List.copyOf(sources);
    }
}
