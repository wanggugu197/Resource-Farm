package com.maple.resource_farm.plantPot.compat.jade;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.plantPot.block.ClocheHelper;
import com.maple.resource_farm.plantPot.block.HoppingBonsaiPotBlockEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.NotNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum HoppingBonsaiPotProvider implements IServerDataProvider<BlockAccessor> {

    INSTANCE;

    public static final ResourceLocation UID = ResourceFarm.id("hopping_bonsai_pot_info");

    @Override
    public @NotNull ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(@NotNull CompoundTag data, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (!(be instanceof HoppingBonsaiPotBlockEntity bed)) return;
        int cloched = bed.getClochedTier();
        data.putInt("isCloched", cloched);
        if (cloched != 0) {
            data.putString("clocheName", ClocheHelper.getClocheItem(cloched).getDisplayName().getString());
            data.putFloat("clocheSpeedModifier", bed.getClocheGrowthModifier());
            data.putFloat("clocheYieldModifier", bed.getClocheYieldModifier());
        }
    }
}
