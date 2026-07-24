package com.maple.resource_farm.data.misc;

import com.maple.resource_farm.ResourceFarm;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * 资源农场方块 Tag 定义。
 * <p>
 * 动态内容的标签成员由 {@link com.maple.resource_farm.data.tree.builder.TreeTagInsert} 注入。
 */
public class ResourceFarmBlockTags {

    public static final TagKey<Block> RESOURCE_SAPLING = createBlockTag("resource_sapling");
    public static final TagKey<Block> RESOURCE_LEAVES = createBlockTag("resource_leaves");
    public static final TagKey<Block> RESOURCE_LOG = createBlockTag("resource_log");
    public static final TagKey<Block> RESOURCE_PLANKS = createBlockTag("resource_planks");

    private static TagKey<Block> createBlockTag(String path) {
        return TagKey.create(Registries.BLOCK, ResourceFarm.id(path));
    }

    public static TagKey<Block> createBlockTag(Identifier location) {
        return TagKey.create(Registries.BLOCK, location);
    }
}
