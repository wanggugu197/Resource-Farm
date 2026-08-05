package com.maple.resource_farm.resourceTree;

import com.maple.resource_farm.resourceTree.builder.TreeBuilder;
import com.maple.resource_farm.resourceTree.builder.TreeLootInsert;
import com.maple.resource_farm.resourceTree.builder.TreeRecipe;
import com.maple.resource_farm.resourceTree.builder.TreeTagInsert;
import com.maple.resource_farm.resourceTree.data.ResourceTree;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceFarmMaps;
import com.maple.resource_farm.resourceTree.data.register.TreesCommonRegister;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;

import com.mapleutillib.api.dynamicdata.DynamicDataManager;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import static com.maple.resource_farm.ResourceFarm.REGISTRY;
import static com.maple.resource_farm.ResourceFarm.isDataGen;
import static com.maple.resource_farm.resourceTree.builder.TreeInitialization.initializationTreeMap;

public class ResourceTreeAccessManagement {

    public static final Object2ObjectOpenHashMap<String, ResourceTree> ResourceTreeMap = new Object2ObjectOpenHashMap<>();

    private ResourceTreeAccessManagement() {}

    public static void registerTree(IEventBus modBus) {
        ResourceFarmMaps.load();
        TreesCommonRegister.registerLang();
        initializationTreeMap();
        // 注册方块/物品
        if (!isDataGen()) {
            TreeBuilder.registerResourceTree();
            registerDynamicDataBuilders();
        }
    }

    private static void registerDynamicDataBuilders() {
        DynamicDataManager manager = REGISTRY.dynamicData();
        manager.stageTagMembers(
                TreeTagInsert.collectBlockTagMembers(),
                TreeTagInsert.collectItemTagMembers())
                .registerEarlyBuilder(() -> buildCompostables(manager))
                .registerEarlyBuilder(access -> TreeLootInsert.generateBlockLoot(manager::addLootTable, access))
                .registerLateBuilder(registries -> registerTreeRecipe(manager.createRecipeOutput(), registries));
    }

    private static void buildCompostables(DynamicDataManager manager) {
        Compostable saplingCompost = new Compostable(0.8F);
        Compostable leavesCompost = new Compostable(0.6F);
        ResourceTreeMap.forEach((a, resourceTree) -> {
            if (resourceTree == null) return;
            // BlockItem 与方块同 id
            manager.addCompostable(resourceTree.getSapling().identifier(), saplingCompost);
            manager.addCompostable(resourceTree.getLeaves().identifier(), leavesCompost);
        });
    }

    private static final Object2IntOpenHashMap<String> treeCommonRecipeCount = new Object2IntOpenHashMap<>();

    public static void registerTreeRecipe(RecipeOutput consumer, HolderLookup.Provider registries) {
        treeCommonRecipeCount.clear();
        TreeRecipe.init(consumer, registries);
        treeCommonRecipeCount.clear();
    }

    public static void addTreeRecipeCount(String treeId, int amount) {
        treeCommonRecipeCount.addTo(treeId, amount);
    }

    public static int getTreeRecipeCount(String treeId) {
        return treeCommonRecipeCount.getInt(treeId);
    }

    public static ResourceTree getResourceTree(String treeId) {
        String id = (treeId.endsWith("_tree") ? treeId : treeId + "_tree").toLowerCase().replace(":", "_");
        return ResourceTreeMap.get(id);
    }
}
