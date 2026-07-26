package com.maple.resource_farm.data.misc;

import com.maple.resource_farm.plantPot.ResourcePlantPotRegister;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;

import com.gto.registrylib.util.entry.RegistryEntry;

import java.util.Map;

import static com.maple.resource_farm.ResourceFarm.REGISTRY;

public class ResourceFarmCreativeModeTabs {

    public static void init() {}

    // 创造模式标签注册
    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> FARM_TAB = REGISTRY
            .creativeTab("resource_farm", "Resource Farm", Map.of("zh_cn", "资源农场"),
                    builder -> builder.icon(Items.OAK_LOG::getDefaultInstance));

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> TREE_TAB = REGISTRY
            .creativeTab("resource_tree", "Resource Farm | Resource Tree", Map.of("zh_cn", "资源农场 | 资源树"),
                    builder -> builder.icon(Items.OAK_LOG::getDefaultInstance));

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> PLANT_POT_TAB = REGISTRY
            .creativeTab("plant_pot", "Resource Farm | Plant Pot", Map.of("zh_cn", "资源农场 | 盆栽盆"),
                    builder -> builder.icon(ResourcePlantPotRegister.HOPPING_BONSAI_POT::asStack));
}
