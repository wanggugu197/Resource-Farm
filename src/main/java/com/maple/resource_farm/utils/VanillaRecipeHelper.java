package com.maple.resource_farm.utils;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
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
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.ItemLike;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 简化配方注册的工具类（Minecraft 26.1+）。
 */
public final class VanillaRecipeHelper {

    public static final int DEFAULT_SMELTING_TIME = 200;
    public static final int DEFAULT_BLASTING_TIME = 100;
    public static final int DEFAULT_SMOKING_TIME = 100;
    public static final int DEFAULT_CAMPFIRE_TIME = 600;

    private VanillaRecipeHelper() {}

    // ===================== 公共工具 =====================

    /**
     * 创建标签原料（要求物品 Tag 已 bind）。
     * <p>
     * 在 {@code MappedRegistry} 标签未绑定时访问会抛 {@code Tags not bound} /
     * {@code UnsupportedOperationException}，故调用方必须在
     * {@code PendingTags#apply} 之后执行配方生成。
     */
    public static Ingredient tagIngredient(TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.get(tag)
                .map(Ingredient::of)
                .orElseThrow(() -> new IllegalStateException(
                        "Item tag not bound or empty during recipe build: " + tag.location() + " (generate recipes only after PendingTags.apply)"));
    }

    /** 将 ItemLike / TagKey / Ingredient 统一为 Ingredient */
    public static Ingredient ingredient(Object input) {
        return switch (input) {
            case null -> throw new IllegalArgumentException("ingredient is null");
            case Ingredient ing -> ing;
            case ItemLike item -> Ingredient.of(item);
            case TagKey<?> key when key.isFor(Registries.ITEM) -> tagIngredient((TagKey<Item>) key);
            default -> throw new IllegalArgumentException("不支持的原料类型: " + input.getClass().getName());
        };
    }

    /** 将 ItemLike / ItemStackTemplate 统一为结果模板 */
    public static ItemStackTemplate result(Object output) {
        return switch (output) {
            case null -> throw new IllegalArgumentException("result is null");
            case ItemStackTemplate template -> template;
            case ItemLike item -> new ItemStackTemplate(item.asItem(), 1);
            default -> throw new IllegalArgumentException("不支持的结果类型: " + output.getClass().getName());
        };
    }

    public static ItemStackTemplate result(Object output, int count) {
        int c = Math.max(1, count);
        return switch (output) {
            case null -> throw new IllegalArgumentException("result is null");
            case ItemStackTemplate template -> new ItemStackTemplate(template.item(), c, template.components());
            case ItemLike item -> new ItemStackTemplate(item.asItem(), c);
            default -> throw new IllegalArgumentException("不支持的结果类型: " + output.getClass().getName());
        };
    }

    private static ResourceKey<Recipe<?>> recipeKey(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }

    private static final InventoryChangeTrigger.TriggerInstance UNCONDITIONAL_TRIGGER_INSTANCE = new InventoryChangeTrigger.TriggerInstance(
            Optional.empty(),
            InventoryChangeTrigger.TriggerInstance.Slots.ANY,
            List.of());

    private static final Criterion<InventoryChangeTrigger.TriggerInstance> UNCONDITIONAL_CRITERION = CriteriaTriggers.INVENTORY_CHANGED.createCriterion(UNCONDITIONAL_TRIGGER_INSTANCE);

    // ===================== 入口 =====================

    public static CookingRecipe smelting(RecipeOutput provider, @NotNull Identifier regName) {
        return new CookingRecipe(provider, regName, CookingKind.SMELTING);
    }

    public static CookingRecipe blasting(RecipeOutput provider, @NotNull Identifier regName) {
        return new CookingRecipe(provider, regName, CookingKind.BLASTING);
    }

    public static CookingRecipe smoking(RecipeOutput provider, @NotNull Identifier regName) {
        return new CookingRecipe(provider, regName, CookingKind.SMOKING);
    }

    public static CookingRecipe campfire(RecipeOutput provider, @NotNull Identifier regName) {
        return new CookingRecipe(provider, regName, CookingKind.CAMPFIRE);
    }

    public static StonecuttingRecipe stonecutting(RecipeOutput provider, @NotNull Identifier regName) {
        return new StonecuttingRecipe(provider, regName);
    }

    public static SmithingTransformRecipe smithing(RecipeOutput provider, @NotNull Identifier regName) {
        return new SmithingTransformRecipe(provider, regName);
    }

