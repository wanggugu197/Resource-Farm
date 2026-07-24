package com.maple.resource_farm.common.datagen;

import com.maple.resource_farm.ResourceFarm;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

/**
 * 构建 ResourceFarm 的数据生成类
 */
public class ResourceFarmDatagen {

    /**
     * 订阅 GatherDataEvent，触发数据生成流程
     */
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        var registries = event.getLookupProvider();

        // 服务端数据生成（26.1 GatherDataEvent 按 Server/Client 子类分发，此处统一处理）
        Set<String> modPackSet = Set.of(ResourceFarm.MOD_ID);
        RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();

        generator.addProvider(
                true,
                new DatapackBuiltinEntriesProvider(
                        packOutput,
                        registries,
                        registrySetBuilder,
                        modPackSet));
    }
}
