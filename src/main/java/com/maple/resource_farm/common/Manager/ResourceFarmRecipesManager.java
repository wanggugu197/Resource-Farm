package com.maple.resource_farm.common.Manager;

import com.maple.resource_farm.ResourceTree.ResourceTreeAccessManagement;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourceFarmRecipesManager {

    public static Object2IntOpenHashMap<String> treeCommonRecipeCount;

    public static void recipeAddition(RecipeOutput originalConsumer) {
        RecipeOutput consumer = wrapRecipeOutput(originalConsumer);

        treeCommonRecipeCount = new Object2IntOpenHashMap<>();

        ResourceTreeAccessManagement.registerTreeRecipe(consumer);

        treeCommonRecipeCount.clear();
        treeCommonRecipeCount = null;
    }

    private static RecipeOutput wrapRecipeOutput(RecipeOutput originalConsumer) {
        return new RecipeOutput() {

            @Override
            public Advancement.@NotNull Builder advancement() {
                return originalConsumer.advancement();
            }

            @Override
            public void includeRootAdvancement() {
                originalConsumer.includeRootAdvancement();
            }

            @Override
            public void accept(@NotNull ResourceKey<Recipe<?>> id, @NotNull Recipe<?> recipe,
                               @Nullable AdvancementHolder advancement, ICondition @NotNull... conditions) {
                originalConsumer.accept(id, recipe, advancement, conditions);
            }
        };
    }
}
