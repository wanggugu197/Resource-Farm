package com.resource_farm.api.ResourceTree;

import com.resource_farm.ResourceFarm;
import com.resource_farm.utils.RLUtils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class ResourceTreeTypes {

    public static final ResourceTreeType OAK = create("oak"); // 橡树
    public static final ResourceTreeType DARK_OAK = create("dark_oak"); // 深色橡树
    public static final ResourceTreeType BIRCH = create("birch"); // 白桦树
    public static final ResourceTreeType SPRUCE = create("spruce"); // 云杉树
    public static final ResourceTreeType JUNGLE = create("jungle"); // 丛林树
    public static final ResourceTreeType ACACIA = create("acacia"); // 合金欢树
    public static final ResourceTreeType CHERRY = create("cherry"); // 樱花树
    public static final ResourceTreeType MANGROVE = createPropagule("mangrove"); // 红树
    public static final ResourceTreeType PALE_OAK = create("pale_oak"); // 苍白橡木 1.21.1 没有这个

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
                ResourceFarm.id("item/crossover/clump"),
                ResourceFarm.id("item/crossover/clump_overlay"),
                "block.resource_farm.tree.sapling",
                "block.resource_farm.tree.leaves",
                "block.resource_farm.tree.log",
                "block.resource_farm.tree.stripped_log",
                "block.resource_farm.tree.wood",
                "block.resource_farm.tree.stripped_wood",
                "block.resource_farm.tree.planks",
                "item.resource_farm.tree.resin",
                "item.resource_farm.tree.fruit",
                "item.resource_farm.tree.clump");
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
                ResourceFarm.id("item/crossover/clump"),
                ResourceFarm.id("item/crossover/clump_overlay"),
                "block.resource_farm.tree.propagule",
                "block.resource_farm.tree.leaves",
                "block.resource_farm.tree.log",
                "block.resource_farm.tree.stripped_log",
                "block.resource_farm.tree.wood",
                "block.resource_farm.tree.stripped_wood",
                "block.resource_farm.tree.planks",
                "item.resource_farm.tree.resin",
                "item.resource_farm.tree.fruit",
                "item.resource_farm.tree.clump");
    }
}
