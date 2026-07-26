package com.maple.resource_farm.compat;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.client.ClientInit;
import com.maple.resource_farm.plantPot.ResourcePlantPotRegister;
import com.maple.resource_farm.plantPot.compat.jei.GrowthRecipeCategory;
import com.maple.resource_farm.plantPot.recipe.GrowthRecipe;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import org.jspecify.annotations.NonNull;

import java.util.List;

@JeiPlugin
public class ResourceFarmJeiPlugin implements IModPlugin {

    @Override
    public @NonNull Identifier getPluginUid() {
        return ResourceFarm.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new GrowthRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(GrowthRecipeCategory.GROWTH_RECIPE_TYPE, ResourcePlantPotRegister.BONSAI_POT.asStack());
        registration.addCraftingStation(GrowthRecipeCategory.GROWTH_RECIPE_TYPE, ResourcePlantPotRegister.HOPPING_BONSAI_POT.asStack());
    }

    @Override
    public void registerRecipes(@NonNull IRecipeRegistration registration) {
        var recipeMap = ClientInit.getClientRecipeMap();
        RecipeType<GrowthRecipe> recipeType = ResourcePlantPotRegister.GROWTH.get();

        var holders = recipeMap.byType(recipeType);
        if (holders.isEmpty()) {
            ResourceFarm.LOGGER.warn("[ResourceFarm JEI] No GrowthRecipe found in client cache.");
            return;
        }

        List<GrowthRecipe> recipes = holders.stream().map(RecipeHolder::value).toList();
        registration.addRecipes(GrowthRecipeCategory.GROWTH_RECIPE_TYPE, recipes);
    }

    @Override
    public void onRuntimeAvailable(@NonNull IJeiRuntime runtime) {
        // 无需在此添加配方，所有配方已在 registerRecipes 中注册
        // 如需隐藏调试物品等，可在此实现
    }

    @Override
    public void onRuntimeUnavailable() {
        // 可选资源释放
    }
}
