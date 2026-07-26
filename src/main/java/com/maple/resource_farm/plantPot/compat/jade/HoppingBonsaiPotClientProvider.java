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

public enum HoppingBonsaiPotClientProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public @NonNull Identifier getUid() {
        return HoppingBonsaiPotProvider.UID;
    }

    @Override
    public void appendTooltip(@NonNull ITooltip tooltip, BlockAccessor accessor, @NonNull IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        int isCloched = data.getIntOr("isCloched", 0);
        if (isCloched != 0) {
            String clocheName = data.getStringOr("clocheName", "");
            float clocheSpeed = data.getFloatOr("clocheSpeedModifier", 1f);
            float clocheYield = data.getFloatOr("clocheYieldModifier", 1f);
            tooltip.add(Component.translatable("jade.resource_farm.cloche_installed",
                    clocheName,
                    String.format("%.2f", clocheSpeed),
                    String.format("%.2f", clocheYield))
                    .withStyle(ChatFormatting.DARK_GREEN));
        }
    }
}
