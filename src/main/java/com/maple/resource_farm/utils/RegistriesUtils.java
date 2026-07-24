package com.maple.resource_farm.utils;

import com.maple.resource_farm.ResourceFarm;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public final class RegistriesUtils {

    public static final Identifier AIR = RLUtils.get("minecraft", "air");

    private RegistriesUtils() {}

    public static Item getItem(String s) {
        return getItem(RLUtils.parse(s));
    }

    public static Item getItem(String mod, String name) {
        return getItem(RLUtils.get(mod, name));
    }

    public static Item getItem(Identifier id) {
        Item i = BuiltInRegistries.ITEM.getValue(id);
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
        Block block = BuiltInRegistries.BLOCK.getValue(RLUtils.parse(s));
        return block != null ? block : Blocks.AIR;
    }

    public static Fluid getFluid(String s) {
        return getFluid(RLUtils.parse(s));
    }

    public static Fluid getFluid(Identifier id) {
        Fluid fluid = BuiltInRegistries.FLUID.getValue(id);
        return fluid != null ? fluid : Fluids.EMPTY;
    }
}
