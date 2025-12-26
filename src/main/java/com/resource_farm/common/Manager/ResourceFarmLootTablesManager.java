package com.resource_farm.common.Manager;

import com.resource_farm.data.tree.builder.TreeLootManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import org.apache.logging.log4j.util.TriConsumer;

public class ResourceFarmLootTablesManager {

    public static void generateComposTablesLoot(TriConsumer<ResourceLocation, LootTable, RegistryAccess.Frozen> lootTables,
                                                final RegistryAccess.Frozen access) {
        TreeLootManager.generateBlockLoot(lootTables, access);
    }
}
