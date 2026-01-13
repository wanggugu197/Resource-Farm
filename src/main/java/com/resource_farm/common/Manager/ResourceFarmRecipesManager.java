package com.resource_farm.common.Manager;

import com.resource_farm.data.tree.ResourceTreeAccessManagement;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
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

    public static void createRecipeAddition(RecipeOutput originalConsumer) {
        RecipeOutput consumer = wrapRecipeOutput(originalConsumer);

        ResourceTreeAccessManagement.registerCreateTreeRecipe(consumer);
    }

    public static void mekanismRecipeAddition(RecipeOutput originalConsumer) {
        RecipeOutput consumer = wrapRecipeOutput(originalConsumer);

        ResourceTreeAccessManagement.registerMekanismTreeRecipe(consumer);
    }

    private static RecipeOutput wrapRecipeOutput(RecipeOutput originalConsumer) {
        return new RecipeOutput() {

            @Override
            public Advancement.@NotNull Builder advancement() {
                return originalConsumer.advancement();
            }

            @Override
            public void accept(@NotNull ResourceLocation id, @NotNull Recipe<?> recipe,
                               @Nullable AdvancementHolder advancement, ICondition @NotNull... conditions) {
                originalConsumer.accept(id, recipe, advancement, conditions);
            }
        };
    }
}
