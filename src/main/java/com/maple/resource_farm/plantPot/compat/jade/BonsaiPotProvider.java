package com.maple.resource_farm.plantPot.compat.jade;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.plantPot.block.BonsaiPotBlockEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.NotNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum BonsaiPotProvider implements IServerDataProvider<BlockAccessor> {

    INSTANCE;

    public static final ResourceLocation UID = ResourceFarm.id("bonsai_pot_info");

    @Override
    public @NotNull ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(@NotNull CompoundTag data, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (!(be instanceof BonsaiPotBlockEntity bed)) return;

        ItemStack seedStack = bed.getStack(0);
        ItemStack soilStack = bed.getStack(1);

        if (seedStack.isEmpty()) {
            data.putBoolean("hasCrop", false);
            if (!soilStack.isEmpty()) {
                data.putString("soilName", soilStack.getDisplayName().getString());
                data.putFloat("growthModifier", bed.getSoilGrowthModifier(soilStack));
            }
            return;
        }

        data.putBoolean("hasCrop", true);
        data.putString("cropName", seedStack.getDisplayName().getString());
        data.putInt("currentStage", bed.getGrowthStage());
        data.putInt("maxStage", bed.isCrop() ? 8 : 1);
        data.putInt("progressPercent", bed.getGrowthProgress());
        data.putString("soilName", soilStack.getDisplayName().getString());
        data.putFloat("growthModifier", bed.getSoilGrowthModifier(soilStack));
        data.putFloat("skyBoostModifier", bed.getSkyDayModifier());
    }
}
