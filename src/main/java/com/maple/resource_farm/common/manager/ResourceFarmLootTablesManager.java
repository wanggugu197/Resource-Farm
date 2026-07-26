package com.maple.resource_farm.common.manager;

import com.maple.resource_farm.common.inject.ResourceFarmDynamicInjections;
import com.maple.resource_farm.resourceTree.builder.TreeLootInsert;

import net.minecraft.core.RegistryAccess;

/**
 * 生成战利品表到内存暂存区（直注，无 JSON）。
 */
public class ResourceFarmLootTablesManager {

    public static void generateLoot(final RegistryAccess.Frozen access) {
        TreeLootInsert.generateBlockLoot(ResourceFarmDynamicInjections::addLootTable, access);
    }
}
