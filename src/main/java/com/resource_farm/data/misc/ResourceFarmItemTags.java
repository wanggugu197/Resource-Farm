package com.resource_farm.data.misc;

import com.resource_farm.ResourceFarm;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import com.tterrag.registrate.providers.RegistrateItemTagsProvider;

public class ResourceFarmItemTags {

    public static void init(RegistrateItemTagsProvider provider) {
        provider.addTag(RESOURCE_SAPLING);
        provider.addTag(RESOURCE_LEAVES);
        provider.addTag(RESOURCE_LOG);
        provider.addTag(RESOURCE_PLANKS);
        provider.addTag(RESOURCE_RESIN);
        provider.addTag(RESOURCE_FRUIT);
        provider.addTag(RESOURCE_CLUMP);
    }

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

    public static TagKey<Item> createItemTag(ResourceLocation location) {
        return TagKey.create(Registries.ITEM, location);
    }
}
