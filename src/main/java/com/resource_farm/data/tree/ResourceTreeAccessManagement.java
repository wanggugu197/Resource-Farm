package com.resource_farm.data.tree;

import com.resource_farm.config.PresetResourceTreeConfigHolder;
import com.resource_farm.config.ResourceFarmConfigHolder;
import com.resource_farm.config.TreeRegisterConfig;
import com.resource_farm.config.TreeRemoveConfig;
import com.resource_farm.data.tree.RegisterAndSpecialRecipe.TreesCommonRegister;
import com.resource_farm.data.tree.builder.TreeCommonRecipe;

import net.minecraft.data.recipes.RecipeOutput;

public class ResourceTreeAccessManagement {

    public static void registerTree() {
        // 资源树-基础注册
        if (ResourceFarmConfigHolder.presetTreeConfigHolder.enablePresetTreeGroups) {
            PresetResourceTreeConfigHolder.PresetTreeGenerationConfigs configs = ResourceFarmConfigHolder.presetTreeConfigHolder.presetTreeGeneration;
            if (configs.minecraftBase) TreesCommonRegister.base();
            if (configs.minecraftMineral) TreesCommonRegister.mineral();
            if (configs.minecraftBiology) TreesCommonRegister.biology();
            if (configs.minecraftAgriculture) TreesCommonRegister.agriculture();
        }

        // 资源树-配置文件注册
        TreeRegisterConfig.parseJSONtoRegisterTree();
        // 资源树-配置文件移除
        TreeRemoveConfig.parseJSONtoRemoveTree();
    }

    public static void registerTreeRecipe(RecipeOutput consumer) {
        // 资源树-通用配方注册
        TreeCommonRecipe.init(consumer);
        // 资源树-特殊配方注册
        if (ResourceFarmConfigHolder.presetTreeConfigHolder.enablePresetTreeGroups) {
            PresetResourceTreeConfigHolder.PresetTreeGenerationConfigs configs = ResourceFarmConfigHolder.presetTreeConfigHolder.presetTreeGeneration;
            if (configs.minecraftBase) TreesCommonRegister.baseSpecialRecipe(consumer);
            if (configs.minecraftBiology) TreesCommonRegister.biologySpecialRecipe(consumer);
            if (configs.minecraftAgriculture) TreesCommonRegister.agricultureSpecialRecipe(consumer);
        }
    }
}
