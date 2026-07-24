package com.maple.resource_farm.common.Manager;

import com.maple.resource_farm.common.pack.ResourceFarmDynamicDataPack;
import com.maple.resource_farm.data.ResourceFarmBlocks;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

public class ResourceFarmComposTablesManager {

    private static final DataMapType<Item, Compostable> COMPOSTABLE_DATA_MAP_TYPE = NeoForgeDataMaps.COMPOSTABLES;

    public static void buildComposTablesData(RegistryAccess.Frozen frozen) {
        DataMapProvider.Builder<Compostable, Item> compostDataMapBuilder = new DataMapProvider.Builder<>(COMPOSTABLE_DATA_MAP_TYPE);
        compostDataMapBuilder.replace(false);
        Compostable saplingCompost = new Compostable(0.8F);
        Compostable leavesCompost = new Compostable(0.6F);
        ResourceFarmBlocks.ResourceTreeMap.forEach((a, resourceTree) -> {
            if (resourceTree == null) return;
            // BlockItem 与方块同 id
            compostDataMapBuilder.add(resourceTree.getSapling().identifier(), saplingCompost, false);
            compostDataMapBuilder.add(resourceTree.getLeaves().identifier(), leavesCompost, false);
        });

        ResourceFarmDynamicDataPack.addDataMap(COMPOSTABLE_DATA_MAP_TYPE, compostDataMapBuilder, frozen);
    }
}
