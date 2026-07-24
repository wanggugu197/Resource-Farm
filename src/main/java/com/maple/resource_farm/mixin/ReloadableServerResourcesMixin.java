package com.maple.resource_farm.mixin;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.common.Manager.ResourceFarmComposTablesManager;
import com.maple.resource_farm.common.Manager.ResourceFarmLootTablesManager;
import com.maple.resource_farm.common.Manager.ResourceFarmRecipesManager;
import com.maple.resource_farm.common.pack.ResourceFarmDynamicDataPack;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 在服务端数据包加载前，将动态配方/战利品/堆肥表写入 {@link ResourceFarmDynamicDataPack}。
 * 26.1.2 签名：loadResources(ResourceManager, LayeredRegistryAccess, List&lt;PendingTags&gt;, FeatureFlagSet,
 * CommandSelection, PermissionSet, Executor, Executor)
 */
@Mixin(value = ReloadableServerResources.class, priority = 100)
public abstract class ReloadableServerResourcesMixin {

    @Inject(method = "loadResources", at = @At("HEAD"))
    private static void resource_farm$injectDynamicData(
                                                        ResourceManager resourceManager,
                                                        LayeredRegistryAccess<RegistryLayer> contextLayers,
                                                        List<Registry.PendingTags<?>> updatedContextTags,
                                                        FeatureFlagSet enabledFeatures,
                                                        Commands.CommandSelection commandSelection,
                                                        PermissionSet functionCompilationPermissions,
                                                        Executor backgroundExecutor,
                                                        Executor mainThreadExecutor,
                                                        CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir) {
        long globalStartTime = System.currentTimeMillis();
        ResourceFarm.LOGGER.info("=== Resource Farm 数据加载开始 ===");

        RegistryAccess.Frozen frozen = contextLayers.compositeAccess();

        long step1StartTime = System.currentTimeMillis();
        ResourceFarmRecipesManager.recipeAddition(new RecipeOutput() {

            @Override
            public Advancement.@NotNull Builder advancement() {
                return Advancement.Builder.recipeAdvancement();
            }

            @Override
            public void includeRootAdvancement() {
                // Dynamic pack does not need root recipe advancement.
            }

            @Override
            public void accept(@NotNull ResourceKey<Recipe<?>> id, @NotNull Recipe<?> recipe,
                               @Nullable AdvancementHolder advancement, ICondition @NotNull... conditions) {
                ResourceFarmDynamicDataPack.addRecipe(id.identifier(), recipe, advancement, frozen);
            }
        });
        ResourceFarm.LOGGER.info("MC原版配方添加完成，耗时 {}ms", System.currentTimeMillis() - step1StartTime);

        long step2StartTime = System.currentTimeMillis();
        ResourceFarmComposTablesManager.buildComposTablesData(frozen);
        ResourceFarm.LOGGER.info("构建堆肥桶表数据完成，耗时 {}ms", System.currentTimeMillis() - step2StartTime);

        long step3StartTime = System.currentTimeMillis();
        ResourceFarmLootTablesManager.generateComposTablesLoot(ResourceFarmDynamicDataPack::addLootTable, frozen);
        ResourceFarm.LOGGER.info("构建战利品数据完成，耗时 {}ms", System.currentTimeMillis() - step3StartTime);

        ResourceFarm.LOGGER.info("Resource Farm Data loading took {}ms", System.currentTimeMillis() - globalStartTime);
        ResourceFarm.LOGGER.info("=== Resource Farm 数据加载结束 ===");
    }
}
