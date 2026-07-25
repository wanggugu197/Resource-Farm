package com.maple.resource_farm.data;

import com.maple.resource_farm.ResourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;

import net.neoforged.bus.api.IEventBus;

public class ResourceFarmRegister {

    public static void init(IEventBus modBus) {
        if (ResourceFarmConfigHolder.FarmConfigHolder.dev.enableResourceTree) {
            ResourceTreeAccessManagement.registerTree(modBus);
        }
    }
}
