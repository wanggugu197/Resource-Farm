package com.resource_farm.data.tree.builder;

import com.resource_farm.api.block.FertilizeSettings;
import com.resource_farm.data.tree.ResourceTreeConfig;

import static com.resource_farm.api.ResourceOre.ResourceOreTypes.*;
import static com.resource_farm.api.ResourceTree.ResourceTreeTypes.*;
import static com.resource_farm.data.tree.RegisterResourceTrees.createResourceTree;

public class TreesRegister {

    public static void commonRegister() {
        createResourceTree(ResourceTreeConfig.create(
                "minecraft:iron_ingot", null,
                OAK, IRON, FertilizeSettings.DEFAULT, 10,
                null, null,
                0, 0xeeeeee));

        createResourceTree(ResourceTreeConfig.create(
                "minecraft:copper_ingot", null,
                DARK_OAK, COPPER, FertilizeSettings.DEFAULT, 15,
                null, null,
                0, 0xe77c56));

        createResourceTree(ResourceTreeConfig.create(
                "minecraft:gold_ingot", null,
                BIRCH, GOLD, FertilizeSettings.DEFAULT, 5,
                null, null,
                0, 0xfdf55f));

        createResourceTree(ResourceTreeConfig.create(
                "minecraft:emerald", null,
                SPRUCE, EMERALD, FertilizeSettings.DEFAULT, 10,
                null, null,
                0, 0x17ff6c));

        createResourceTree(ResourceTreeConfig.create(
                "minecraft:diamond", null,
                JUNGLE, DIAMOND, FertilizeSettings.DEFAULT, 10,
                null, null,
                0, 0xC8FFFF));

        createResourceTree(ResourceTreeConfig.create(
                "minecraft:quartz", null,
                ACACIA, NETHER_QUARTZ, FertilizeSettings.DEFAULT, 10,
                null, null,
                0, 0xf8efe3));

        createResourceTree(ResourceTreeConfig.create(
                "minecraft:redstone", null,
                CHERRY, REDSTONE, FertilizeSettings.DEFAULT, 10,
                null, null,
                4, 0xff0000));

        createResourceTree(ResourceTreeConfig.create(
                "minecraft:lapis_lazuli", null,
                CHERRY, LAPIS, FertilizeSettings.DEFAULT, 10,
                null, null,
                0, 0x85a9ff));

        createResourceTree(ResourceTreeConfig.create(
                "minecraft:flint", null,
                MANGROVE, NETHER_GOLD, FertilizeSettings.DEFAULT, 10,
                null, null,
                0, 0xc7c7c7));
    }
}
