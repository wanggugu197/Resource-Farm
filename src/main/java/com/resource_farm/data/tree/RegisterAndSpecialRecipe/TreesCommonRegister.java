package com.resource_farm.data.tree.RegisterAndSpecialRecipe;

import com.resource_farm.api.IntObjectHolder;
import com.resource_farm.data.lang.LangHandler;
import com.resource_farm.data.tree.builder.TreeRegister;
import com.resource_farm.utils.RLUtils;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import static com.resource_farm.api.ResourceOre.ResourceOreTypes.*;
import static com.resource_farm.api.ResourceTree.ResourceTreeTypes.*;
import static com.resource_farm.data.tree.builder.TreeRegister.*;

public class TreesCommonRegister {

    public static void base() {
        TreeRegister.createSimpleResourceTree("minecraft:dirt", null, 16, OAK, IRON, 0X9E7255);
        TreeRegister.createSimpleResourceTree("minecraft:clay", null, 16, BIRCH, REDSTONE, 0XB1B7C5);
        TreeRegister.createSimpleResourceTree("minecraft:gravel", null, 16, CHERRY, COPPER, 0X918C8C);
        TreeRegister.createSimpleResourceTree("minecraft:sand", null, 16, ACACIA, IRON, 0XF1E4B3);

        LangHandler.addLang("resource_farm.resource_tree.wood", "木", "Wood");
        createMissingItemResourceTree("resource_farm.resource_tree.wood", DARK_OAK, NETHER_QUARTZ, 0XB08F55);
        LangHandler.addLang("resource_farm.resource_tree.dyes", "染料", "Dyes");
        createMissingItemResourceTree("resource_farm.resource_tree.dyes", SPRUCE, NETHER_QUARTZ, 0Xaae23a);

        TreeRegister.createSimpleResourceTree("minecraft:stone", null, 16, BIRCH, REDSTONE, 0X8B8B8B);
        TreeRegister.createSimpleResourceTree("minecraft:deepslate", null, 16, OAK, COPPER, 0X58585B);
        TreeRegister.createSimpleResourceTree("minecraft:sculk", null, 16, ACACIA, LAPIS, 0X0E2128);
        TreeRegister.createSimpleResourceTree("minecraft:obsidian", null, 16, BIRCH, COPPER, 0X100C1C);

        TreeRegister.createSimpleResourceTree("minecraft:netherrack", null, 16, OAK, IRON, 0X6C2A2A);
        TreeRegister.createSimpleResourceTree("minecraft:glowstone_dust", null, 32, CHERRY, COPPER, 14, 0XFFE17A);
        TreeRegister.createSimpleResourceTree("minecraft:soul_sand", null, 32, BIRCH, REDSTONE, 0X594438);
        TreeRegister.createSimpleResourceTree("minecraft:end_stone", null, 16, CHERRY, IRON, 0XF2F5AE);

        TreeRegister.createSimpleResourceTree("minecraft:snowball", null, 16, BIRCH, LAPIS, 0XFFFFFF);
        TreeRegister.createSimpleResourceTree("minecraft:ice", null, 16, ACACIA, LAPIS, 0XA1CAFF);
    }

    public static void baseSpecialRecipe(RecipeOutput consumer) {
        TreeItemRecipeBuild(consumer, "wood", false,
                item(Items.OAK_LOG, 16), item(Items.DARK_OAK_LOG, 16), item(Items.BIRCH_LOG, 16), item(Items.SPRUCE_LOG, 16),
                item(Items.JUNGLE_LOG, 16), item(Items.ACACIA_LOG, 16), item(Items.CHERRY_LOG, 16), item(Items.MANGROVE_LOG, 16),
                item(Items.APPLE, 32), item(Items.BAMBOO, 64), item(Items.MOSS_BLOCK, 16));
        TreeSaplingRecipeBuild(consumer, "wood", Items.OAK_LOG, Items.BIRCH_LOG, Items.APPLE, Items.MOSS_BLOCK);

        TreeItemRecipeBuild(consumer, "dyes", false,
                item(Items.WHITE_DYE, 32), item(Items.ORANGE_DYE, 32), item(Items.MAGENTA_DYE, 32), item(Items.LIGHT_BLUE_DYE, 32),
                item(Items.YELLOW_DYE, 32), item(Items.LIME_DYE, 32), item(Items.PINK_DYE, 32), item(Items.GRAY_DYE, 32),
                item(Items.LIGHT_GRAY_DYE, 32), item(Items.CYAN_DYE, 32), item(Items.PURPLE_DYE, 32), item(Items.BLUE_DYE, 32),
                item(Items.BROWN_DYE, 32), item(Items.GREEN_DYE, 32), item(Items.RED_DYE, 32), item(Items.BLACK_DYE, 32));
        TreeSaplingRecipeBuild(consumer, "dyes", ItemTags.create(RLUtils.parse("c:dyes")));
    }

