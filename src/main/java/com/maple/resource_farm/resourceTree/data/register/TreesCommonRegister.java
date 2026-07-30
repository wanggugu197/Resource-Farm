package com.maple.resource_farm.resourceTree.data.register;

import com.gto.registrylib.datagen.provider.RegistryLibRecipeProvider;
import com.maple.resource_farm.api.IntObjectHolder;
import com.maple.resource_farm.data.lang.LangHandler;
import com.maple.resource_farm.resourceTree.builder.TreeRecipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import com.mapleutillib.utils.RLUtils;

/**
 * 预设树木的<strong>配方</strong>与<strong>翻译键</strong>。
 */
public class TreesCommonRegister {

    /** 数据包树木使用的自定义翻译键（无原版 item 名时）。 */
    public static void registerLang() {
        LangHandler.addLang("resource_farm.resource_tree.wood", "木", "Wood");
        LangHandler.addLang("resource_farm.resource_tree.dyes", "染料", "Dyes");
        LangHandler.addLang("resource_farm.resource_tree.sculk", "幽匿", "Sculk");
        LangHandler.addLang("resource_farm.resource_tree.breeding", "养殖", "Breeding");
        LangHandler.addLang("resource_farm.resource_tree.aquatic_products", "水产", "Aquatic Products");
        LangHandler.addLang("resource_farm.resource_tree.minor_creature", "小型怪物", "Minor Creature");
        LangHandler.addLang("resource_farm.resource_tree.cultivation", "耕作", "Cultivation");
        LangHandler.addLang("resource_farm.resource_tree.berries", "浆果", "Berries");
        LangHandler.addLang("resource_farm.resource_tree.prismarine", "海晶", "Prismarine");
        LangHandler.addLang("resource_farm.resource_tree.chorus", "紫颂", "Chorus");
        LangHandler.addLang("resource_farm.resource_tree.echo", "回响", "Echo");
    }

    public static void baseSpecialRecipe(RegistryLibRecipeProvider consumer) {
        TreeRecipe.TreeItemRecipeBuild(consumer, "wood",
                item(Items.OAK_LOG, 16), item(Items.DARK_OAK_LOG, 16), item(Items.BIRCH_LOG, 16), item(Items.SPRUCE_LOG, 16),
                item(Items.JUNGLE_LOG, 16), item(Items.ACACIA_LOG, 16), item(Items.CHERRY_LOG, 16), item(Items.MANGROVE_LOG, 16),
                item(Items.APPLE, 32), item(Items.BAMBOO, 64), item(Items.MOSS_BLOCK, 16));
        TreeRecipe.TreeSaplingRecipeBuild(consumer, "wood", Items.OAK_LOG, Items.BIRCH_LOG, Items.APPLE, Items.MOSS_BLOCK);

        TreeRecipe.TreeItemRecipeBuild(consumer, "dyes",
                item(Items.WHITE_DYE, 32), item(Items.ORANGE_DYE, 32), item(Items.MAGENTA_DYE, 32), item(Items.LIGHT_BLUE_DYE, 32),
                item(Items.YELLOW_DYE, 32), item(Items.LIME_DYE, 32), item(Items.PINK_DYE, 32), item(Items.GRAY_DYE, 32),
                item(Items.LIGHT_GRAY_DYE, 32), item(Items.CYAN_DYE, 32), item(Items.PURPLE_DYE, 32), item(Items.BLUE_DYE, 32),
                item(Items.BROWN_DYE, 32), item(Items.GREEN_DYE, 32), item(Items.RED_DYE, 32), item(Items.BLACK_DYE, 32));
        TreeRecipe.TreeSaplingRecipeBuild(consumer, "dyes", ItemTags.create(RLUtils.parse("c:dyes")));

        TreeRecipe.TreeItemRecipeBuildWithExtra(consumer, "lava", Items.BUCKET, 1, item(Items.LAVA_BUCKET, 1));
        TreeRecipe.TreeSaplingRecipeBuild(consumer, "lava", Items.LAVA_BUCKET);

        TreeRecipe.TreeItemRecipeBuildWithExtra(consumer, "water", Items.BUCKET, 1, item(Items.WATER_BUCKET, 1));
        TreeRecipe.TreeSaplingRecipeBuild(consumer, "water", Items.WATER_BUCKET);

        TreeRecipe.TreeItemRecipeBuild(consumer, "sculk", item(Items.SCULK_VEIN, 8), item(Items.SCULK_CATALYST, 1),
                item(Items.SCULK_SHRIEKER, 1), item(Items.SCULK_SENSOR, 1));
        TreeRecipe.TreeItemRecipeBuild(consumer, "soul_sand", item(Items.SOUL_SOIL, 32));
    }

