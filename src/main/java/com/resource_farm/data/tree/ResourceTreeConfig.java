package com.resource_farm.data.tree;

import com.resource_farm.api.ResourceOre.ResourceOreType;
import com.resource_farm.api.ResourceTree.ResourceTreeType;
import com.resource_farm.api.block.ColoringSettings;
import com.resource_farm.api.block.FertilizeSettings;

import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public record ResourceTreeConfig(
                                 String id,                                     // 唯一标识，自动生成
                                 @Nullable String correspondingItem,            // 对应物品
                                 @Nullable String translateKey,                 // 对应翻译键
                                 int productOutput,                             // 基础合成产量
                                 ResourceTreeType treeType,                     // 树木样式
                                 ResourceOreType oreType,                       // 矿石样式
                                 FertilizeSettings fertilizeSetting,            // 施肥设置
                                 int growthFrequency,                           // 生长频率
                                 @Nullable Block customPlaceBlock,              // 可放置方块
                                 @Nullable TagKey<Block> customPlaceBlockTag,   // 可放置方块Tag
                                 int lightLevel,                                // 亮度
                                 ColoringSettings coloringSettings              // 颜色
) {

    public ResourceTreeConfig {
        lightLevel = Mth.clamp(lightLevel, 0, 15);

        id = generateId(correspondingItem, translateKey);
    }

    private static String generateId(@Nullable String correspondingItem, @Nullable String translationKey) {
        if (correspondingItem != null) {
            int colonIndex = correspondingItem.indexOf(':');
            if (colonIndex != -1) {
                if ("minecraft".equals(correspondingItem.substring(0, colonIndex))) {
                    return correspondingItem.substring(colonIndex + 1).replace(':', '_');
                }
            }
            return correspondingItem.replace(':', '_');
        }
        if (translationKey != null) {
            int lastDotIndex = translationKey.lastIndexOf('.');
            return lastDotIndex == -1 ? translationKey : translationKey.substring(lastDotIndex + 1);
        }
        throw new IllegalArgumentException(
                "correspondingItem and translateKey cannot both be null, one of them must be specified (non-null)");
    }

    /**
     * 基础创建函数
     */
    public static ResourceTreeConfig create(
                                            @Nullable String correspondingItem,
                                            @Nullable String translateKey,
                                            int productOutput,
                                            ResourceTreeType treeStyle,
                                            ResourceOreType oreStyle,
                                            FertilizeSettings fertilizeSetting,
                                            int growthFrequency,
                                            @Nullable Block customPlaceBlock,
                                            @Nullable TagKey<Block> customPlaceBlockTag,
                                            int lightLevel,
                                            int colors) {
        return new ResourceTreeConfig(
                null,
                correspondingItem,
                translateKey,
                productOutput,
                treeStyle,
                oreStyle,
                fertilizeSetting,
                growthFrequency,
                customPlaceBlock,
                customPlaceBlockTag,
                lightLevel,
                ColoringSettings.of(new boolean[] { false, true }, new int[] { -1, colors }));
    }
}
