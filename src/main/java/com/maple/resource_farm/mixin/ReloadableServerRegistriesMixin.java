package com.maple.resource_farm.mixin;

import com.maple.resource_farm.common.inject.ResourceFarmDynamicInjections;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * 在 loot 等 RELOADABLE 注册表冻结前，把动态战利品表以对象直接 {@code register} 进
 * {@link WritableRegistry}（无 JSON）。
 * <p>
 * 时机：{@code scheduleRegistryLoad} 已完成 JSON 扫描与 tag 加载，
 * {@code createAndValidateFullContext} 即将 {@code freeze}。
 */
@Mixin(ReloadableServerRegistries.class)
public class ReloadableServerRegistriesMixin {

    @Inject(method = "createAndValidateFullContext", at = @At("HEAD"))
    private static void resource_farm$injectLootTables(
                                                       LayeredRegistryAccess<RegistryLayer> contextLayers,
                                                       HolderLookup.Provider contextLookupWithUpdatedTags,
                                                       List<WritableRegistry<?>> newRegistries,
                                                       CallbackInfoReturnable<?> cir) {
        ResourceFarmDynamicInjections.injectLootTables(newRegistries);
    }
}