    public static ShapedRecipe shaped(RecipeOutput provider, @NotNull Identifier regName) {
        return new ShapedRecipe(provider, regName);
    }

    public static ShapelessRecipe shapeless(RecipeOutput provider, @NotNull Identifier regName) {
        return new ShapelessRecipe(provider, regName);
    }

    // ===================== 烹饪 Builder =====================

    private enum CookingKind {

        SMELTING(DEFAULT_SMELTING_TIME),
        BLASTING(DEFAULT_BLASTING_TIME),
        SMOKING(DEFAULT_SMOKING_TIME),
        CAMPFIRE(DEFAULT_CAMPFIRE_TIME);

        final int defaultTime;

        CookingKind(int defaultTime) {
            this.defaultTime = defaultTime;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static final class CookingRecipe {

        final RecipeOutput provider;
        final Identifier regName;
        final CookingKind kind;

        Ingredient input;
        ItemStackTemplate output;

        @Setter
        @Accessors(fluent = true, chain = true)
        float experience = 0.0f;

        /** null 时使用该烹饪类型的默认时间 */
        @Setter
        @Accessors(fluent = true, chain = true)
        @Nullable
        Integer cookingTime;

        public CookingRecipe input(Object input) {
            this.input = ingredient(input);
            return this;
        }

        public CookingRecipe output(Object output) {
            this.output = result(output);
            return this;
        }

        public CookingRecipe output(Object output, int count) {
            this.output = result(output, count);
            return this;
        }

        public void save() {
            Objects.requireNonNull(input, "cooking recipe input is required: " + regName);
            Objects.requireNonNull(this.output, "cooking recipe output is required: " + regName);
            int time = Math.max(1, cookingTime != null ? cookingTime : kind.defaultTime);
            ResourceKey<Recipe<?>> key = recipeKey(regName);

            SimpleCookingRecipeBuilder builder = switch (kind) {
                case SMELTING -> SimpleCookingRecipeBuilder.smelting(
                        input, RecipeCategory.MISC, CookingBookCategory.MISC, this.output, experience, time);
                case BLASTING -> SimpleCookingRecipeBuilder.blasting(
                        input, RecipeCategory.MISC, CookingBookCategory.MISC, this.output, experience, time);
                case SMOKING -> SimpleCookingRecipeBuilder.smoking(
                        input, RecipeCategory.MISC, this.output, experience, time);
                case CAMPFIRE -> SimpleCookingRecipeBuilder.campfireCooking(
                        input, RecipeCategory.MISC, this.output, experience, time);
            };
            builder.unlockedBy("has_recipe", RecipeUnlockedTrigger.unlocked(key)).save(provider, key);
        }
    }

    // ===================== 切石机 Builder =====================

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static final class StonecuttingRecipe {

        final RecipeOutput provider;
        final Identifier regName;

        Ingredient input;
        ItemStackTemplate output;

        public StonecuttingRecipe input(Object input) {
            this.input = ingredient(input);
            return this;
        }

        public StonecuttingRecipe output(Object output) {
            this.output = result(output);
            return this;
        }

        public StonecuttingRecipe output(Object output, int count) {
            this.output = result(output, count);
            return this;
        }

        public void save() {
            Objects.requireNonNull(input, "stonecutting recipe input is required: " + regName);
            Objects.requireNonNull(output, "stonecutting recipe output is required: " + regName);
            new SingleItemRecipeBuilder(
                    RecipeCategory.MISC,
                    StonecutterRecipe::new,
                    input,
                    output.item().value(),
                    Math.max(1, output.count()))
                    .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                    .save(provider, recipeKey(regName));
        }
    }

    // ===================== 锻造转换 Builder =====================

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static final class SmithingTransformRecipe {

        final RecipeOutput provider;
        final Identifier regName;

        Ingredient template;
        Ingredient base;
        Ingredient addition;
        ItemStackTemplate output;

        public SmithingTransformRecipe template(Object template) {
            this.template = ingredient(template);
            return this;
        }

        public SmithingTransformRecipe base(Object base) {
            this.base = ingredient(base);
            return this;
        }

        public SmithingTransformRecipe addition(Object addition) {
            this.addition = ingredient(addition);
            return this;
        }

        public SmithingTransformRecipe output(Object output) {
            this.output = result(output);
            return this;
        }

        public SmithingTransformRecipe output(Object output, int count) {
            this.output = result(output, count);
            return this;
        }

        public void save() {
            Objects.requireNonNull(template, "smithing template is required: " + regName);
            Objects.requireNonNull(base, "smithing base is required: " + regName);
            Objects.requireNonNull(addition, "smithing addition is required: " + regName);
            Objects.requireNonNull(output, "smithing output is required: " + regName);
            new SmithingTransformRecipeBuilder(template, base, addition, RecipeCategory.MISC, output)
                    .unlocks("unlocked", UNCONDITIONAL_CRITERION)
                    .save(provider, recipeKey(regName));
        }
    }

    // ===================== 成型 / 无成型 Builder =====================

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static final class ShapedRecipe {

        final RecipeOutput provider;
        final Identifier regName;

        ItemStackTemplate output;
        final List<String> patterns = new ArrayList<>();
        final Map<Character, Ingredient> keys = new LinkedHashMap<>();

        public ShapedRecipe output(Object output) {
            this.output = result(output);
            return this;
        }

        public ShapedRecipe output(Object output, int count) {
            this.output = result(output, count);
            return this;
        }

        /** 逐行添加图案 */
        public ShapedRecipe pattern(String... rows) {
            patterns.addAll(Arrays.asList(rows));
            return this;
        }

        /**
         * 兼容旧 varargs 写法：字符串为图案行，{@code 'X', 原料} 为键定义。
         * 原料经 {@link #ingredient(Object)} 转换。
         */
        public ShapedRecipe pattern(Object... recipe) {
            for (int i = 0; i < recipe.length; i++) {
                Object o = recipe[i];
                switch (o) {
                    case String row -> patterns.add(row);
                    case Character sign -> {
                        if (i + 1 >= recipe.length) {
                            throw new IllegalArgumentException("shaped key '" + sign + "' missing ingredient: " + regName);
                        }
                        keys.put(sign, ingredient(recipe[++i]));
                    }
                    case null -> throw new IllegalArgumentException("shaped pattern element is null: " + regName);
                    default -> throw new IllegalArgumentException(
                            "shaped pattern expects String or Character, got: " + o.getClass().getName());
                }
            }
            return this;
        }

        public ShapedRecipe define(char key, Object input) {
            keys.put(key, ingredient(input));
            return this;
        }

        public void save() {
            Objects.requireNonNull(output, "shaped recipe output is required: " + regName);
            if (patterns.isEmpty()) {
                throw new IllegalStateException("shaped recipe pattern is empty: " + regName);
            }
            ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(
                    BuiltInRegistries.ITEM,
                    RecipeCategory.MISC,
                    output);
            for (String row : patterns) {
                builder.pattern(row);
            }
            keys.forEach(builder::define);
            builder.unlockedBy("unlocked", UNCONDITIONAL_CRITERION);
            builder.save(provider, recipeKey(regName));
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static final class ShapelessRecipe {

        final RecipeOutput provider;
        final Identifier regName;

        ItemStackTemplate output;
        final List<Ingredient> inputs = new ArrayList<>();

        public ShapelessRecipe output(Object output) {
            this.output = result(output);
            return this;
        }

        public ShapelessRecipe output(Object output, int count) {
            this.output = result(output, count);
            return this;
        }

        public ShapelessRecipe requires(Object input) {
            inputs.add(ingredient(input));
            return this;
        }

        public ShapelessRecipe requires(Object input, int count) {
            Ingredient ing = ingredient(input);
            for (int i = 0; i < Math.max(1, count); i++) {
                inputs.add(ing);
            }
            return this;
        }

        /** 多个原料，每个经 {@link #ingredient(Object)} 转换 */
        public ShapelessRecipe requires(Object... inputs) {
            for (Object input : inputs) {
                requires(input);
            }
            return this;
        }

        public void save() {
            Objects.requireNonNull(output, "shapeless recipe output is required: " + regName);
            if (inputs.isEmpty()) {
                throw new IllegalStateException("shapeless recipe requires at least one ingredient: " + regName);
            }
            ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(
                    BuiltInRegistries.ITEM,
                    RecipeCategory.MISC,
                    output);
            for (Ingredient input : inputs) {
                builder.requires(input);
            }
            builder.unlockedBy("unlocked", UNCONDITIONAL_CRITERION);
            builder.save(provider, recipeKey(regName));
        }
    }
}
