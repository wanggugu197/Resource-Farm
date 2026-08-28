package com.maple.resource_farm.client;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.resourceTree.builder.TreeModelRenderer;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;

import lombok.Getter;

public class ClientInit {

    @Getter
    private static RecipeManager clientRecipeManager = new RecipeManager(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));

    public static void init(IEventBus modBus) {
        // 1.21.1：资源树物品的运行时着色必须在 RegisterColorHandlersEvent.Item 事件内注册
        // （动态资源包的模型在事件后才重建，但着色数据可在事件时从资源树配置独立计算）
        modBus.addListener(ClientInit::registerItemTints);

        // 监听游戏事件（RecipesUpdatedEvent 和登出事件）
        NeoForge.EVENT_BUS.addListener(ClientInit::receiveRecipes);
        NeoForge.EVENT_BUS.addListener(ClientInit::onLoggingOut);
    }

    public static void registerItemTints(RegisterColorHandlersEvent.Item event) {
        TreeModelRenderer.applyItemTints(event);
    }

    public static void receiveRecipes(RecipesUpdatedEvent event) {
        clientRecipeManager = event.getRecipeManager();
        ResourceFarm.LOGGER.info("[ResourceFarm] Client recipe cache updated.");
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        clientRecipeManager = new RecipeManager(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        ResourceFarm.LOGGER.info("[ResourceFarm] Client recipe cache cleared on logout.");
    }
}
