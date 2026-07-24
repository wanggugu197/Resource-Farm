package com.maple.resource_farm.mixin.client;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.data.tree.builder.TreeModelRenderer;

import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.fml.ModLoader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 客户端模型重载前写入动态资源包模型。
 * 26.1.2 签名：reload(SharedState, Executor, PreparationBarrier, Executor)
 */
@Mixin(value = ModelManager.class)
public abstract class ModelManagerMixin {

    @Inject(method = "reload", at = @At(value = "HEAD"))
    private void resource_farm$loadDynamicModels(
                                                 PreparableReloadListener.SharedState currentReload,
                                                 Executor taskExecutor,
                                                 PreparableReloadListener.PreparationBarrier preparationBarrier,
                                                 Executor reloadExecutor,
                                                 CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if (ModLoader.hasErrors()) {
            ResourceFarm.LOGGER.warn("Resource Farm Model loading CANCELLED because loading errors have been encountered");
            return;
        }

        long startTime = System.currentTimeMillis();
        TreeModelRenderer.reinitModels();
        ResourceFarm.LOGGER.info("Resource Farm Model loading took {}ms", System.currentTimeMillis() - startTime);
    }
}