    public static void mineral() {
        TreeRegister.createSimpleResourceTree("minecraft:flint", null, 32, ACACIA, NETHER_GOLD, 0X5C5A5A);
        TreeRegister.createSimpleResourceTree("minecraft:coal", null, 16, SPRUCE, IRON, 0X413D43);
        TreeRegister.createSimpleResourceTree("minecraft:raw_copper", null, 16, JUNGLE, COPPER, 0XF7AD7E);
        TreeRegister.createSimpleResourceTree("minecraft:raw_iron", null, 16, OAK, IRON, 0XFFEFCA);
        TreeRegister.createSimpleResourceTree("minecraft:raw_gold", null, 16, BIRCH, GOLD, 0XFFFF58);
        TreeRegister.createSimpleResourceTree("minecraft:lapis_lazuli", null, 32, SPRUCE, LAPIS, 0X4C95FF);
        TreeRegister.createSimpleResourceTree("minecraft:redstone", null, 32, JUNGLE, REDSTONE, 4, 0XC20900);
        TreeRegister.createSimpleResourceTree("minecraft:diamond", null, 8, CHERRY, DIAMOND, 0X99FFFF);
        TreeRegister.createSimpleResourceTree("minecraft:emerald", null, 8, CHERRY, EMERALD, 0X51FF8E);
        TreeRegister.createSimpleResourceTree("minecraft:amethyst_shard", null, 16, DARK_OAK, EMERALD, 0XFFCBFF);
        TreeRegister.createSimpleResourceTree("minecraft:quartz", null, 16, SPRUCE, NETHER_QUARTZ, 0XFFFFFF);
        TreeRegister.createSimpleResourceTree("minecraft:ancient_debris", null, 4, BIRCH, LAPIS, 0XAD7365);
    }

    public static void biology() {
        LangHandler.addLang("resource_farm.resource_tree.breeding", "养殖", "Breeding");
        createMissingItemResourceTree("resource_farm.resource_tree.breeding", DARK_OAK, IRON, 0XFF6C5E);
        LangHandler.addLang("resource_farm.resource_tree.aquatic_products", "水产", "Aquatic Products");
        createMissingItemResourceTree("resource_farm.resource_tree.aquatic_products", DARK_OAK, REDSTONE, 0X28B6EC);

        createSimpleResourceTree("minecraft:honeycomb", "entity.minecraft.bee", 8, CHERRY, NETHER_GOLD, 0XFFCF40);
        createMissingItemResourceTree("entity.minecraft.rabbit", BIRCH, COPPER, 0XFFC885);
        createSimpleResourceTree("minecraft:armadillo_scute", "entity.minecraft.armadillo", 8, OAK, LAPIS, 0XFFA9A6);
        createSimpleResourceTree("minecraft:turtle_scute", "entity.minecraft.turtle", 8, BIRCH, LAPIS, 0X5FFF7A);

        LangHandler.addLang("resource_farm.resource_tree.minor_creature", "小型怪物", "Minor Creature");
        createMissingItemResourceTree("resource_farm.resource_tree.minor_creature", JUNGLE, NETHER_QUARTZ, 0XDD6C34);

        createSimpleResourceTree("minecraft:ender_pearl", "entity.minecraft.enderman", 8, BIRCH, EMERALD, 0X26A08B);
        createSimpleResourceTree("minecraft:slime_ball", "entity.minecraft.slime", 8, ACACIA, COPPER, 0XB4FFA6);
        createSimpleResourceTree("minecraft:phantom_membrane", "entity.minecraft.phantom", 8, MANGROVE, NETHER_QUARTZ, 0XFFFFFC);
        createSimpleResourceTree("minecraft:breeze_rod", "entity.minecraft.breeze", 8, CHERRY, DIAMOND, 0XC4DAFF);
        createSimpleResourceTree("minecraft:ghast_tear", "entity.minecraft.ghast", 8, OAK, LAPIS, 0XE5FFFF);
        createSimpleResourceTree("minecraft:blaze_rod", "entity.minecraft.blaze", 8, SPRUCE, DIAMOND, 0XFFF32F);
        createSimpleResourceTree("minecraft:wither_skeleton_skull", "entity.minecraft.wither_skeleton", 1, ACACIA, GOLD, 0X3F3F3F);
        createSimpleResourceTree("minecraft:shulker_shell", "entity.minecraft.shulker", 4, CHERRY, REDSTONE, 0XCA87D4);
    }

