package com.resource_farm.data.tree;

import com.resource_farm.config.TreeRegisterConfig;
import com.resource_farm.config.TreeRemoveConfig;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.data.tree.builder.TreesRegister;

public class RegisterResourceTrees {

    public static void createResourceTree(ResourceTreeConfig resourceTreeConfig) {
        ResourceFarmBlocks.ResourceTreeMap.put(resourceTreeConfig.id() + "_tree", ResourceTree.create(resourceTreeConfig));
    }

    public static void createResourceTree(ResourceTreeConfig... resourceTreeConfigs) {
        for (ResourceTreeConfig resourceTreeConfig : resourceTreeConfigs)
            ResourceFarmBlocks.ResourceTreeMap.put(resourceTreeConfig.id() + "_tree", ResourceTree.create(resourceTreeConfig));
    }

    public static void removeResourceTree(String treeName) {
        ResourceFarmBlocks.ResourceTreeMap.remove(treeName);
    }

    public static void removeResourceTree(String... treeNames) {
        for (String treeName : treeNames)
            ResourceFarmBlocks.ResourceTreeMap.remove(treeName);
    }

    public static void registerTree() {
        // 资源树-基础注册
        TreesRegister.commonRegister();

        // 资源树-配置文件注册
        TreeRegisterConfig.parseJSONtoRegisterTree();

        // 资源树-配置文件移除
        TreeRemoveConfig.parseJSONtoRemoveTree();
    }
}
