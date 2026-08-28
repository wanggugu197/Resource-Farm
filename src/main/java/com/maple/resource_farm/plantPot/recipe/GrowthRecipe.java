package com.maple.resource_farm.plantPot.recipe;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.plantPot.ResourcePlantPotRegister;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.gui.ui.utils.IModularUIProvider;
import com.lowdragmc.lowdraglib2.integration.xei.IngredientIO;
import com.mapleutillib.utils.RLUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.FlexWrap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @param renderer "crop" 或 ""
 */

public record GrowthRecipe(
                           String renderer,
                           int duration,
                           boolean fertilizable,
                           Ingredient seed,
                           List<Ingredient> soils,
                           List<OutputEntry> output)
        implements Recipe<SingleRecipeInput>, IModularUIProvider<GrowthRecipe> {

    public boolean matchesSoil(ItemStack stack) {
        for (Ingredient soil : soils) {
            if (soil.test(stack)) return true;
        }
        return false;
    }

    @Override
    public boolean matches(SingleRecipeInput input, @NotNull Level level) {
        return seed.test(input.item());
    }

    @Override
    public ItemStack assemble(@NotNull SingleRecipeInput input, @NotNull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NotNull String getGroup() {
        return "";
    }

    @Override
    public @NotNull RecipeSerializer<GrowthRecipe> getSerializer() {
        return ResourcePlantPotRegister.GROWTH.getSerializer();
    }

    @Override
    public @NotNull RecipeType<GrowthRecipe> getType() {
        return ResourcePlantPotRegister.GROWTH.get();
    }

    public static final MapCodec<GrowthRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("renderer", "").forGetter(GrowthRecipe::renderer),
            Codec.INT.optionalFieldOf("duration", 1200).forGetter(GrowthRecipe::duration),
            Codec.BOOL.optionalFieldOf("fertilizable", true).forGetter(GrowthRecipe::fertilizable),
            Ingredient.CODEC.fieldOf("seed").forGetter(GrowthRecipe::seed),
            Ingredient.CODEC.listOf().fieldOf("soils").forGetter(GrowthRecipe::soils),
            OutputEntry.CODEC.listOf().fieldOf("output").forGetter(GrowthRecipe::output))
            .apply(instance, GrowthRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GrowthRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, GrowthRecipe::renderer,
            ByteBufCodecs.INT, GrowthRecipe::duration,
            ByteBufCodecs.BOOL, GrowthRecipe::fertilizable,
            Ingredient.CONTENTS_STREAM_CODEC, GrowthRecipe::seed,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), GrowthRecipe::soils,
            OutputEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), GrowthRecipe::output,
            GrowthRecipe::new);

    // ========== IModularUIProvider 实现 ==========
    public static final int JEI_WIDTH = 126;
    public static final int JEI_HEIGHT = 56;

    @Override
    public ModularUI createModularUI(GrowthRecipe recipe) {
        // 根容器：水平排列（物品 → 箭头 → 掉落物网格）
        var root = new UIElement()
                .layout(l -> l.flexDirection(FlexDirection.ROW)
                        .alignItems(AlignItems.CENTER)
                        .gapAll(4)
                        .paddingAll(2));

        // ---- 左侧：种子 + 土壤（垂直排列） ----
        var inputs = new UIElement()
                .layout(l -> l.flexDirection(FlexDirection.COLUMN)
                        .gapAll(3)
                        .alignItems(AlignItems.CENTER));

        // 种子槽位
        var seedItems = Stream.of(recipe.seed().getItems()).toList();
        var seedSlot = new ItemSlot()
                .setItem(seedItems.getFirst())
                .xeiRecipeIngredient(IngredientIO.INPUT, seedItems::stream)
                .xeiRecipeSlot(IngredientIO.INPUT, 1f, 1, seedItems::stream);
        seedSlot.style(style -> {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("jei.resource_farm.recipe.seed.during", recipe.duration() / 20));
            if (!recipe.fertilizable()) list.add(Component.translatable("jei.resource_farm.recipe.seed.fertilizable"));
            style.appendTooltips(list.toArray(new Component[0]));
        });
        seedSlot.bindDataSource(SupplierDataSource.of(() -> {
            var mui = seedSlot.getModularUI();
            return seedItems.get(((mui == null ? 0 : (int) mui.getTickCounter()) / 20) % seedItems.size());
        }), true);
        inputs.addChild(seedSlot);

        // ---- 加号指示 ----
        var cross = new UIElement()
                .layout(l -> l.width(15).height(15))
                .style(s -> s.background(SpriteTexture.of(ResourceFarm.id("textures/gui/cross.png"))));
        inputs.addChild(cross);

        // 土壤槽位
        var soilItems = recipe.soils().stream()
                .flatMap(ing -> Stream.of(ing.getItems()))
                .toList();
        var soilSlot = new ItemSlot()
                .setItem(soilItems.getFirst())
                .xeiRecipeIngredient(IngredientIO.INPUT, soilItems::stream)
                .xeiRecipeSlot(IngredientIO.INPUT, 1f, 1, soilItems::stream);
        soilSlot.bindDataSource(SupplierDataSource.of(() -> {
            var mui = soilSlot.getModularUI();
            return soilItems.get(((mui == null ? 0 : (int) mui.getTickCounter()) / 20) % soilItems.size());
        }), true);
        inputs.addChild(soilSlot);

        root.addChild(inputs);

        // ---- 中间：箭头指示 ----
        var arrow = new UIElement()
                .layout(l -> l.width(22).height(15))
                .style(s -> s.background(SpriteTexture.of(ResourceFarm.id("textures/gui/arrow.png"))));
        root.addChild(arrow);

        // ---- 右侧：掉落物网格（4列） ----
        var dropsGrid = new UIElement()
                .layout(l -> l.flexDirection(FlexDirection.ROW)
                        .flexWrap(FlexWrap.WRAP)
                        .width(18 * 4));

        int index = 0;
        for (OutputEntry entry : recipe.output()) {
            if (index >= 12) break;
            int avgCount = (entry.min() + entry.max()) / 2;
            if (avgCount < 1) avgCount = 1;
            ItemStack displayStack = new ItemStack(entry.item(), avgCount);
            var slot = new ItemSlot()
                    .setItem(displayStack)
                    .xeiRecipeIngredient(IngredientIO.OUTPUT, () -> Stream.of(displayStack))
                    .xeiRecipeSlot(IngredientIO.OUTPUT, entry.chance(), avgCount, () -> Stream.of(displayStack));
            slot.style(style -> {
                List<Component> list = new ArrayList<>();
                if (entry.min() != entry.max()) list.add(Component.translatable("jei.resource_farm.recipe.drop.count_between", entry.min(), entry.max()));
                else list.add(Component.translatable("jei.resource_farm.recipe.drop.count", entry.min()));
                if (entry.chance() < 1.0f) list.add(Component.translatable("jei.resource_farm.recipe.drop.chance", String.format("%.2f%%", entry.chance() * 100)));
                style.appendTooltips(list.toArray(new Component[0]));
            });
            dropsGrid.addChild(slot);
            index++;
        }
        root.addChild(dropsGrid);

        // ---- 组装 UI ----
        return ModularUI.of(UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC_MERGED))), null);
    }

    // ========== 静态构建器入口 ==========
    public static Builder builder(RecipeOutput provider) {
        return Builder.builder(provider);
    }

    // ========== Builder 优化 ==========
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Builder {

        final RecipeOutput provider;

        @Setter
        @Accessors(fluent = true, chain = true)
        ResourceLocation id;
        @Setter
        @Accessors(fluent = true, chain = true)
        String renderer = "";
        @Setter
        @Accessors(fluent = true, chain = true)
        boolean fertilizable = true;
        @Setter
        @Accessors(fluent = true, chain = true)
        int duration = 1200;
        @Setter
        @Accessors(fluent = true, chain = true)
        Ingredient seed = null;
        @Setter
        @Accessors(fluent = true, chain = true)
        List<Ingredient> soils = new ArrayList<>();
        @Setter
        @Accessors(fluent = true, chain = true)
        List<OutputEntry> outputs = new ArrayList<>();

        // ★ 公开静态工厂方法
        public static Builder builder(RecipeOutput provider) {
            return new Builder(provider);
        }

        public Builder addSoil(Ingredient soil) {
            this.soils.add(soil);
            return this;
        }

        public Builder addOutput(OutputEntry output) {
            this.outputs.add(output);
            return this;
        }

        /**
         * 将构建的配方注册到 RecipeOutput 中。
         * 如果未设置 id，则基于种子物品自动生成。
         * 
         * @throws IllegalStateException 如果配方不完整（种子、土壤、产出缺失）
         */
        public void register() {
            if (seed == null || soils.isEmpty() || outputs.isEmpty()) {
                throw new IllegalStateException("Growth recipe is incomplete: seed, soils, and outputs must be set.");
            }
            if (id == null) {
                var items = seed.getItems();
                var item = items[0].getItem();
                var registryName = BuiltInRegistries.ITEM.getKey(item);
                id = RLUtils.get(registryName.getNamespace(),
                        registryName.getPath() + "_growth");
            }
            var recipe = new GrowthRecipe(renderer, duration, fertilizable, seed, List.copyOf(soils), List.copyOf(outputs));
            provider.accept(id, recipe, null);
        }
    }
}
