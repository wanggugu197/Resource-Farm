package com.maple.resource_farm.common.Manager;

import com.maple.resource_farm.ResourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.common.inject.ResourceFarmDynamicInjections;

import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;

/**
 * 构建堆肥桶 DataMap 条目到内存暂存区（直注，无 JSON）。
 */
public class ResourceFarmComposTablesManager {

    public static void buildComposTablesData() {
        Compostable saplingCompost = new Compostable(0.8F);
        Compostable leavesCompost = new Compostable(0.6F);
        ResourceTreeAccessManagement.ResourceTreeMap.forEach((a, resourceTree) -> {
            if (resourceTree == null) return;
            // BlockItem 与方块同 id
            ResourceFarmDynamicInjections.addCompostable(resourceTree.getSapling().identifier(), saplingCompost);
            ResourceFarmDynamicInjections.addCompostable(resourceTree.getLeaves().identifier(), leavesCompost);
        });
    }
}
