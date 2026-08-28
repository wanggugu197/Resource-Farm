package com.maple.resource_farm.client;

import com.maple.resource_farm.ResourceFarm;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@EventBusSubscriber(modid = ResourceFarm.MOD_ID)
public class RecipeSyncHandler {

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        // 1.21.1：自定义配方类型随 RecipeManager 自动同步到客户端，无需显式请求
        ResourceFarm.LOGGER.debug("[ResourceFarm] Datapack sync; custom recipe types sync automatically.");
    }
}
