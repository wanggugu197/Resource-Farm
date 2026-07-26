package com.maple.resource_farm.client;

import com.maple.resource_farm.ResourceFarm;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;

@Mod(value = ResourceFarm.MOD_ID, dist = Dist.CLIENT)
public class ResourceFarmClient {

    public ResourceFarmClient(IEventBus modBus, FMLModContainer container) {
        ClientInit.init(modBus);
    }
}
