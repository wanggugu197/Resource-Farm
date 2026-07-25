package com.maple.resource_farm.common;

import com.maple.resource_farm.ResourceTree.builder.TreeModelRenderer;
import com.maple.resource_farm.common.Manager.ResourceFarmDynamicDataEvents;
import com.maple.resource_farm.data.ResourceFarmRegister;
import com.maple.resource_farm.data.lang.LangHandler;
import com.maple.resource_farm.data.misc.ResourceFarmCreativeModeTabs;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

import com.mapleutillib.api.pack.addon.AddonFinder;

import static com.maple.resource_farm.ResourceFarm.REGISTRY;

/**
 * 通用初始化。
 * <p>
 * 动态<strong>资源包</strong>（客户端模型）走 MapleUtilLib：
 * {@code REGISTRY.packs().register → packIcon → whenClient}。
 * 包发现 / ModelManager 重建由 lib 负责，本模组不再写 AddPackFinders / ModelManager mixin。
 * <p>
 * 动态<strong>服务端数据</strong>（配方 / 战利品 / 堆肥）仍用
 * {@link com.maple.resource_farm.common.inject.ResourceFarmDynamicInjections}
 * 内存直注（Tag bind 时序），不经 {@code whenServer} 数据包 JSON。
 */
public class CommonInit {

    public static void init(final IEventBus modBus) {
        NeoForge.EVENT_BUS.register(ResourceFarmDynamicDataEvents.class);
        ResourceFarmCreativeModeTabs.init();
        ResourceFarmRegister.init(modBus);
        LangHandler.init();

        initDynamicPacks(modBus);
    }

    /**
     * MapleUtilLib 动态包：与最新 API 对齐。
     *
     * <pre>{@code
     * REGISTRY.packs()
     *     .register(modBus)
     *     .packIcon("icon.png")
     *     .addNamespace(...)   // addon
     *     .whenClient(...)
     *     // .whenServer((data, regs) -> ...)  // RF 暂不用
     * }</pre>
     */
    private static void initDynamicPacks(IEventBus modBus) {
        var packs = REGISTRY.packs()
                .register(modBus)
                .packIcon("icon.png") // 与 neoforge.mods.toml logoFile 一致；默认即此值
                .whenClient(TreeModelRenderer::reinitModels);

        try {
            AddonFinder.getAddons().keySet().forEach(packs::addNamespace);
        } catch (Throwable ignored) {
            // addon 扫描失败不影响主流程
        }
    }
}
