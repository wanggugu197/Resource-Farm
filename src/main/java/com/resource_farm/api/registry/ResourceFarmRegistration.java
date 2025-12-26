package com.resource_farm.api.registry;

import com.resource_farm.ResourceFarm;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import com.tterrag.registrate.Registrate;

public class ResourceFarmRegistration {

    public static final Registrate REGISTRATE = Registrate.create(ResourceFarm.MOD_ID);

    static {
        ResourceFarmRegistration.REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    public static void init() {}
}
