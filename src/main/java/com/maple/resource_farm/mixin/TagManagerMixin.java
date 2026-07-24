package com.maple.resource_farm.mixin;

import com.maple.resource_farm.api.ITagLoader;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 26.1 移除了 TagManager；标签加载改由 TagLoader 静态方法完成。
 * 通过 ThreadLocal + NEW 注入，把对应 Registry 写入 TagLoader。
 */
@Mixin(TagLoader.class)
public class TagManagerMixin {

    private static final ThreadLocal<Registry<?>> CURRENT_REGISTRY = new ThreadLocal<>();

    @Inject(
            method = "loadPendingTags",
            at = @At("HEAD"))
    private static <T> void resource_farm$captureFrozenRegistry(
                                                                ResourceManager manager, Registry<T> registry, CallbackInfoReturnable<?> cir) {
        CURRENT_REGISTRY.set(registry);
    }

    @Inject(
            method = "loadPendingTags",
            at = @At("RETURN"))
    private static <T> void resource_farm$clearFrozenRegistry(
                                                              ResourceManager manager, Registry<T> registry, CallbackInfoReturnable<?> cir) {
        CURRENT_REGISTRY.remove();
    }

    @Inject(
            method = "loadTagsForRegistry(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/WritableRegistry;)V",
            at = @At("HEAD"))
    private static <T> void resource_farm$captureWritableRegistry(
                                                                  ResourceManager manager, WritableRegistry<T> registry, CallbackInfo ci) {
        CURRENT_REGISTRY.set(registry);
    }

    @Inject(
            method = "loadTagsForRegistry(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/WritableRegistry;)V",
            at = @At("RETURN"))
    private static <T> void resource_farm$clearWritableRegistry(
                                                                ResourceManager manager, WritableRegistry<T> registry, CallbackInfo ci) {
        CURRENT_REGISTRY.remove();
    }

    @ModifyExpressionValue(
                           method = {
                                   "loadPendingTags",
                                   "loadTagsForRegistry(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/tags/TagLoader$ElementLookup;)Ljava/util/Map;"
                           },
                           at = @At(value = "NEW", target = "net/minecraft/tags/TagLoader"))
    private static <T> TagLoader<Holder<T>> resource_farm$attachRegistry(TagLoader<Holder<T>> loader) {
        Registry<?> registry = CURRENT_REGISTRY.get();
        if (registry != null) {
            ((ITagLoader) loader).resource_farm$setRegistry(registry);
        }
        return loader;
    }
}
