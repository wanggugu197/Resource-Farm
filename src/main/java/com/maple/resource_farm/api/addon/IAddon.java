package com.maple.resource_farm.api.addon;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;

import com.mapleutillib.api.registry.ModRegistryCore;

import java.util.function.Consumer;

public interface IAddon {

    ModRegistryCore getRegistry();

    void InitComplete();

    /**
     * Call init on your custom IWorldGenLayer class(es) here
     */
    default void registerWorldgenLayers() {}

    /**
     * Call init on your custom VeinGenerator class(es) here
     */
    default void registerVeinGenerators() {}

    /**
     * Call init on your custom IndicatorGenerator class(es) here
     */
    default void registerIndicatorGenerators() {}

    default void addRecipes(RecipeOutput provider) {}

    default void removeRecipes(Consumer<Identifier> consumer) {}

    /**
     * Does this addon require high-tier content to be enabled?
     * 
     * @return if this addon requires highTier.
     */
    default boolean requiresHighTier() {
        return false;
    }
}
