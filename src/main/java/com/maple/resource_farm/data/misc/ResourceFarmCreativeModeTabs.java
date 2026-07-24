package com.maple.resource_farm.data.misc;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;

import com.gto.registrylib.util.entry.RegistryEntry;

import java.util.Map;

import static com.maple.resource_farm.common.registry.ResourceFarmRegistration.REGISTRY;

public class ResourceFarmCreativeModeTabs {

    public static void init() {}

    // 创造模式标签注册
    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> FARM_TAB = REGISTRY
            .creativeTab("resource_farm", "Resource Farm", Map.of("zh_cn", "资源农场"),
                    builder -> builder.icon(() -> Items.OAK_LOG.asItem().getDefaultInstance()));

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> TREE_TAB = REGISTRY
            .creativeTab("resource_tree", "Resource Farm | Resource Tree", Map.of("zh_cn", "资源农场 | 资源树"),
                    builder -> builder.icon(() -> Items.OAK_LOG.asItem().getDefaultInstance()));
}
