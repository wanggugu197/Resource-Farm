package com.maple.resource_farm.resourceTree;

import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.resourceTree.builder.TreeBuilder;
import com.maple.resource_farm.resourceTree.builder.TreeRecipe;
import com.maple.resource_farm.resourceTree.data.ResourceTree;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceFarmMaps;
import com.maple.resource_farm.resourceTree.data.register.TreesCommonRegister;

import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.bus.api.IEventBus;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

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
        }
    }

    public static void registerTreeRecipe(RecipeOutput consumer) {
        TreeRecipe.init(consumer);
        if (ResourceFarmConfigHolder.treeConfigHolder != null && ResourceFarmConfigHolder.treeConfigHolder.enablePresetTreeGroups) {
            PresetResourceTreeConfigHolder.PresetTreeGenerationConfigs configs = ResourceFarmConfigHolder.treeConfigHolder.presetTreeGeneration;
            if (configs.minecraftBase) TreesCommonRegister.baseSpecialRecipe(consumer);
            if (configs.minecraftMineral) TreesCommonRegister.mineralSpecialRecipe(consumer);
            if (configs.minecraftBiology) TreesCommonRegister.biologySpecialRecipe(consumer);
            if (configs.minecraftAgriculture) TreesCommonRegister.agricultureSpecialRecipe(consumer);
        }
    }
}
