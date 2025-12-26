package com.resource_farm.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.javafmlmod.FMLModContainer;

public class Client {

    public Client(IEventBus modBus, FMLModContainer container) {
        ClientInit.init(modBus);
    }
}
