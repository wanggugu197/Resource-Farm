package com.maple.resource_farm.common.registry;

import com.maple.resource_farm.ResourceFarm;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import com.mapleutillib.api.registry.ModLangProvider;
import com.mapleutillib.api.registry.ModRegistryCore;

/**
 * Resource Farm 注册入口：基于 MapleUtilLib {@link ModRegistryCore}（继承 RegistryLib）。
 */
public class ResourceFarmRegistration {

    public static final ModRegistryCore REGISTRY = ModRegistryCore.create(ResourceFarm.MOD_ID);

    static {
        // 与 MapleUtilLib 一致：zh_cn 走已注册的 Provider；再补 zh_tw 别名
        REGISTRY.withLangAlias("zh_tw", ModLangProvider.LANG_ZH_TW);
        // 不使用默认创造标签页，由 ResourceFarmCreativeModeTabs 显式注册
        REGISTRY.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    public static void init() {}
}
