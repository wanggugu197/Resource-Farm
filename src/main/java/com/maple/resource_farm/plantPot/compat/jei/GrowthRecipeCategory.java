package com.maple.resource_farm.plantPot.compat.jei;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.plantPot.ResourcePlantPotRegister;
import com.maple.resource_farm.plantPot.recipe.GrowthRecipe;

import net.minecraft.network.chat.Component;

import com.lowdragmc.lowdraglib2.integration.xei.jei.ModularUIRecipeCategory;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;

import javax.annotation.Nonnull;

public class GrowthRecipeCategory extends ModularUIRecipeCategory<GrowthRecipe> {

    public static final RecipeType<GrowthRecipe> GROWTH_RECIPE_TYPE = new RecipeType<>(ResourceFarm.id("growth"), GrowthRecipe.class);

    private final IDrawable icon;

    public GrowthRecipeCategory(IGuiHelper guiHelper) {
        super(recipe -> recipe.createModularUI(recipe));
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ResourcePlantPotRegister.HOPPING_BONSAI_POT.asStack());
    }

    @Override
    public @Nonnull RecipeType<GrowthRecipe> getRecipeType() {
        return GROWTH_RECIPE_TYPE;
    }

    @Override
    public @Nonnull Component getTitle() {
        return Component.translatable("jei.resource_farm.growth_recipe");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return GrowthRecipe.JEI_WIDTH;
    }

    @Override
    public int getHeight() {
        return GrowthRecipe.JEI_HEIGHT;
    }
}
