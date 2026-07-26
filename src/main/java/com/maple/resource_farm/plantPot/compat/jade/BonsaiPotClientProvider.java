package com.maple.resource_farm.plantPot.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.jspecify.annotations.NonNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum BonsaiPotClientProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public @NonNull Identifier getUid() {
        return BonsaiPotProvider.UID;
    }

    @Override
    public void appendTooltip(@NonNull ITooltip tooltip, BlockAccessor accessor, @NonNull IPluginConfig config) {
        CompoundTag data = accessor.getServerData();

        String soilName = data.getStringOr("soilName", "");
        float growthModifier = data.getFloatOr("growthModifier", 1f);
        String cropName = data.getStringOr("cropName", "");
        int currentStage = data.getIntOr("currentStage", 0);
        int maxStage = data.getIntOr("maxStage", 0);
        int progressPercent = data.getIntOr("progressPercent", 0);
        float skyBoostModifier = data.getFloatOr("skyBoostModifier", 1f);

        if (!data.getBooleanOr("hasCrop", false)) {
            tooltip.add(Component.translatable("jade.resource_farm.no_crop").withStyle(ChatFormatting.GRAY));
            if (!soilName.isEmpty()) {
                tooltip.add(Component.translatable("jade.resource_farm.soil_info",
                        soilName, String.format("%.2fx", growthModifier))
                        .withStyle(ChatFormatting.GRAY));
            }
            return;
        }

        if (progressPercent >= 100) {
            tooltip.add(Component.translatable("jade.resource_farm.crop_ready", cropName)
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        } else {
            tooltip.add(Component.translatable("jade.resource_farm.crop_progress",
                    cropName, currentStage, maxStage, progressPercent)
                    .withStyle(ChatFormatting.GREEN));
        }

        tooltip.add(Component.translatable("jade.resource_farm.soil_info",
                soilName, String.format("%.2fx", growthModifier))
                .withStyle(ChatFormatting.DARK_AQUA));

        if (skyBoostModifier != 1.0f) {
            tooltip.add(Component.translatable("jade.resource_farm.sky_boost",
                    String.format("%.2fx", skyBoostModifier))
                    .withStyle(ChatFormatting.YELLOW));
        }
    }
}
