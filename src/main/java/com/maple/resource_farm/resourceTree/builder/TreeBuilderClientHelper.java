package com.maple.resource_farm.resourceTree.builder;

import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeBaseType;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.FoliageColor;

public class TreeBuilderClientHelper {

    /** No-op / white tint (faces without tint index ignore this layer). */
    private static final BlockColor BLANK_TINT = constant(-1);

    static BlockColor[] overlayOnlyLayers(int color) {
        BlockColor overlayTint = constant(color);
        return new BlockColor[] { BLANK_TINT, overlayTint };
    }

    static BlockColor[] leavesLayers(int color, ResourceTreeBaseType treeType) {
        BlockColor overlayTint = constant(color);
        return new BlockColor[] { leavesBaseTint(treeType), overlayTint };
    }

    /**
     * Layer 0 tint for leaves base models (matches vanilla leaf block colors / item tints).
     * Cherry and pale oak textures are pre-colored — blank layer.
     */
    static BlockColor leavesBaseTint(ResourceTreeBaseType treeType) {
        if (treeType == null || treeType.type() == null) {
            return constant(FoliageColor.getDefaultColor());
        }
        return switch (treeType.type()) {
            case "birch" -> constant(FoliageColor.getBirchColor());
            case "spruce" -> constant(FoliageColor.getEvergreenColor());
            case "mangrove" -> constant(FoliageColor.getMangroveColor());
            case "cherry", "pale_oak" -> constant(-1);
            default -> constant(FoliageColor.getDefaultColor()); // oak / jungle / acacia / dark_oak
        };
    }

    private static BlockColor constant(int color) {
        return (state, level, pos, tintIndex) -> color;
    }

    /**
     * registrylib 的 {@code blockTintSource(Supplier<Supplier<BlockColor[]>>)} 会把数组里的每个
     * BlockColor 逐个 {@code event.register} 到同一方块，而 1.21.1 的 BlockColors.register 是
     * put 覆盖——最终只有最后一个生效，导致所有 tintindex 恒染同一色（无法分层）。
     * <p>
     * 因此包装为<strong>单个</strong> BlockColor（数组长度 1），内部按 tintindex 分派到各层颜色；
     * tintindex 越界返回 -1（不着色，保持纹理原色）。
     */
    static BlockColor[] layered(BlockColor... layers) {
        BlockColor wrapped = (state, level, pos, tintIndex) -> tintIndex >= 0 && tintIndex < layers.length ? layers[tintIndex].getColor(state, level, pos, tintIndex) : -1;
        return new BlockColor[] { wrapped };
    }
}
