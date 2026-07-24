package com.maple.resource_farm.utils;

import com.maple.resource_farm.ResourceFarm;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * 简化配方注册的工具类（Minecraft 26.1+）。
 * <p>
 * 动态数据包注入发生在 {@code ReloadableServerResources#loadResources} 早期：
 * <ul>
 * <li>禁止构造 {@link net.minecraft.world.item.ItemStack}（Data Components 可能未绑定 → NPE）</li>
 * <li>禁止 {@code HolderGetter#getOrThrow(TagKey)}（标签可能尚未解析 → Missing tag）</li>
 * </ul>
 * 结果用 {@link ItemStackTemplate}；标签原料用 {@link #tagIngredient(TagKey)} 延迟解析。
 */
public class VanillaRecipeHelper {

    /**
     * 创建<strong>不立即解析</strong>的标签原料。
     * <p>
     * {@link ShapedRecipeBuilder#define(Character, TagKey)} 内部会调用
     * {@code items.getOrThrow(tag)}，在 loadResources 早期会抛 Missing tag。
     * 这里用 {@link HolderSet#emptyNamed} 只携带 TagKey，序列化进 JSON 后由游戏在标签就绪时再绑定。
     */
    public static Ingredient tagIngredient(TagKey<Item> tag) {
        return Ingredient.of(HolderSet.emptyNamed(BuiltInRegistries.ITEM, tag));
    }

    /** 安全的结果模板：不触碰 Item 默认组件绑定 */
    public static ItemStackTemplate result(ItemLike item) {
        return new ItemStackTemplate(item.asItem(), 1);
    }

    public static ItemStackTemplate result(ItemLike item, int count) {
        return new ItemStackTemplate(item.asItem(), Math.max(1, count));
    }

    private static ResourceKey<Recipe<?>> recipeKey(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }

    private static ResourceKey<Recipe<?>> recipeKey(String regName) {
        return recipeKey(ResourceFarm.id(regName));
    }

    // ===================== 基础烹饪配方封装 =====================
    public static void addSmeltingRecipe(RecipeOutput provider, @NotNull String regName,
                                         TagKey<Item> input, ItemLike output) {
        addSmeltingRecipe(provider, regName, tagIngredient(input), result(output), 0.0f);
    }

    public static void addSmeltingRecipe(RecipeOutput provider, @NotNull String regName,
                                         Ingredient input, ItemLike output, float experience) {
        addSmeltingRecipe(provider, regName, input, result(output), experience);
    }

    public static void addSmeltingRecipe(RecipeOutput provider, @NotNull String regName,
                                         ItemLike input, ItemLike output, float experience) {
        addSmeltingRecipe(provider, regName, Ingredient.of(input), result(output), experience);
    }

    public static void addSmeltingRecipe(RecipeOutput provider, @NotNull String regName,
                                         Ingredient input, ItemStackTemplate output, float experience) {
        SimpleCookingRecipeBuilder.smelting(
                input,
                RecipeCategory.MISC,
                CookingBookCategory.MISC,
                output,
                experience,
                200)
                .unlockedBy("has_recipe", RecipeUnlockedTrigger.unlocked(recipeKey(regName)))
                .save(provider, recipeKey(regName));
    }

    public static void addBlastingRecipe(RecipeOutput provider, @NotNull String regName,
                                         TagKey<Item> input, ItemLike output) {
        addBlastingRecipe(provider, regName, tagIngredient(input), result(output), 0.0f);
    }

    public static void addBlastingRecipe(RecipeOutput provider, @NotNull String regName,
                                         ItemLike input, ItemLike output, float experience) {
        addBlastingRecipe(provider, regName, Ingredient.of(input), result(output), experience);
    }

    public static void addBlastingRecipe(RecipeOutput provider, @NotNull String regName,
                                         Ingredient input, ItemStackTemplate output, float experience) {
        SimpleCookingRecipeBuilder.blasting(
                input,
                RecipeCategory.MISC,
                CookingBookCategory.MISC,
                output,
                experience,
                100)
                .unlockedBy("has_recipe", RecipeUnlockedTrigger.unlocked(recipeKey(regName)))
                .save(provider, recipeKey(regName));
    }

    public static void addSmokingRecipe(RecipeOutput provider, @NotNull String regName,
                                        ItemLike input, ItemLike output, float experience) {
        addSmokingRecipe(provider, regName, Ingredient.of(input), result(output), experience);
    }

    public static void addSmokingRecipe(RecipeOutput provider, @NotNull String regName,
                                        Ingredient input, ItemLike output, float experience) {
        addSmokingRecipe(provider, regName, input, result(output), experience);
    }

    public static void addSmokingRecipe(RecipeOutput provider, @NotNull String regName,
                                        Ingredient input, ItemLike output, int count, float experience) {
        addSmokingRecipe(provider, regName, input, result(output, count), experience);
    }

    public static void addSmokingRecipe(RecipeOutput provider, @NotNull String regName,
                                        Ingredient input, ItemStackTemplate output, float experience) {
        SimpleCookingRecipeBuilder.smoking(
                input,
                RecipeCategory.MISC,
                output,
                experience,
                100)
                .unlockedBy("has_recipe", RecipeUnlockedTrigger.unlocked(recipeKey(regName)))
                .save(provider, recipeKey(regName));
    }

    public static void addCampfireRecipe(RecipeOutput provider, @NotNull String regName,
                                         ItemLike input, ItemLike output, float experience) {
        addCampfireRecipe(provider, regName, Ingredient.of(input), result(output), experience);
    }

    public static void addCampfireRecipe(RecipeOutput provider, @NotNull String regName,
                                         Ingredient input, ItemStackTemplate output, float experience) {
        SimpleCookingRecipeBuilder.campfireCooking(
                input,
                RecipeCategory.MISC,
                output,
                experience,
                600)
                .unlockedBy("has_recipe", RecipeUnlockedTrigger.unlocked(recipeKey(regName)))
                .save(provider, recipeKey(regName));
    }

    private static final InventoryChangeTrigger.TriggerInstance UNCONDITIONAL_TRIGGER_INSTANCE = new InventoryChangeTrigger.TriggerInstance(
            Optional.empty(),
            InventoryChangeTrigger.TriggerInstance.Slots.ANY,
            List.of());

    private static final Criterion<InventoryChangeTrigger.TriggerInstance> UNCONDITIONAL_CRITERION = CriteriaTriggers.INVENTORY_CHANGED
            .createCriterion(UNCONDITIONAL_TRIGGER_INSTANCE);

    // ===================== 成型 / 无成型：结果只用 ItemStackTemplate，避免 (ItemLike,int,Object...) 歧义 =====================
    public static void addShapedRecipe(RecipeOutput provider, @NotNull String regName,
                                       @NotNull ItemStackTemplate result, @NotNull Object... recipe) {
        addShapedRecipe(provider, ResourceFarm.id(regName), result, recipe);
    }

    public static void addShapedRecipe(RecipeOutput provider, @NotNull Identifier regName,
                                       @NotNull ItemStackTemplate result, @NotNull Object... recipe) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(
                BuiltInRegistries.ITEM,
                RecipeCategory.MISC,
                result);

        for (int i = 0; i < recipe.length; i++) {
            Object o = recipe[i];
            if (o instanceof String pattern) {
                builder.pattern(pattern);
            } else if (o instanceof Character sign) {
                Object content = recipe[++i];
                switch (content) {
                    case ItemLike itemLike -> builder.define(sign, itemLike);
                    case Ingredient ingredient -> builder.define(sign, ingredient);
                    // 不可用 define(TagKey)：内部 getOrThrow 会在标签未加载时崩溃
                    case TagKey<?> key when key.isFor(Registries.ITEM) -> builder.define(sign, tagIngredient((TagKey<Item>) key));
                    default -> throw new IllegalArgumentException("不支持的配方原料类型: " + content.getClass().getName());
                }
            }
        }
        builder.unlockedBy("unlocked", UNCONDITIONAL_CRITERION);
        builder.save(provider, recipeKey(regName));
    }

    public static void addShapelessRecipe(RecipeOutput provider, @NotNull String regName,
                                          @NotNull ItemStackTemplate result, @NotNull Object... recipe) {
        addShapelessRecipe(provider, ResourceFarm.id(regName), result, recipe);
    }

    public static void addShapelessRecipe(RecipeOutput provider, @NotNull Identifier regName,
                                          @NotNull ItemStackTemplate result, @NotNull Object... recipe) {
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(
                BuiltInRegistries.ITEM,
                RecipeCategory.MISC,
                result);

        for (Object content : recipe) {
            switch (content) {
                case ItemLike itemLike -> builder.requires(itemLike);
                case Ingredient ingredient -> builder.requires(ingredient);
                // 不可用 requires(TagKey)：同样会立即解析标签
                case TagKey<?> key when key.isFor(Registries.ITEM) -> builder.requires(tagIngredient((TagKey<Item>) key));
                default -> throw new IllegalArgumentException("不支持的配方原料类型: " + content.getClass().getName());
            }
        }
        builder.unlockedBy("unlocked", UNCONDITIONAL_CRITERION);
        builder.save(provider, recipeKey(regName));
    }
}
