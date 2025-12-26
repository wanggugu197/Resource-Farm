package com.resource_farm.mixin;

import com.resource_farm.ResourceFarm;
import com.resource_farm.common.Manager.ResourceFarmComposTablesManager;
import com.resource_farm.common.Manager.ResourceFarmLootTablesManager;
import com.resource_farm.common.Manager.ResourceFarmRecipesManager;
import com.resource_farm.common.pack.ResourceFarmDynamicDataPack;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = ReloadableServerResources.class, priority = 100)
public abstract class ReloadableServerResourcesMixin {

    @Inject(method = "loadResources", at = @At("HEAD"))
    private static void init(ResourceManager resourceManager, LayeredRegistryAccess<RegistryLayer> access,
                             FeatureFlagSet featureFlags, Commands.CommandSelection commands,
                             int functionCompilationLevel, Executor backgroundExecutor, Executor gameExecutor,
                             CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir) {
        RegistryAccess.Frozen frozen = access.compositeAccess();

        long startTime = System.currentTimeMillis();

        ResourceFarmRecipesManager.recipeAddition(new RecipeOutput() {

            @Override
            public Advancement.@NotNull Builder advancement() {
                return Advancement.Builder.recipeAdvancement();
            }

            @Override
            public void accept(@NotNull ResourceLocation id, @NotNull Recipe<?> recipe,
                               @Nullable AdvancementHolder advancement, ICondition @NotNull... conditions) {
                ResourceFarmDynamicDataPack.addRecipe(id, recipe, advancement, frozen);
            }
        });
        ResourceFarmComposTablesManager.buildComposTablesData(frozen);
        ResourceFarmLootTablesManager.generateComposTablesLoot(ResourceFarmDynamicDataPack::addLootTable, frozen);

        ResourceFarm.LOGGER.info("Resource Farm Data loading took {}ms", System.currentTimeMillis() - startTime);
    }
}
