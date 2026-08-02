package com.maple.resource_farm.common.manager;

import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;

import net.minecraft.data.recipes.RecipeOutput;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class ResourceFarmRecipesManager {

    public static Object2IntOpenHashMap<String> treeCommonRecipeCount;

    public static void recipeAddition(RecipeOutput consumer) {
        treeCommonRecipeCount = new Object2IntOpenHashMap<>();

        ResourceTreeAccessManagement.registerTreeRecipe(consumer);

        treeCommonRecipeCount.clear();
        treeCommonRecipeCount = null;
    }
}
