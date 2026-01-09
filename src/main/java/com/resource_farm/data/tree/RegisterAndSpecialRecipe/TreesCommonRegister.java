package com.resource_farm.data.tree.RegisterAndSpecialRecipe;

import static com.resource_farm.api.ResourceOre.ResourceOreTypes.*;
import static com.resource_farm.api.ResourceTree.ResourceTreeTypes.*;
import static com.resource_farm.data.tree.builder.TreeRegister.createSimpleResourceTree;

public class TreesCommonRegister {

    public static void base() {
        createSimpleResourceTree("minecraft:dirt", null, OAK, IRON, 0X9E7255);
        createSimpleResourceTree("minecraft:clay", null, BIRCH, REDSTONE, 0XB1B7C5);
        createSimpleResourceTree("minecraft:gravel", null, CHERRY, COPPER, 0X918C8C);
        createSimpleResourceTree("minecraft:sand", null, ACACIA, IRON, 0XF1E4B3);

        createSimpleResourceTree("minecraft:stone", null, BIRCH, REDSTONE, 0X8B8B8B);
        createSimpleResourceTree("minecraft:deepslate", null, OAK, COPPER, 0X58585B);
        createSimpleResourceTree("minecraft:sculk", null, ACACIA, LAPIS, 0X0E2128);
        createSimpleResourceTree("minecraft:obsidian", null, BIRCH, COPPER, 0X100C1C);

        createSimpleResourceTree("minecraft:netherrack", null, OAK, IRON, 0X6C2A2A);
        createSimpleResourceTree("minecraft:glowstone_dust", null, CHERRY, COPPER, 14, 0XFFE17A);
        createSimpleResourceTree("minecraft:soul_sand", null, BIRCH, REDSTONE, 0X594438);
        createSimpleResourceTree("minecraft:end_stone", null, CHERRY, IRON, 0XF2F5AE);

        createSimpleResourceTree("minecraft:snowball", null, BIRCH, LAPIS, 0XFFFFFF);
        createSimpleResourceTree("minecraft:ice", null, ACACIA, LAPIS, 0XA1CAFF);
    }

    public static void mineral() {
        createSimpleResourceTree("minecraft:flint", null, ACACIA, NETHER_GOLD, 0X5C5A5A);
        createSimpleResourceTree("minecraft:coal", null, SPRUCE, IRON, 0X413D43);
        createSimpleResourceTree("minecraft:raw_copper", null, JUNGLE, COPPER, 0XF7AD7E);
        createSimpleResourceTree("minecraft:raw_iron", null, OAK, IRON, 0XFFEFCA);
        createSimpleResourceTree("minecraft:raw_gold", null, BIRCH, GOLD, 0XFFFF58);
        createSimpleResourceTree("minecraft:lapis_lazuli", null, SPRUCE, LAPIS, 0X4C95FF);
        createSimpleResourceTree("minecraft:redstone", null, JUNGLE, REDSTONE, 4, 0XC20900);
        createSimpleResourceTree("minecraft:diamond", null, CHERRY, DIAMOND, 0X99FFFF);
        createSimpleResourceTree("minecraft:emerald", null, CHERRY, EMERALD, 0X51FF8E);
        createSimpleResourceTree("minecraft:amethyst_shard", null, DARK_OAK, EMERALD, 0XFFCBFF);
        createSimpleResourceTree("minecraft:quartz", null, SPRUCE, NETHER_QUARTZ, 0XFFFFFF);
        createSimpleResourceTree("minecraft:ancient_debris", null, BIRCH, LAPIS, 0XAD7365);
    }
}
