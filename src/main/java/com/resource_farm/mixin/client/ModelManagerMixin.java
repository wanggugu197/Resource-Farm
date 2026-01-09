package com.resource_farm.mixin.client;

import com.resource_farm.ResourceFarm;
import com.resource_farm.data.tree.builder.TreeModelRenderer;

import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.fml.ModLoader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = ModelManager.class)
public abstract class ModelManagerMixin {

    @Inject(method = "reload", at = @At(value = "HEAD"))
    private void loadDynamicModels(PreparableReloadListener.PreparationBarrier preparationBarrier,
                                   ResourceManager resourceManager, ProfilerFiller preparationsProfiler,
                                   ProfilerFiller reloadProfiler, Executor backgroundExecutor,
                                   Executor gameExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if (ModLoader.hasErrors()) {
            ResourceFarm.LOGGER.warn("Resource Farm Model loading CANCELLED because loading errors have been encountered");
            return;
        }

        long startTime = System.currentTimeMillis();

        TreeModelRenderer.reinitModels();

        ResourceFarm.LOGGER.info("Resource Farm Model loading took {}ms", System.currentTimeMillis() - startTime);
    }
}
