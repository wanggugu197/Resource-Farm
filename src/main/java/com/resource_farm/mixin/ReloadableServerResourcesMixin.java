package com.resource_farm.mixin;

import com.resource_farm.ResourceFarm;
import com.resource_farm.common.Manager.ResourceFarmComposTablesManager;
import com.resource_farm.common.Manager.ResourceFarmLootTablesManager;
import com.resource_farm.common.Manager.ResourceFarmRecipesManager;
import com.resource_farm.common.pack.ResourceFarmDynamicDataPack;
import com.resource_farm.common.pack.crossover.CreateDynamicDataPack;
import com.resource_farm.common.pack.crossover.MekanismDynamicDataPack;

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
        long globalStartTime = System.currentTimeMillis();
        ResourceFarm.LOGGER.info("=== Resource Farm 数据加载开始 ===");

        RegistryAccess.Frozen frozen = access.compositeAccess();

        long step1StartTime = System.currentTimeMillis();
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
        ResourceFarm.LOGGER.info("MC原版配方添加完成，耗时 {}ms", System.currentTimeMillis() - step1StartTime);

        if (ResourceFarm.isCreateLoaded()) {
            long step2StartTime = System.currentTimeMillis();
            ResourceFarmRecipesManager.createRecipeAddition(new RecipeOutput() {

                @Override
                public Advancement.@NotNull Builder advancement() {
                    return Advancement.Builder.recipeAdvancement();
                }

                @Override
                public void accept(@NotNull ResourceLocation id, @NotNull Recipe<?> recipe,
                                   @Nullable AdvancementHolder advancement, ICondition @NotNull... conditions) {
                    CreateDynamicDataPack.addCreateRecipe(id, recipe, frozen);
                }
            });
            ResourceFarm.LOGGER.info("机械动力配方添加完成，耗时 {}ms", System.currentTimeMillis() - step2StartTime);
        }

        if (ResourceFarm.isMekLoaded()) {
            long step3StartTime = System.currentTimeMillis();
            ResourceFarmRecipesManager.mekanismRecipeAddition(new RecipeOutput() {

                @Override
                public Advancement.@NotNull Builder advancement() {
                    return Advancement.Builder.recipeAdvancement();
                }

                @Override
                public void accept(@NotNull ResourceLocation id, @NotNull Recipe<?> recipe,
                                   @Nullable AdvancementHolder advancement, ICondition @NotNull... conditions) {
                    MekanismDynamicDataPack.addMekanismRecipe(id, recipe, frozen);
                }
            });
            ResourceFarm.LOGGER.info("通用机械配方添加完成，耗时 {}ms", System.currentTimeMillis() - step3StartTime);
        }

        long step4StartTime = System.currentTimeMillis();
        ResourceFarmComposTablesManager.buildComposTablesData(frozen);
        ResourceFarm.LOGGER.info("构建堆肥桶表数据完成，耗时 {}ms", System.currentTimeMillis() - step4StartTime);

        long step5StartTime = System.currentTimeMillis();
        ResourceFarmLootTablesManager.generateComposTablesLoot(ResourceFarmDynamicDataPack::addLootTable, frozen);
        ResourceFarm.LOGGER.info("构建战利品数据完成，耗时 {}ms", System.currentTimeMillis() - step5StartTime);

        ResourceFarm.LOGGER.info("Resource Farm Data loading took {}ms", System.currentTimeMillis() - globalStartTime);
        ResourceFarm.LOGGER.info("=== Resource Farm 数据加载结束 ===");
    }
}
