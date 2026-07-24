package com.maple.resource_farm.mixin;

import com.maple.resource_farm.api.ITagLoader;
import com.maple.resource_farm.common.Manager.ResourceFarmTagManager;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagLoader;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

/**
 * Tag 内存注入入口。
 * <p>
 * 在 {@link TagLoader#build} 完成 id→Holder 解析之后，将 Resource Farm 动态成员以
 * {@link net.minecraft.core.Holder} 直接合并进结果 Map，再交给
 * {@code prepareTagReload → PendingTags.apply → MappedRegistry.bindTags}。
 * <p>
 * 不经过 JSON 序列化/反序列化。
 *
 * @see ResourceFarmTagManager 链路说明
 * @see TagManagerMixin 将当前 Registry 挂到 TagLoader 实例
 */
@Mixin(value = TagLoader.class, priority = 600)
public class TagLoaderMixin implements ITagLoader {

    @Nullable
    @Unique
    private Registry<?> resource_farm$storedRegistry;

    /**
     * build 返回 {@code Map<Identifier, List<T>>}，对注册表标签加载路径 T={@code Holder<E>}。
     */
    @Inject(method = "build", at = @At("RETURN"), cancellable = true)
    private void resource_farm$injectHoldersAfterBuild(
                                                       Map<Identifier, List<TagLoader.EntryWithSource>> builders,
                                                       CallbackInfoReturnable<Map<Identifier, List<?>>> cir) {
        if (resource_farm$storedRegistry == null) {
            return;
        }
        Map<?, ?> original = cir.getReturnValue();
        Map<?, ?> merged = ResourceFarmTagManager.injectBuiltHolders(original, resource_farm$storedRegistry);
        if (merged != null && merged != original) {
            // noinspection unchecked,rawtypes
            cir.setReturnValue((Map) merged);
        }
    }

    @Override
    public void resource_farm$setRegistry(Registry<?> registry) {
        this.resource_farm$storedRegistry = registry;
    }
}
