package com.resource_farm.data.misc;

import com.resource_farm.ResourceFarm;

import net.minecraft.resources.ResourceLocation;

/**
 * 静态纯双层材质叠加模型工具类
 */
public class ResourceFarmModels {

    // ===================== 公共常量：基础父模型路径 =====================
    public static final ResourceLocation STATIC_DEFAULT_PARENT = ResourceFarm.id("block/cube_multi_layer/default");
    public static final ResourceLocation STATIC_ALL_PARENT = ResourceFarm.id("block/cube_multi_layer/all");
    public static final ResourceLocation STATIC_BOTTOM_TOP_PARENT = ResourceFarm
            .id("block/cube_multi_layer/bottom_top");
    public static final ResourceLocation STATIC_ITEM_PARENT = ResourceLocation.fromNamespaceAndPath("minecraft",
            "item/generated");

    // ===================== 特殊形态双层父模型路径 =====================
    /** 水平柱双层父模型（继承 minecraft:block/cube_column_horizontal，支持双层分面（side/end）） */
    public static final ResourceLocation STATIC_HORIZONTAL_COLUMN_PARENT = ResourceFarm
            .id("block/cube_multi_layer/horizontal_column");
    /** 十字形态双层父模型（继承 minecraft:block/cross，支持双层全脸，适配树苗） */
    public static final ResourceLocation STATIC_CROSS_PARENT = ResourceFarm.id("block/cube_multi_layer/cross");
    /** 树叶双层父模型（继承 minecraft:block/leaves，支持双层全脸 + 着色（tint），适配树叶） */
    public static final ResourceLocation STATIC_LEAVES_PARENT = ResourceFarm.id("block/cube_multi_layer/leaves");

    public static final ResourceLocation BLANK_TEXTURE = ResourceFarm.id("block/void");

    public record LayerFaceTextures(ResourceLocation bottom, ResourceLocation side, ResourceLocation top) {

        public LayerFaceTextures(ResourceLocation bottom, ResourceLocation side, ResourceLocation top) {
            this.bottom = bottom != null ? bottom : BLANK_TEXTURE;
            this.side = side != null ? side : BLANK_TEXTURE;
            this.top = top != null ? top : BLANK_TEXTURE;
        }

        public static LayerFaceTextures allFace(ResourceLocation allFaceTexture) {
            return new LayerFaceTextures(allFaceTexture, allFaceTexture, allFaceTexture);
        }

        public static LayerFaceTextures bottomTopSame(ResourceLocation bottomTopTexture, ResourceLocation sideTexture) {
            return new LayerFaceTextures(bottomTopTexture, sideTexture, bottomTopTexture);
        }
    }
}
