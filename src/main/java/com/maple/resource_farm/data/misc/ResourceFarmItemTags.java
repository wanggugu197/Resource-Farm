package com.maple.resource_farm.data.misc;

import com.maple.resource_farm.ResourceFarm;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * 资源农场物品 Tag 定义。
 * <p>
 * 动态内容的标签成员由 {@link com.maple.resource_farm.resourceTree.builder.TreeTagInsert} 注入。
 */
public class ResourceFarmItemTags {

    public static void init() {}

    // ===== 原资源树标签 =====
    public static final TagKey<Item> RESOURCE_SAPLING = createItemTag("resource_sapling");
    public static final TagKey<Item> RESOURCE_LEAVES = createItemTag("resource_leaves");
    public static final TagKey<Item> RESOURCE_LOG = createItemTag("resource_log");
    public static final TagKey<Item> RESOURCE_PLANKS = createItemTag("resource_planks");
    public static final TagKey<Item> RESOURCE_RESIN = createItemTag("resource_resin");
    public static final TagKey<Item> RESOURCE_FRUIT = createItemTag("resource_fruit");
    public static final TagKey<Item> RESOURCE_CLUMP = createItemTag("resource_clump");

    // ===== 土壤标签（用于 GrowthRecipe） =====
    public static final TagKey<Item> DIRT_SOILS = createItemTag("dirt_soils");
    public static final TagKey<Item> TREE_SOILS = createItemTag("tree_soils");
    public static final TagKey<Item> FARMLAND_SOILS = createItemTag("farmland_soils");
    public static final TagKey<Item> SAND_SOILS = createItemTag("sand_soils");
    public static final TagKey<Item> MUSHROOM_SOILS = createItemTag("mushroom_soils");
    public static final TagKey<Item> END_SOILS = createItemTag("end_soils");
    public static final TagKey<Item> JUNGLE_SOILS = createItemTag("jungle_soils");
    public static final TagKey<Item> NETHER_SOILS = createItemTag("nether_soils");
    public static final TagKey<Item> MOSS_SOILS = createItemTag("moss_soils");
    public static final TagKey<Item> WATER_SOILS = createItemTag("water_soils");
    public static final TagKey<Item> STONE_SOILS = createItemTag("stone_soils");
    public static final TagKey<Item> SOUL_SAND_SOILS = createItemTag("soul_sand_soils");
    public static final TagKey<Item> SCULK_SOILS = createItemTag("sculk_soils");

    // ===== 神秘农业/JustDireThings 的分级土壤 =====
    public static final TagKey<Item> MA_TIER1_SOILS = createItemTag("tier_1_soils");
    public static final TagKey<Item> MA_TIER2_SOILS = createItemTag("tier_2_soils");
    public static final TagKey<Item> MA_TIER3_SOILS = createItemTag("tier_3_soils");
    public static final TagKey<Item> MA_TIER4_SOILS = createItemTag("tier_4_soils");
    public static final TagKey<Item> MA_TIER5_SOILS = createItemTag("tier_5_soils");

    private static TagKey<Item> createItemTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceFarm.id(path));
    }

    public static TagKey<Item> createItemTag(ResourceLocation location) {
        return TagKey.create(Registries.ITEM, location);
    }
}
