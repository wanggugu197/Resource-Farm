package com.resource_farm.common.Manager;

import com.resource_farm.data.tree.builder.TreeRecipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourceFarmRecipesManager {

    public static void recipeAddition(RecipeOutput originalConsumer) {
        RecipeOutput consumer = new RecipeOutput() {

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

        TreeRecipe.init(consumer);
    }
}