    public static void mineralSpecialRecipe(RegistryLibRecipeProvider consumer) {
        TreeRecipe.TreeItemRecipeBuild(consumer, "coal", item(Items.CHARCOAL, 1));
        TreeRecipe.TreeItemRecipeBuild(consumer, "raw_copper", item(Items.COPPER_BLOCK, 1));
        TreeRecipe.TreeItemRecipeBuild(consumer, "raw_iron", item(Items.IRON_BLOCK, 1));
        TreeRecipe.TreeItemRecipeBuild(consumer, "raw_gold", item(Items.GOLD_BLOCK, 1));
        TreeRecipe.TreeItemRecipeBuild(consumer, "amethyst_shard", item(Items.BUDDING_AMETHYST, 1), item(Items.AMETHYST_CLUSTER, 1));
    }

    public static void biologySpecialRecipe(RegistryLibRecipeProvider consumer) {
        TreeRecipe.TreeItemAndSaplingRecipeBuild(consumer, "breeding",
                item(Items.EGG, 16), item(Items.FEATHER, 16), item(Items.CHICKEN, 16), item(Items.PORKCHOP, 16),
                item(Items.LEATHER, 16), item(Items.BEEF, 16), item(Items.WHITE_WOOL, 16), item(Items.MUTTON, 16));
        TreeRecipe.TreeItemRecipeBuildWithExtra(consumer, "breeding", Items.BUCKET, 1, item(Items.MILK_BUCKET, 1));

        TreeRecipe.TreeItemAndSaplingRecipeBuild(consumer, "aquatic_products",
                item(Items.TROPICAL_FISH, 16), item(Items.PUFFERFISH, 16), item(Items.SALMON, 16), item(Items.COD, 16),
                item(Items.GLOW_INK_SAC, 16), item(Items.INK_SAC, 16));
        TreeRecipe.TreeItemRecipeBuildWithExtra(consumer, "honeycomb", Items.GLASS_BOTTLE, 4, item(Items.HONEY_BOTTLE, 4));
        TreeRecipe.TreeItemAndSaplingRecipeBuild(consumer, "rabbit",
                item(Items.RABBIT, 16), item(Items.RABBIT_HIDE, 16), item(Items.RABBIT_FOOT, 1));

        TreeRecipe.TreeItemRecipeBuild(consumer, "minor_creature",
                item(Items.ROTTEN_FLESH, 16), item(Items.GUNPOWDER, 16), item(Items.BONE, 16), item(Items.SPIDER_EYE, 16),
                item(Items.STRING, 16));
        TreeRecipe.TreeSaplingRecipeBuild(consumer, "minor_creature", Items.ROTTEN_FLESH, Items.GUNPOWDER, Items.BONE, Items.SPIDER_EYE);
    }

    public static void agricultureSpecialRecipe(RegistryLibRecipeProvider consumer) {
        TreeRecipe.TreeItemAndSaplingRecipeBuild(consumer, "cultivation",
                item(Items.WHEAT, 16), item(Items.SUGAR_CANE, 16), item(Items.POTATO, 16), item(Items.CARROT, 16),
                item(Items.BEETROOT, 16), item(Items.COCOA_BEANS, 6), item(Items.MELON, 2), item(Items.PUMPKIN, 2));
        TreeRecipe.TreeItemAndSaplingRecipeBuild(consumer, "berries", item(Items.SWEET_BERRIES, 16), item(Items.GLOW_BERRIES, 16));
        TreeRecipe.TreeItemAndSaplingRecipeBuild(consumer, "prismarine", item(Items.PRISMARINE_CRYSTALS, 16), item(Items.PRISMARINE_SHARD, 16));
        TreeRecipe.TreeItemRecipeBuild(consumer, "chorus_fruit", item(Items.CHORUS_FLOWER, 1));
    }

    public static IntObjectHolder<Item> item(Item item, int number) {
        return new IntObjectHolder<>(number, item);
    }
}
