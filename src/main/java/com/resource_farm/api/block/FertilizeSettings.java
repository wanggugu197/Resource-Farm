package com.resource_farm.api.block;

import com.resource_farm.utils.RegistriesUtils;

import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.Lazy;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;

/**
 * 催熟物品配置记录类
 */
public record FertilizeSettings(
                                Lazy<Item> mainRipeningItem,
                                double mainChance,
                                Lazy<Item> secondaryRipeningItem,
                                double secondaryChance) {

    public static final FertilizeSettings DEFAULT = new FertilizeSettings(Lazy.of(() -> Items.BONE_MEAL), 0.35, Lazy.of(() -> Items.BONE_MEAL), 0.35);
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

    public static FertilizeSettings of(Lazy<Item> mainItem, double mainChance,
                                       Lazy<Item> secondaryItem, double secondaryChance) {
        if (isChanceValid(mainChance) || isChanceValid(secondaryChance)) return DEFAULT;
        return new FertilizeSettings(mainItem, mainChance, secondaryItem, secondaryChance);
    }

    public static FertilizeSettings of(Lazy<Item> mainItem, double mainChance) {
        if (isChanceValid(mainChance)) return DEFAULT;
        return new FertilizeSettings(mainItem, mainChance, Lazy.of(() -> Items.BARRIER), 0.0);
    }

    public static FertilizeSettings of(@Nullable String mainItem, double mainChance,
                                       @Nullable String secondaryItem, double secondaryChance) {
        return FertilizeSettings.of(
                Lazy.of(() -> mainItem == null ? Items.BARRIER : RegistriesUtils.getItem(mainItem)),
                mainChance,
                Lazy.of(() -> secondaryItem == null ? Items.BARRIER : RegistriesUtils.getItem(secondaryItem)),
                secondaryChance);
    }

    public static FertilizeSettings of(@Nullable String mainItem, double mainChance) {
        return FertilizeSettings.of(
                Lazy.of(() -> mainItem == null ? Items.BARRIER : RegistriesUtils.getItem(mainItem)),
                mainChance);
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

        Lazy<Item> mainItem = mainItemStr == null ? null : Lazy.of(() -> RegistriesUtils.getItem(mainItemStr));
        Lazy<Item> secondaryItem = secondaryItemStr == null ? null : Lazy.of(() -> RegistriesUtils.getItem(secondaryItemStr));

        return FertilizeSettings.of(mainItem, mainChance, secondaryItem, secondaryChance);
    }
}
