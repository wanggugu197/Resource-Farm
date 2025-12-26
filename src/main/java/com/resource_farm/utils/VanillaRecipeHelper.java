package com.resource_farm.utils;

import com.resource_farm.ResourceFarm;

import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.NotNull;

/**
 * 简化配方注册的工具类
 */
public class VanillaRecipeHelper {

    // ===================== 基础烹饪配方封装 =====================
    public static void addSmeltingRecipe(RecipeOutput provider, @NotNull String regName,
                                         TagKey<Item> input, ItemStack output) {
        addSmeltingRecipe(provider, ResourceFarm.id(regName), input, output, 0.0f);
    }

    public static void addSmeltingRecipe(RecipeOutput provider, @NotNull String regName,
                                         Ingredient input, ItemStack output, float experience) {
        addSmeltingRecipe(provider, ResourceFarm.id(regName), input, output, experience);
    }

    public static void addSmeltingRecipe(RecipeOutput provider, @NotNull String regName,
                                         ItemLike input, ItemLike output, float experience) {
        addSmeltingRecipe(provider, ResourceFarm.id(regName),
                Ingredient.of(input), new ItemStack(output), experience);
    }

    public static void addSmeltingRecipe(RecipeOutput provider, @NotNull String regName,
                                         ItemLike input, ItemStack output, float experience) {
        addSmeltingRecipe(provider, ResourceFarm.id(regName),
                Ingredient.of(input), output, experience);
    }

    public static void addSmeltingRecipe(RecipeOutput provider, @NotNull ResourceLocation regName,
                                         TagKey<Item> input, ItemStack output, float experience) {
        addSmeltingRecipe(provider, regName, Ingredient.of(input), output, experience);
    }

    public static void addSmeltingRecipe(RecipeOutput provider, @NotNull ResourceLocation regName,
                                         Ingredient input, ItemStack output, float experience) {
        SimpleCookingRecipeBuilder.smelting(
                input,
                RecipeCategory.MISC,
                output,
                experience,
                200)
                .unlockedBy("has_recipe", RecipeUnlockedTrigger.unlocked(regName))
                .save(provider, regName);
    }

    public static void addBlastingRecipe(RecipeOutput provider, @NotNull String regName,
                                         TagKey<Item> input, ItemStack output) {
        addBlastingRecipe(provider, ResourceFarm.id(regName), Ingredient.of(input), output, 0.0f);
    }

    public static void addBlastingRecipe(RecipeOutput provider, @NotNull String regName,
                                         ItemLike input, ItemLike output, float experience) {
        addBlastingRecipe(provider, ResourceFarm.id(regName),
                Ingredient.of(input), new ItemStack(output), experience);
    }

    public static void addBlastingRecipe(RecipeOutput provider, @NotNull String regName,
                                         ItemLike input, ItemStack output, float experience) {
        addBlastingRecipe(provider, ResourceFarm.id(regName),
                Ingredient.of(input), output, experience);
    }

    public static void addBlastingRecipe(RecipeOutput provider, @NotNull ResourceLocation regName,
                                         Ingredient input, ItemStack output, float experience) {
        SimpleCookingRecipeBuilder.blasting(
                input,
                RecipeCategory.MISC,
                output,
                experience,
                100)
                .unlockedBy("has_recipe", RecipeUnlockedTrigger.unlocked(regName))
                .save(provider, regName);
    }

    public static void addSmokingRecipe(RecipeOutput provider, @NotNull String regName,
                                        ItemStack input, ItemStack output, float experience) {
        addSmokingRecipe(provider, ResourceFarm.id(regName),
                Ingredient.of(input.getItem()), output, experience);
    }

    public static void addSmokingRecipe(RecipeOutput provider, @NotNull String regName,
                                        ItemLike input, ItemLike output, float experience) {
        addSmokingRecipe(provider, ResourceFarm.id(regName),
                Ingredient.of(input), new ItemStack(output), experience);
    }

