package com.maple.resource_farm.compat;

import com.maple.resource_farm.plantPot.block.BonsaiPotBlock;
import com.maple.resource_farm.plantPot.block.BonsaiPotBlockEntity;
import com.maple.resource_farm.plantPot.block.HoppingBonsaiPotBlock;
import com.maple.resource_farm.plantPot.block.HoppingBonsaiPotBlockEntity;
import com.maple.resource_farm.plantPot.compat.jade.BonsaiPotClientProvider;
import com.maple.resource_farm.plantPot.compat.jade.BonsaiPotProvider;
import com.maple.resource_farm.plantPot.compat.jade.HoppingBonsaiPotClientProvider;
import com.maple.resource_farm.plantPot.compat.jade.HoppingBonsaiPotProvider;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class ResourceFarmJadePlugin implements IWailaPlugin {

    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(HoppingBonsaiPotProvider.INSTANCE, HoppingBonsaiPotBlockEntity.class);
        registration.registerBlockDataProvider(BonsaiPotProvider.INSTANCE, BonsaiPotBlockEntity.class);
    }

    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(HoppingBonsaiPotClientProvider.INSTANCE, HoppingBonsaiPotBlock.class);
        registration.registerBlockComponent(BonsaiPotClientProvider.INSTANCE, BonsaiPotBlock.class);
    }
}
