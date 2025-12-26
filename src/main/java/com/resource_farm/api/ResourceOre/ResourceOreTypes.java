package com.resource_farm.api.ResourceOre;

import com.resource_farm.ResourceFarm;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class ResourceOreTypes {

    public static final ResourceOreType COPPER = create("block/ore/", "copper");
    public static final ResourceOreType DIAMOND = create("block/ore/", "diamond");
    public static final ResourceOreType EMERALD = create("block/ore/", "emerald");
    public static final ResourceOreType GOLD = create("block/ore/", "gold");
    public static final ResourceOreType IRON = create("block/ore/", "iron");
    public static final ResourceOreType LAPIS = create("block/ore/", "lapis");
    public static final ResourceOreType NETHER_GOLD = create("block/ore/", "nether_gold");
    public static final ResourceOreType NETHER_QUARTZ = create("block/ore/", "nether_quartz");
    public static final ResourceOreType REDSTONE = create("block/ore/", "redstone");

    public static final Object2ObjectOpenHashMap<String, ResourceOreType> ORE_TYPES = new Object2ObjectOpenHashMap<>();

    static {
        ORE_TYPES.put("copper", COPPER);
        ORE_TYPES.put("diamond", DIAMOND);
        ORE_TYPES.put("emerald", EMERALD);
        ORE_TYPES.put("gold", GOLD);
        ORE_TYPES.put("iron", IRON);
        ORE_TYPES.put("lapis", LAPIS);
        ORE_TYPES.put("nether_gold", NETHER_GOLD);
        ORE_TYPES.put("nether_quartz", NETHER_QUARTZ);
        ORE_TYPES.put("redstone", REDSTONE);
    }

    public static ResourceOreType create(String contents, String name) {
        return ResourceOreType.of(ResourceFarm.id(contents + name));
    }
}
