package com.maple.resource_farm.data.tree;

import com.maple.resource_farm.api.ResourceOre.ResourceOreType;
import com.maple.resource_farm.api.ResourceTree.ResourceTreeType;
import com.maple.resource_farm.api.block.FertilizeSettings;

import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.Lazy;

import com.mapleutillib.utils.RegistriesUtils;

import javax.annotation.Nullable;

public record ResourceTreeConfig(
                                 String id,
                                 @Nullable String correspondingItem,
                                 @Nullable String translateKey,
                                 boolean automaticBasicRecipe,
                                 int productOutput,
                                 ResourceTreeType treeType,
                                 ResourceOreType oreType,
                                 FertilizeSettings fertilizeSetting,
                                 int growthFrequency,
                                 Lazy<Block> customPlaceBlock,
                                 @Nullable TagKey<Block> customPlaceBlockTag,
                                 int lightLevel,
                                 int color) {

    public ResourceTreeConfig {
        lightLevel = Mth.clamp(lightLevel, 0, 15);
        color = opaque(color);
        id = generateId(correspondingItem, translateKey);
    }

    /** 保证 Alpha = 0xFF */
    public static int opaque(int rgbOrArgb) {
        return 0xFF000000 | (rgbOrArgb & 0x00FFFFFF);
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

    public static ResourceTreeConfig create(
                                            @Nullable String correspondingItem,
                                            @Nullable String translateKey,
                                            boolean automaticBasicRecipe,
                                            int productOutput,
                                            ResourceTreeType treeStyle,
                                            ResourceOreType oreStyle,
                                            FertilizeSettings fertilizeSetting,
                                            int growthFrequency,
                                            @Nullable String customPlaceBlock,
                                            @Nullable TagKey<Block> customPlaceBlockTag,
                                            int lightLevel,
                                            int colors) {
        return new ResourceTreeConfig(
                null,
                correspondingItem,
                translateKey,
                automaticBasicRecipe,
                productOutput,
                treeStyle,
                oreStyle,
                fertilizeSetting,
                growthFrequency,
                Lazy.of(() -> customPlaceBlock == null ? Blocks.BARRIER : RegistriesUtils.getBlock(customPlaceBlock)),
                customPlaceBlockTag,
                lightLevel,
                colors);
    }
}
