package com.resource_farm.data;

import com.resource_farm.data.tree.RegisterResourceTrees;
import com.resource_farm.data.tree.ResourceTree;
import com.resource_farm.data.tree.builder.TreeBuilder;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import static com.resource_farm.ResourceFarm.isDataGen;
import static com.resource_farm.data.tree.builder.TreeInitialization.initializationTreeMap;

public class ResourceFarmBlocks {

    public static void init() {
        initializationTreeMap();
        // 注册方块/物品
        if (!isDataGen()) {
            TreeBuilder.registerResourceTree();
        }
    }

    public static final Object2ObjectOpenHashMap<String, ResourceTree> ResourceTreeMap = new Object2ObjectOpenHashMap<>();

    static {
        RegisterResourceTrees.registerTree();
    }
}
