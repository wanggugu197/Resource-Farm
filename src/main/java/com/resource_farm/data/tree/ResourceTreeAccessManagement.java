package com.resource_farm.data.tree;

import com.resource_farm.config.TreeRegisterConfig;
import com.resource_farm.config.TreeRemoveConfig;
import com.resource_farm.data.tree.RegisterAndSpecialRecipe.TreesCommonRegister;
import com.resource_farm.data.tree.builder.TreeCommonRecipe;

import net.minecraft.data.recipes.RecipeOutput;

public class ResourceTreeAccessManagement {

    public static void registerTree() {
        // 资源树-基础注册
        TreesCommonRegister.base();
        TreesCommonRegister.mineral();

        // 资源树-配置文件注册
        TreeRegisterConfig.parseJSONtoRegisterTree();
        // 资源树-配置文件移除
        TreeRemoveConfig.parseJSONtoRemoveTree();
    }

    public static void registerTreeRecipe(RecipeOutput consumer) {
        // 资源树-基础配方注册
        TreeCommonRecipe.init(consumer);
    }
}
