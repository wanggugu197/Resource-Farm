package com.maple.resource_farm.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {

    @Accessor("recipes")
    RecipeMap resource_farm$getRecipes();

    @Accessor("recipes")
    void resource_farm$setRecipes(RecipeMap recipes);

    @Accessor("registries")
    HolderLookup.Provider resource_farm$getRegistries();
}
