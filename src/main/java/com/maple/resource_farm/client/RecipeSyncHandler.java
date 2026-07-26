package com.maple.resource_farm.client;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.plantPot.ResourcePlantPotRegister;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@EventBusSubscriber(modid = ResourceFarm.MOD_ID)
public class RecipeSyncHandler {

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        // 告诉 NeoForge 需要将 ResourceFarmRecipeTypes.GROWTH 类型的配方同步到客户端
        event.sendRecipes(ResourcePlantPotRegister.GROWTH.get());
        ResourceFarm.LOGGER.info("[ResourceFarm] Requested sync of GROWTH recipe type to client.");
    }
}
