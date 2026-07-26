package com.maple.resource_farm.client;

import com.maple.resource_farm.ResourceFarm;

import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.common.NeoForge;

import lombok.Getter;

public class ClientInit {

    @Getter
    private static RecipeMap clientRecipeMap = RecipeMap.EMPTY;

    public static void init(IEventBus modBus) {
        // modBus.register(ClientInit.class);

        // 监听游戏事件（RecipesReceivedEvent 和登出事件）
        NeoForge.EVENT_BUS.addListener(ClientInit::receiveRecipes);
        NeoForge.EVENT_BUS.addListener(ClientInit::onLoggingOut);
    }

    public static void receiveRecipes(RecipesReceivedEvent event) {
        clientRecipeMap = event.getRecipeMap();
        ResourceFarm.LOGGER.info("[ResourceFarm] Client recipe cache updated.");
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        clientRecipeMap = RecipeMap.EMPTY;
        ResourceFarm.LOGGER.info("[ResourceFarm] Client recipe cache cleared on logout.");
    }
}
