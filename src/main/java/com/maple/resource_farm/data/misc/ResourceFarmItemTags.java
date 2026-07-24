package com.maple.resource_farm.data.misc;

import com.maple.resource_farm.ResourceFarm;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * 资源农场物品 Tag 定义。
 * <p>
 * 动态内容的标签成员由 {@link com.maple.resource_farm.data.tree.builder.TreeTagInsert} 注入。
 */
public class ResourceFarmItemTags {

    public static final TagKey<Item> RESOURCE_SAPLING = createItemTag("resource_sapling");
    public static final TagKey<Item> RESOURCE_LEAVES = createItemTag("resource_leaves");
    public static final TagKey<Item> RESOURCE_LOG = createItemTag("resource_log");
    public static final TagKey<Item> RESOURCE_PLANKS = createItemTag("resource_planks");
    public static final TagKey<Item> RESOURCE_RESIN = createItemTag("resource_resin");
    public static final TagKey<Item> RESOURCE_FRUIT = createItemTag("resource_fruit");
    public static final TagKey<Item> RESOURCE_CLUMP = createItemTag("resource_clump");

    private static TagKey<Item> createItemTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceFarm.id(path));
    }

    public static TagKey<Item> createItemTag(Identifier location) {
        return TagKey.create(Registries.ITEM, location);
    }
}
