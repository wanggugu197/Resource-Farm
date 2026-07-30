package com.maple.resource_farm.mixin;

import com.gto.registrylib.datagen.provider.RegistryLibRecipeProvider;
import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.common.inject.ResourceFarmDynamicInjections;
import com.maple.resource_farm.common.manager.ResourceFarmComposTablesManager;
import com.maple.resource_farm.common.manager.ResourceFarmLootTablesManager;
import com.maple.resource_farm.common.manager.ResourceFarmRecipesManager;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.common.conditions.ICondition;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 分两阶段准备动态数据：
 * <ol>
 * <li>{@code loadResources} HEAD：仅战利品/堆肥（不依赖物品 Tag bind）</li>
 * <li>{@code updateComponentsAndStaticRegistryTags} RETURN：标签已 bind，再生成并注入配方/进度</li>
 * </ol>
 */
@Mixin(value = ReloadableServerResources.class, priority = 100)
public abstract class ReloadableServerResourcesMixin {

    @Shadow
    public abstract RecipeManager getRecipeManager();

    @Shadow
    public abstract ServerAdvancementManager getAdvancements();

    /**
     * 阶段 1：loot / compost 可在标签 bind 前生成。
     */
    @Inject(method = "loadResources", at = @At("HEAD"))
    private static void resource_farm$prepareEarlyDynamicData(
                                                              ResourceManager resourceManager,
                                                              LayeredRegistryAccess<RegistryLayer> contextLayers,
                                                              List<Registry.PendingTags<?>> updatedContextTags,
                                                              FeatureFlagSet enabledFeatures,
                                                              Commands.CommandSelection commandSelection,
                                                              PermissionSet functionCompilationPermissions,
                                                              Executor backgroundExecutor,
                                                              Executor mainThreadExecutor,
                                                              CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir) {
        long t0 = System.currentTimeMillis();
        ResourceFarm.LOGGER.info("=== Resource Farm 动态数据（早期：loot/compost）===");

        ResourceFarmDynamicInjections.clear();
        RegistryAccess.Frozen frozen = contextLayers.compositeAccess();

        long step = System.currentTimeMillis();
        ResourceFarmComposTablesManager.buildComposTablesData();
        ResourceFarm.LOGGER.info("堆肥表对象生成完成，耗时 {}ms", System.currentTimeMillis() - step);

        step = System.currentTimeMillis();
        ResourceFarmLootTablesManager.generateLoot(frozen);
        ResourceFarm.LOGGER.info("战利品对象生成完成，耗时 {}ms", System.currentTimeMillis() - step);

        ResourceFarm.LOGGER.info("Resource Farm early prepare took {}ms", System.currentTimeMillis() - t0);
    }

    /**
     * 阶段 2：{@link Registry.PendingTags#apply} 与 {@code TagsUpdatedEvent} 之后，
     * 物品/方块 Tag（含动态注入成员）已可用，再构建引用 {@code #minecraft:saplings} 的配方。
     */
    @Inject(method = "updateComponentsAndStaticRegistryTags", at = @At("RETURN"))
    private void resource_farm$injectAfterTagsBound(CallbackInfo ci) {
        if (!ResourceFarmDynamicInjections.markRecipesBuilt()) {
            return;
        }

        long t0 = System.currentTimeMillis();
        ResourceFarm.LOGGER.info("=== Resource Farm 动态数据（晚期：recipes，标签已绑定）===");

        long step = System.currentTimeMillis();

        HolderLookup.Provider registries = ((RecipeManagerAccessor) this.getRecipeManager()).resource_farm$getRegistries();
        ResourceFarmRecipesManager.recipeAddition(new RegistryLibRecipeProvider(null, registries,creatRecipeOutput()));
        ResourceFarm.LOGGER.info("配方对象生成完成，耗时 {}ms", System.currentTimeMillis() - step);

        ResourceFarmDynamicInjections.injectRecipes(this.getRecipeManager());
        ResourceFarmDynamicInjections.injectAdvancements(this.getAdvancements());

        ResourceFarmDynamicInjections.dumpAll(registries);

        ResourceFarm.LOGGER.info("Resource Farm late recipe inject took {}ms", System.currentTimeMillis() - t0);
        ResourceFarm.LOGGER.info("=== Resource Farm 晚期注入结束 ===");
    }

    private static RecipeOutput creatRecipeOutput() {
        return new RecipeOutput() {

            @Override
            public Advancement.@NotNull Builder advancement() {
                return Advancement.Builder.recipeAdvancement();
            }

            @Override
            public void includeRootAdvancement() {}

            @Override
            public void accept(@NotNull ResourceKey<Recipe<?>> id, @NotNull Recipe<?> recipe,
                               @Nullable AdvancementHolder advancement, ICondition @NotNull... conditions) {
                ResourceFarmDynamicInjections.addRecipe(id, recipe, advancement);
            }
        };
    }
}
