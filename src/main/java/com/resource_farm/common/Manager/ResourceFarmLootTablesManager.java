package com.resource_farm.common.Manager;

import com.resource_farm.data.tree.builder.TreeLootInsert;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import org.apache.logging.log4j.util.TriConsumer;

public class ResourceFarmLootTablesManager {

    public static void generateComposTablesLoot(TriConsumer<ResourceLocation, LootTable, RegistryAccess.Frozen> lootTables,
                                                final RegistryAccess.Frozen access) {
        TreeLootInsert.generateBlockLoot(lootTables, access);
    }
}
