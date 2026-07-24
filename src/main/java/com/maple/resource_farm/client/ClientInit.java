package com.maple.resource_farm.client;

import com.maple.resource_farm.api.item.TintableItem;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class ClientInit {

    public static void init(IEventBus modBus) {
        modBus.register(ClientInit.class);
    }

    @SubscribeEvent
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(TintableItem.TINT_SOURCE_ID, TintableItem.TintableItemTintSource.MAP_CODEC);
    }
}
