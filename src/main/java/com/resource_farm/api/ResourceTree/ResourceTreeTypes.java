package com.resource_farm.api.ResourceTree;

import com.resource_farm.ResourceFarm;
import com.resource_farm.utils.RLUtils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class ResourceTreeTypes {

    public static final ResourceTreeType OAK = create("oak");
    public static final ResourceTreeType DARK_OAK = create("dark_oak");
    public static final ResourceTreeType BIRCH = create("birch");
    public static final ResourceTreeType SPRUCE = create("spruce");
    public static final ResourceTreeType JUNGLE = create("jungle");
    public static final ResourceTreeType ACACIA = create("acacia");
    public static final ResourceTreeType CHERRY = create("cherry");
    public static final ResourceTreeType MANGROVE = createPropagule("mangrove");
    public static final ResourceTreeType PALE_OAK = create("pale_oak");

    public static final Object2ObjectOpenHashMap<String, ResourceTreeType> TREE_TYPES = new Object2ObjectOpenHashMap<>();

    static {
        TREE_TYPES.put("oak", OAK);
        TREE_TYPES.put("dark_oak", DARK_OAK);
        TREE_TYPES.put("birch", BIRCH);
        TREE_TYPES.put("spruce", SPRUCE);
        TREE_TYPES.put("jungle", JUNGLE);
        TREE_TYPES.put("acacia", ACACIA);
        TREE_TYPES.put("cherry", CHERRY);
        TREE_TYPES.put("mangrove", MANGROVE);
        TREE_TYPES.put("pale_oak", PALE_OAK);
    }

    public static ResourceTreeType create(String name) {
        return ResourceTreeType.of(
                name,
                RLUtils.mc("block/" + name + "_sapling"),
                ResourceFarm.id("block/tree/sapling/" + name + "_sapling_overlay"),
                RLUtils.mc("block/" + name + "_leaves"),
                ResourceFarm.id("block/tree/leaves/" + name + "_leaves_overlay"),
                RLUtils.mc("block/" + name + "_log"),
                RLUtils.mc("block/" + name + "_log_top"),
                RLUtils.mc("block/" + "stripped_" + name + "_log"),
                RLUtils.mc("block/" + "stripped_" + name + "_log_top"),
                RLUtils.mc("block/" + name + "_planks"),
                ResourceFarm.id("item/resin/base_resin"),
                ResourceFarm.id("item/resin/base_resin"),
                ResourceFarm.id("item/fruit/base_fruit"),
                ResourceFarm.id("item/fruit/base_fruit_overlay"),
                "block.resource_farm.tree.sapling",
                "block.resource_farm.tree.leaves",
                "block.resource_farm.tree.log",
                "block.resource_farm.tree.stripped_log",
                "block.resource_farm.tree.wood",
                "block.resource_farm.tree.stripped_wood",
                "block.resource_farm.tree.planks",
                "block.resource_farm.tree.resin",
                "block.resource_farm.tree.fruit");
    }

    public static ResourceTreeType createPropagule(String name) {
        return ResourceTreeType.of(
                name,
                RLUtils.mc("block/" + name + "_propagule"),
                ResourceFarm.id("block/tree/sapling/" + name + "_propagule_overlay"),
                RLUtils.mc("block/" + name + "_leaves"),
                ResourceFarm.id("block/tree/leaves/" + name + "_leaves_overlay"),
                RLUtils.mc("block/" + name + "_log"),
                RLUtils.mc("block/" + name + "_log_top"),
                RLUtils.mc("block/" + "stripped_" + name + "_log"),
                RLUtils.mc("block/" + "stripped_" + name + "_log_top"),
                RLUtils.mc("block/" + name + "_planks"),
                ResourceFarm.id("item/resin/base_resin"),
                ResourceFarm.id("item/resin/base_resin"),
                ResourceFarm.id("item/fruit/base_fruit"),
                ResourceFarm.id("item/fruit/base_fruit_overlay"),
                "block.resource_farm.tree.propagule",
                "block.resource_farm.tree.leaves",
                "block.resource_farm.tree.log",
                "block.resource_farm.tree.stripped_log",
                "block.resource_farm.tree.wood",
                "block.resource_farm.tree.stripped_wood",
                "block.resource_farm.tree.planks",
                "block.resource_farm.tree.resin",
                "block.resource_farm.tree.fruit");
    }
}
