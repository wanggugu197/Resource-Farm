package com.resource_farm.data.tree;

import com.resource_farm.ResourceFarm;
import com.resource_farm.config.PresetResourceTreeConfigHolder;
import com.resource_farm.config.ResourceFarmConfigHolder;
import com.resource_farm.config.TreeRegisterConfig;
import com.resource_farm.config.TreeRemoveConfig;
import com.resource_farm.data.tree.RegisterAndSpecialRecipe.CrossoverCreateRegister;
import com.resource_farm.data.tree.RegisterAndSpecialRecipe.CrossoverMekanismRegister;
import com.resource_farm.data.tree.RegisterAndSpecialRecipe.TreesCommonRegister;
import com.resource_farm.data.tree.builder.TreeRecipe;

import net.minecraft.data.recipes.RecipeOutput;

public class ResourceTreeAccessManagement {

    public static void registerTree() {
        // 资源树-基础注册
        if (ResourceFarmConfigHolder.TreeConfigHolder.enablePresetTreeGroups) {
            PresetResourceTreeConfigHolder.PresetTreeGenerationConfigs configs = ResourceFarmConfigHolder.TreeConfigHolder.presetTreeGeneration;
            if (configs.minecraftBase) TreesCommonRegister.base();
            if (configs.minecraftMineral) TreesCommonRegister.mineral();
            if (configs.minecraftBiology) TreesCommonRegister.biology();
            if (configs.minecraftAgriculture) TreesCommonRegister.agriculture();
            if (ResourceFarm.isCreateLoaded() && configs.crossoverCreate) CrossoverCreateRegister.create();
            if (ResourceFarm.isMekLoaded() && configs.crossoverMek) CrossoverMekanismRegister.create();
        }

        // 资源树-配置文件注册
        TreeRegisterConfig.parseJSONtoRegisterTree();
        // 资源树-配置文件移除
        TreeRemoveConfig.parseJSONtoRemoveTree();
    }

    public static void registerTreeRecipe(RecipeOutput consumer) {
        // 资源树-通用配方注册
        TreeRecipe.init(consumer);
        // 资源树-特殊配方注册
        if (ResourceFarmConfigHolder.TreeConfigHolder.enablePresetTreeGroups) {
            PresetResourceTreeConfigHolder.PresetTreeGenerationConfigs configs = ResourceFarmConfigHolder.TreeConfigHolder.presetTreeGeneration;
            if (configs.minecraftBase) TreesCommonRegister.baseSpecialRecipe(consumer);
            if (configs.minecraftMineral) TreesCommonRegister.mineralSpecialRecipe(consumer);
            if (configs.minecraftBiology) TreesCommonRegister.biologySpecialRecipe(consumer);
            if (configs.minecraftAgriculture) TreesCommonRegister.agricultureSpecialRecipe(consumer);
        }
    }

    public static void registerCreateTreeRecipe(RecipeOutput consumer) {
        // 资源树-机械动力联动通用配方注册
        CrossoverCreateRegister.init(consumer);
        // 资源树-机械动力联动特殊配方注册
        if (ResourceFarmConfigHolder.TreeConfigHolder.enablePresetTreeGroups) {
            PresetResourceTreeConfigHolder.PresetTreeGenerationConfigs configs = ResourceFarmConfigHolder.TreeConfigHolder.presetTreeGeneration;
            if (configs.crossoverCreate) CrossoverCreateRegister.createSpecialRecipe(consumer);
        }
    }

    public static void registerMekanismTreeRecipe(RecipeOutput consumer) {
        // 资源树-通用机械联动通用配方注册
        CrossoverMekanismRegister.init(consumer);
        // 资源树-通用机械联动特殊配方注册
        if (ResourceFarmConfigHolder.TreeConfigHolder.enablePresetTreeGroups) {
            PresetResourceTreeConfigHolder.PresetTreeGenerationConfigs configs = ResourceFarmConfigHolder.TreeConfigHolder.presetTreeGeneration;
            if (configs.crossoverMek) CrossoverMekanismRegister.createSpecialRecipe(consumer);
        }
    }
}