    public static void biologySpecialRecipe(RecipeOutput consumer) {
        TreeRegister.TreeItemRecipeBuild(consumer, "breeding", true,
                item(Items.EGG, 16), item(Items.FEATHER, 16), item(Items.CHICKEN, 16), item(Items.PORKCHOP, 16),
                item(Items.LEATHER, 16), item(Items.BEEF, 16), item(Items.WHITE_WOOL, 16), item(Items.MUTTON, 16));
        QuickTreeItemRecipeBuildWithContainer(consumer, "breeding", Items.BUCKET, Items.MILK_BUCKET, 1);

        TreeRegister.TreeItemRecipeBuild(consumer, "aquatic_products", true,
                item(Items.TROPICAL_FISH, 16), item(Items.PUFFERFISH, 16), item(Items.SALMON, 16), item(Items.COD, 16),
                item(Items.GLOW_INK_SAC, 16), item(Items.INK_SAC, 16));
        QuickTreeItemRecipeBuildWithContainer(consumer, "honeycomb", Items.GLASS_BOTTLE, Items.HONEY_BOTTLE, 4);
        TreeRegister.TreeItemRecipeBuild(consumer, "rabbit", true,
                item(Items.RABBIT, 16), item(Items.RABBIT_HIDE, 16), item(Items.RABBIT_FOOT, 1));

        TreeItemRecipeBuild(consumer, "minor_creature", false,
                item(Items.ROTTEN_FLESH, 16), item(Items.GUNPOWDER, 16), item(Items.BONE, 16), item(Items.SPIDER_EYE, 16),
                item(Items.STRING, 16));
        TreeSaplingRecipeBuild(consumer, "minor_creature", Items.ROTTEN_FLESH, Items.GUNPOWDER, Items.BONE, Items.SPIDER_EYE);
    }

    public static void agriculture() {
        LangHandler.addLang("resource_farm.resource_tree.cultivation", "耕作", "Cultivation");
        createMissingItemResourceTree("resource_farm.resource_tree.cultivation", SPRUCE, LAPIS, 0X9BFF3F);
        LangHandler.addLang("resource_farm.resource_tree.berries", "浆果", "Berries");
        createMissingItemResourceTree("resource_farm.resource_tree.berries", CHERRY, COPPER, 0Xdc457d);

        createSimpleResourceTree("minecraft:kelp", null, 16, SPRUCE, DIAMOND, 0X9DE648);
        createSimpleResourceTree("minecraft:nether_wart", null, 8, ACACIA, LAPIS, 0XD83441);

        LangHandler.addLang("resource_farm.resource_tree.prismarine", "海晶", "Prismarine");
        createMissingItemResourceTree("resource_farm.resource_tree.prismarine", DARK_OAK, NETHER_GOLD, 0XBDDCD1);
        LangHandler.addLang("resource_farm.resource_tree.chorus", "紫颂", "Chorus");
        createSimpleResourceTree("minecraft:chorus_fruit", "resource_farm.resource_tree.chorus", 16, CHERRY, COPPER, 0XE3A7E1);
        LangHandler.addLang("resource_farm.resource_tree.echo", "回响", "Echo");
        createSimpleResourceTree("minecraft:echo_shard", "resource_farm.resource_tree.echo", 1, OAK, NETHER_QUARTZ, 0X125E6E);

        createSimpleResourceTree("minecraft:dragon_egg", null, 1, BIRCH, REDSTONE, 0X0E0A12);
        createSimpleResourceTree("minecraft:nether_star", null, 1, BIRCH, EMERALD, 0XFFFFFF);
    }

    public static void agricultureSpecialRecipe(RecipeOutput consumer) {
        TreeRegister.TreeItemRecipeBuild(consumer, "cultivation", true,
                item(Items.WHEAT, 16), item(Items.SUGAR_CANE, 16), item(Items.POTATO, 16), item(Items.CARROT, 16),
                item(Items.BEETROOT, 16), item(Items.COCOA_BEANS, 6), item(Items.MELON, 2), item(Items.PUMPKIN, 2));
        TreeRegister.TreeItemRecipeBuild(consumer, "berries", true, item(Items.SWEET_BERRIES, 16), item(Items.GLOW_BERRIES, 16));
        TreeRegister.TreeItemRecipeBuild(consumer, "prismarine", true, item(Items.PRISMARINE_CRYSTALS, 16), item(Items.PRISMARINE_SHARD, 16));
        TreeRegister.TreeItemRecipeBuild(consumer, "chorus_fruit", false, item(Items.CHORUS_FLOWER, 1));
    }

    private static IntObjectHolder<Item> item(Item item, int number) {
        return new IntObjectHolder<>(number, item);
    }
}
