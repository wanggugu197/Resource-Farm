package com.resource_farm.data.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.tterrag.registrate.util.entry.RegistryEntry;

import static com.resource_farm.api.registry.ResourceFarmRegistration.REGISTRATE;

public class ResourceFarmCreativeModeTabs {

    public static void init() {}

    public static RegistryEntry<CreativeModeTab, CreativeModeTab> FARM_TAB = REGISTRATE
            .object("farm")
            .generic(Registries.CREATIVE_MODE_TAB, () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.resource_farm.resource_farm"))
                    .icon(() -> new ItemStack(Items.OAK_LOG))
                    .displayItems((params, output) -> {})
                    .build())
            .register();

    public static RegistryEntry<CreativeModeTab, CreativeModeTab> TREE_TAB = REGISTRATE
            .object("tree")
            .generic(Registries.CREATIVE_MODE_TAB, () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.resource_farm.resource_tree"))
                    .icon(() -> new ItemStack(Items.OAK_LOG))
                    .displayItems((params, output) -> {})
                    .build())
            .register();
}