    public static void addSmokingRecipe(RecipeOutput provider, @NotNull String regName,
                                        ItemLike input, ItemStack output, float experience) {
        addSmokingRecipe(provider, ResourceFarm.id(regName),
                Ingredient.of(input), output, experience);
    }

    public static void addSmokingRecipe(RecipeOutput provider, @NotNull ResourceLocation regName,
                                        Ingredient input, ItemStack output, float experience) {
        SimpleCookingRecipeBuilder.smoking(
                input,
                RecipeCategory.MISC,
                output,
                experience,
                100)
                .unlockedBy("has_recipe", RecipeUnlockedTrigger.unlocked(regName))
                .save(provider, regName);
    }

    public static void addCampfireRecipe(RecipeOutput provider, @NotNull String regName,
                                         ItemStack input, ItemStack output, float experience) {
        addCampfireRecipe(provider, ResourceFarm.id(regName),
                Ingredient.of(input.getItem()), output, experience);
    }

    public static void addCampfireRecipe(RecipeOutput provider, @NotNull ResourceLocation regName,
                                         Ingredient input, ItemStack output, float experience) {
        SimpleCookingRecipeBuilder.campfireCooking(
                input,
                RecipeCategory.MISC,
                output,
                experience,
                600)
                .unlockedBy("has_recipe", RecipeUnlockedTrigger.unlocked(regName))
                .save(provider, regName);
    }

    // ===================== 成型合成配方封装 =====================
    public static void addShapedRecipe(RecipeOutput provider, @NotNull String regName,
                                       @NotNull ItemStack result, @NotNull Object... recipe) {
        addShapedRecipe(provider, ResourceFarm.id(regName), result, recipe);
    }

    public static void addShapedRecipe(RecipeOutput provider, @NotNull ResourceLocation regName,
                                       @NotNull ItemStack result, @NotNull Object... recipe) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(
                RecipeCategory.MISC,
                result.getItem(),
                result.getCount());

        for (int i = 0; i < recipe.length; i++) {
            Object o = recipe[i];
            if (o instanceof String pattern) {
                builder.pattern(pattern);
            } else if (o instanceof Character sign) {
                Object content = recipe[++i];
                switch (content) {
                    case ItemLike itemLike -> builder.define(sign, itemLike);
                    case TagKey<?> key when key.isFor(Registries.ITEM) -> builder.define(sign, (TagKey<Item>) key);
                    case Ingredient ingredient -> builder.define(sign, ingredient);
                    default -> throw new IllegalArgumentException("不支持的配方原料类型: " + content.getClass().getName());
                }
            }
        }
        builder.unlockedBy("has_recipe", RecipeUnlockedTrigger.unlocked(regName));
        builder.save(provider, regName);
    }

    // ===================== 无成型合成配方封装 =====================
    public static void addShapelessRecipe(RecipeOutput provider, @NotNull String regName,
                                          @NotNull ItemStack result, @NotNull Object... recipe) {
        addShapelessRecipe(provider, ResourceFarm.id(regName), result, recipe);
    }

    public static void addShapelessRecipe(RecipeOutput provider, @NotNull ResourceLocation regName,
                                          @NotNull ItemStack result, @NotNull Object... recipe) {
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(
                RecipeCategory.MISC,
                result.getItem(),
                result.getCount());

        for (Object content : recipe) {
            switch (content) {
                case ItemLike itemLike -> builder.requires(itemLike);
                case TagKey<?> key when key.isFor(Registries.ITEM) -> builder.requires((TagKey<Item>) key);
                case Ingredient ingredient -> builder.requires(ingredient);
                default -> throw new IllegalArgumentException("不支持的配方原料类型: " + content.getClass().getName());
            }
        }
        builder.unlockedBy("has_recipe", RecipeUnlockedTrigger.unlocked(regName));
        builder.save(provider, regName);
    }
}
