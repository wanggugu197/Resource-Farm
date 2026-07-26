package com.maple.resource_farm.resourceTree.builder;

import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeBaseType;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.world.level.FoliageColor;

public class TreeBuilderClientHelper {

    /** No-op / white tint (faces without tintindex ignore this layer). */
    private static final BlockTintSource BLANK_TINT = BlockTintSources.constant(-1);

    static BlockTintSource[] overlayOnlyLayers(int color) {
        BlockTintSource overlayTint = BlockTintSources.constant(color);
        return new BlockTintSource[] { BLANK_TINT, overlayTint };
    }

    static BlockTintSource[] leavesLayers(int color, ResourceTreeBaseType treeType) {
        BlockTintSource overlayTint = BlockTintSources.constant(color);
        return new BlockTintSource[] { leavesBaseTint(treeType), overlayTint };
    }

    /**
     * Layer 0 tint for leaves base models (matches vanilla leaf block colors / item tints).
     * Cherry and pale oak textures are pre-colored — blank layer.
     */
    static BlockTintSource leavesBaseTint(ResourceTreeBaseType treeType) {
        if (treeType == null || treeType.type() == null) {
            return BlockTintSources.foliage();
        }
        return switch (treeType.type()) {
            case "birch" -> BlockTintSources.constant(FoliageColor.FOLIAGE_BIRCH);
            case "spruce" -> BlockTintSources.constant(FoliageColor.FOLIAGE_EVERGREEN);
            case "mangrove" -> BlockTintSources.constant(FoliageColor.FOLIAGE_MANGROVE);
            case "cherry", "pale_oak" -> BlockTintSources.constant(-1);
            default -> BlockTintSources.foliage(); // oak / jungle / acacia / dark_oak
        };
    }
}
