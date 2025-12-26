package com.resource_farm.common.block.grower;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class ResourceTreeGrowerType {

    public static final ResourceTreeGrower.ResourceTreeGrowerBuilder OAK = new ResourceTreeGrower.ResourceTreeGrowerBuilder(
            0.1F,
            null, null,
            "oak", "fancy_oak",
            "oak_bees_005", "fancy_oak_bees_005");
    public static final ResourceTreeGrower.ResourceTreeGrowerBuilder DARK_OAK = new ResourceTreeGrower.ResourceTreeGrowerBuilder(
            0,
            "dark_oak", null,
            null, null,
            null, null);
    public static final ResourceTreeGrower.ResourceTreeGrowerBuilder BIRCH = new ResourceTreeGrower.ResourceTreeGrowerBuilder(
            0,
            null, null,
            "birch", null,
            "birch_bees_005", null);
    public static final ResourceTreeGrower.ResourceTreeGrowerBuilder SPRUCE = new ResourceTreeGrower.ResourceTreeGrowerBuilder(
            0.5F,
            "mega_spruce", "mega_pine",
            "spruce", null,
            null, null);
    public static final ResourceTreeGrower.ResourceTreeGrowerBuilder JUNGLE = new ResourceTreeGrower.ResourceTreeGrowerBuilder(
            0,
            "mega_jungle_tree", null,
            "jungle_tree_no_vine", null,
            null, null);
    public static final ResourceTreeGrower.ResourceTreeGrowerBuilder ACACIA = new ResourceTreeGrower.ResourceTreeGrowerBuilder(
            0,
            null, null,
            "acacia", null,
            null, null);
    public static final ResourceTreeGrower.ResourceTreeGrowerBuilder CHERRY = new ResourceTreeGrower.ResourceTreeGrowerBuilder(
            0,
            null, null,
            "cherry", null,
            "cherry_bees_005", null);
    public static final ResourceTreeGrower.ResourceTreeGrowerBuilder MANGROVE = new ResourceTreeGrower.ResourceTreeGrowerBuilder(
            0.85F,
            null, null,
            "mangrove", "tall_mangrove",
            null, null);

    public static final Object2ObjectOpenHashMap<String, ResourceTreeGrower.ResourceTreeGrowerBuilder> ResourceTreeGrowerBuilderMap = new Object2ObjectOpenHashMap<>();

    static {
        ResourceTreeGrowerBuilderMap.put("oak", OAK);
        ResourceTreeGrowerBuilderMap.put("dark_oak", DARK_OAK);
        ResourceTreeGrowerBuilderMap.put("birch", BIRCH);
        ResourceTreeGrowerBuilderMap.put("spruce", SPRUCE);
        ResourceTreeGrowerBuilderMap.put("jungle", JUNGLE);
        ResourceTreeGrowerBuilderMap.put("acacia", ACACIA);
        ResourceTreeGrowerBuilderMap.put("cherry", CHERRY);
        ResourceTreeGrowerBuilderMap.put("mangrove", MANGROVE);
    }
}
