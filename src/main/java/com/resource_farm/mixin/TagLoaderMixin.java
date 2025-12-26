package com.resource_farm.mixin;

import com.resource_farm.api.ITagLoader;
import com.resource_farm.common.Manager.ResourceFarmTagManager;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(value = TagLoader.class, priority = 600)
public class TagLoaderMixin implements ITagLoader {

    @Nullable
    @Unique
    private Registry<?> resource_farm$storedRegistry;

    @Inject(method = "load", at = @At(value = "RETURN"))
    public void load(ResourceManager resourceManager,
                     CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir) {
        if (resource_farm$storedRegistry == null) return;
        ResourceFarmTagManager.generateResourceFarmDynamicTags(cir.getReturnValue(), resource_farm$storedRegistry);
    }

    @Override
    public void resource_farm$setRegistry(Registry<?> registry) {
        this.resource_farm$storedRegistry = registry;
    }
}
