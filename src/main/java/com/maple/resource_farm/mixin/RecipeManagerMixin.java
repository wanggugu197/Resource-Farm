package com.maple.resource_farm.mixin;

import com.maple.resource_farm.common.inject.ResourceFarmDynamicInjections;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 配方直注必须在标签绑定之后。
 * <p>
 * 主路径：{@link net.minecraft.server.ReloadableServerResources#updateComponentsAndStaticRegistryTags}
 * 之后写入 {@link RecipeMap}；此处在 {@link RecipeManager#finalizeRecipeLoading} HEAD
 * 再兜底合并一次（覆盖 /reload 与首次进档顺序差异）。
 */
@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Inject(method = "finalizeRecipeLoading", at = @At("HEAD"))
    private void resource_farm$ensureDynamicRecipes(FeatureFlagSet enabledFlags, CallbackInfo ci) {
        RecipeManager self = (RecipeManager) (Object) this;
        RecipeManagerAccessor acc = (RecipeManagerAccessor) self;
        RecipeMap current = acc.resource_farm$getRecipes();
        RecipeMap merged = ResourceFarmDynamicInjections.mergeRecipes(current);
        if (merged != current) {
            acc.resource_farm$setRecipes(merged);
        }
    }
}
