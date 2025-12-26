package com.resource_farm.common.datagen;

import com.resource_farm.ResourceFarm;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

/**
 * 构建 ResourceFarm 的数据生成类
 */
@EventBusSubscriber(modid = ResourceFarm.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ResourceFarmDatagen {

    /**
     * 订阅 GatherDataEvent，触发数据生成流程
     */
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        // 1. 获取数据生成核心对象
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        var registries = event.getLookupProvider(); // 注册表查询提供者

        // 2. 客户端数据生成
        if (event.includeClient()) {
            // 示例：后续可添加方块模型、物品模型、声音等客户端资源生成器
            // generator.addProvider(true, new ResourceFarmItemModelProvider(packOutput, existingFileHelper));
            // generator.addProvider(true, new ResourceFarmBlockStateProvider(packOutput, existingFileHelper));
        }

        // 3. 服务端数据生成
        if (event.includeServer()) {
            // 定义模组的数据包集合
            Set<String> modPackSet = Set.of(ResourceFarm.MOD_ID);

            // 3.1 构建注册表集
            RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();
            // 注册：自定义树的 ConfiguredFeature
            // .add(Registries.CONFIGURED_FEATURE, TreeFeatures::bootstrapConfiguredFeatures)
            // 注册：自定义树的 PlacedFeature
            // .add(Registries.PLACED_FEATURE, TreeFeatures::bootstrapPlacedFeatures);

            // 3.2 构建内置数据包条目提供者
            DatapackBuiltinEntriesProvider datapackProvider = generator.addProvider(
                    true, // 覆盖已有数据（开发阶段建议为 true，发布阶段可改为 false）
                    new DatapackBuiltinEntriesProvider(
                            packOutput,         // 数据包输出路径
                            registries,         // 注册表查询提供者
                            registrySetBuilder, // 注册表构建器
                            modPackSet          // 模组数据包集合
                    ));

            // 3.3 可选：添加标签加载器（后续扩展：方块标签、生物群系标签等）
            // 示例：添加树苗种植土壤标签、树叶标签等
            // generator.addProvider(true, new ResourceFarmBlockTagsLoader(packOutput,
            // datapackProvider.getRegistryProvider(), existingFileHelper));
        }
    }
}
