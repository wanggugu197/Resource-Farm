package com.resource_farm.client;

import net.neoforged.bus.api.IEventBus;

public class ClientInit {

    public static void init(IEventBus modBus) {
        modBus.register(ClientInit.class);
    }
}
