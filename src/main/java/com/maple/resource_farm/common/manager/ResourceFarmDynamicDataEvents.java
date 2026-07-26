package com.maple.resource_farm.common.manager;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.common.inject.ResourceFarmDynamicInjections;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.BaseMappedRegistry;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * DataMap 直注：在 NeoForge {@link DataMapsUpdatedEvent}（服务端 reload 应用阶段）
 * 将堆肥表写入 ITEM 注册表的 dataMaps，不经 data_maps JSON。
 */
public final class ResourceFarmDynamicDataEvents {

    private ResourceFarmDynamicDataEvents() {}

    @SubscribeEvent
    public static void onDataMapsUpdated(DataMapsUpdatedEvent event) {
        if (event.getCause() != DataMapsUpdatedEvent.UpdateCause.SERVER_RELOAD) {
            return;
        }
        event.ifRegistry(Registries.ITEM, ResourceFarmDynamicDataEvents::injectCompostables);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void injectCompostables(Registry<Item> registry) {
        if (!(registry instanceof BaseMappedRegistry<?> base)) {
            ResourceFarm.LOGGER.warn("[ResourceFarm] ITEM registry is not BaseMappedRegistry; compost inject skipped");
            return;
        }

        Map dataMaps = base.getDataMaps();
        Map<ResourceKey<Item>, Compostable> compostMap = (Map<ResourceKey<Item>, Compostable>) dataMaps.computeIfAbsent(
                NeoForgeDataMaps.COMPOSTABLES,
                k -> new IdentityHashMap<>());

        int n = ResourceFarmDynamicInjections.injectCompostables(compostMap);
        if (n > 0) {
            ResourceFarm.LOGGER.debug("[ResourceFarm] Compostable map size now {}", compostMap.size());
        }
    }
}
