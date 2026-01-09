package com.resource_farm.common;

import com.resource_farm.common.datagen.Datagen;
import com.resource_farm.common.pack.ResourceFarmDynamicDataPack;
import com.resource_farm.common.pack.ResourceFarmDynamicResourcePack;
import com.resource_farm.common.pack.ResourceFarmPackSource;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.data.misc.ResourceFarmCreativeModeTabs;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

public class CommonInit {

    private static IEventBus modBus;

    public static void init(final IEventBus modBus) {
        CommonInit.modBus = modBus;
        modBus.register(CommonInit.class);
        ResourceFarmCreativeModeTabs.init();
        ResourceFarmBlocks.init();
        modBus.addListener(CommonInit::commonSetup);
        modBus.addListener(CommonInit::modConstruct);
    }

    private static void modConstruct(FMLConstructModEvent event) {
        Datagen.init();
    }

    private static void commonSetup(FMLCommonSetupEvent event) {}

    @SubscribeEvent
    public static void registerPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            ResourceFarmDynamicResourcePack.clearClient();
            event.addRepositorySource(new ResourceFarmPackSource("resource_farm:dynamic_assets",
                    event.getPackType(),
                    Pack.Position.BOTTOM,
                    ResourceFarmDynamicResourcePack::new));
        } else if (event.getPackType() == PackType.SERVER_DATA) {
            ResourceFarmDynamicDataPack.clearServer();
            event.addRepositorySource(new ResourceFarmPackSource("resource_farm:dynamic_data",
                    event.getPackType(),
                    Pack.Position.BOTTOM,
                    ResourceFarmDynamicDataPack::new));
        }
    }
}
