package com.maple.resource_farm.plantPot.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum HoppingBonsaiPotClientProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public @NotNull ResourceLocation getUid() {
        return HoppingBonsaiPotProvider.UID;
    }

    @Override
    public void appendTooltip(@NotNull ITooltip tooltip, BlockAccessor accessor, @NotNull IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        int isCloched = data.getInt("isCloched");
        if (isCloched != 0) {
            String clocheName = data.getString("clocheName");
            float clocheSpeed = data.contains("clocheSpeedModifier") ? data.getFloat("clocheSpeedModifier") : 1f;
            float clocheYield = data.contains("clocheYieldModifier") ? data.getFloat("clocheYieldModifier") : 1f;
            tooltip.add(Component.translatable("jade.resource_farm.cloche_installed",
                    clocheName,
                    String.format("%.2f", clocheSpeed),
                    String.format("%.2f", clocheYield))
                    .withStyle(ChatFormatting.DARK_GREEN));
        }
    }
}
