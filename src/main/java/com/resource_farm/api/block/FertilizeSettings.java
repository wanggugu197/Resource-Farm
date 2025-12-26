package com.resource_farm.api.block;

import com.resource_farm.utils.RegistriesUtils;

import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;

/**
 * 催熟物品配置记录类
 */
public record FertilizeSettings(
                                @Nullable Item mainRipeningItem,
                                double mainChance,
                                @Nullable Item secondaryRipeningItem,
                                double secondaryChance) {

    public static final FertilizeSettings DEFAULT = new FertilizeSettings(Items.BONE_MEAL, 0.35, Items.BONE_MEAL, 0.35);
    public static final FertilizeSettings NULL = new FertilizeSettings(null, 0, null, 0);

    public FertilizeSettings {
        if (mainChance < 0.0 || mainChance > 1.0)
            throw new IllegalArgumentException("mainChance must be between 0.0 and 1.0");
        if (secondaryChance < 0.0 || secondaryChance > 1.0)
            throw new IllegalArgumentException("secondaryChance must be between 0.0 and 1.0");
    }

    public boolean isDefaultOrNull() {
        return this == DEFAULT || this == NULL;
    }

    private static boolean isChanceValid(double chance) {
        return !(chance >= 0.0) || !(chance <= 1.0);
    }

    public static FertilizeSettings of(@Nullable Item mainItem, double mainChance,
                                       @Nullable Item secondaryItem, double secondaryChance) {
        if (mainItem == Items.BARRIER || secondaryItem == Items.BARRIER || isChanceValid(mainChance) || isChanceValid(secondaryChance)) {
            return DEFAULT;
        }
        return new FertilizeSettings(mainItem, mainChance, secondaryItem, secondaryChance);
    }

    public static FertilizeSettings of(@Nullable Item mainItem, double mainChance) {
        if (mainItem == Items.BARRIER || isChanceValid(mainChance)) {
            return DEFAULT;
        }
        return new FertilizeSettings(mainItem, mainChance, null, 0.0);
    }

    public static FertilizeSettings fromJson(JsonObject json) {
        if (json == null) return DEFAULT;

        String type = GsonHelper.getAsString(json, "type", null);
        if (type != null) {
            if (type.equals("default")) return DEFAULT;
            if (type.equals("null")) return NULL;
        }

        String mainItemStr = GsonHelper.getAsString(json, "mainItem", null);
        double mainChance = GsonHelper.getAsDouble(json, "mainChance", 0.0);
        String secondaryItemStr = GsonHelper.getAsString(json, "secondaryItem", null);
        double secondaryChance = GsonHelper.getAsDouble(json, "secondaryChance", 0.0);

        Item mainItem = mainItemStr == null ? null : RegistriesUtils.getItem(mainItemStr);
        Item secondaryItem = secondaryItemStr == null ? null : RegistriesUtils.getItem(secondaryItemStr);

        if (mainItem != null) {
            return secondaryItem != null ? FertilizeSettings.of(mainItem, mainChance, secondaryItem, secondaryChance) :
                    FertilizeSettings.of(mainItem, mainChance);
        }

        return DEFAULT;
    }
}
