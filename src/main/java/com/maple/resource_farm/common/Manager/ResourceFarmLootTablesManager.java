package com.maple.resource_farm.common.Manager;

import com.maple.resource_farm.data.tree.builder.TreeLootInsert;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootTable;

import org.apache.logging.log4j.util.TriConsumer;

public class ResourceFarmLootTablesManager {

    public static void generateComposTablesLoot(TriConsumer<Identifier, LootTable, RegistryAccess.Frozen> lootTables,
                                                final RegistryAccess.Frozen access) {
        TreeLootInsert.generateBlockLoot(lootTables, access);
    }
}
