package com.resource_farm.utils;

import com.resource_farm.ResourceFarm;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public final class RegistriesUtils {

    public static final ResourceLocation AIR = RLUtils.get("minecraft", "air");

    private RegistriesUtils() {}

    public static Item getItem(String s) {
        return getItem(RLUtils.parse(s));
    }

    public static Item getItem(String mod, String name) {
        return getItem(RLUtils.get(mod, name));
    }

    public static Item getItem(ResourceLocation id) {
        Item i = BuiltInRegistries.ITEM.get(id);
        if (i == Items.AIR && !id.equals(AIR)) {
            ResourceFarm.LOGGER.error("Item with ID {} not found", id);
            return Items.BARRIER;
        }
        return i;
    }

    public static ItemStack getItemStack(String s) {
        return getItemStack(s, 1);
    }

    public static ItemStack getItemStack(String s, int a) {
        return new ItemStack(getItem(s), a);
    }

    public static Block getBlock(String s) {
        return BuiltInRegistries.BLOCK.get(RLUtils.parse(s));
    }

    public static Fluid getFluid(String s) {
        return getFluid(RLUtils.parse(s));
    }

    public static Fluid getFluid(ResourceLocation id) {
        return BuiltInRegistries.FLUID.get(id);
    }
}
