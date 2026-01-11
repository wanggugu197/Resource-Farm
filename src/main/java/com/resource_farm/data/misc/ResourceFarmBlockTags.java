package com.resource_farm.data.misc;

import com.resource_farm.ResourceFarm;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import com.tterrag.registrate.providers.RegistrateTagsProvider;

public class ResourceFarmBlockTags {

    public static void init(RegistrateTagsProvider.IntrinsicImpl<Block> provider) {
        provider.addTag(RESOURCE_SAPLING);
        provider.addTag(RESOURCE_LEAVES);
        provider.addTag(RESOURCE_LOG);
        provider.addTag(RESOURCE_PLANKS);
    }

    public static final TagKey<Block> RESOURCE_SAPLING = createBlockTag("resource_sapling");
    public static final TagKey<Block> RESOURCE_LEAVES = createBlockTag("resource_leaves");
    public static final TagKey<Block> RESOURCE_LOG = createBlockTag("resource_log");
    public static final TagKey<Block> RESOURCE_PLANKS = createBlockTag("resource_planks");

    private static TagKey<Block> createBlockTag(String path) {
        return TagKey.create(Registries.BLOCK, ResourceFarm.id(path));
    }

    public static TagKey<Block> createBlockTag(ResourceLocation location) {
        return TagKey.create(Registries.BLOCK, location);
    }
}
