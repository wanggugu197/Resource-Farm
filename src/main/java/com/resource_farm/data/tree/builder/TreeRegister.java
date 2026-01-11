package com.resource_farm.data.tree.builder;

import com.resource_farm.api.ResourceOre.ResourceOreType;
import com.resource_farm.api.ResourceTree.ResourceTreeType;
import com.resource_farm.api.block.FertilizeSettings;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.data.tree.ResourceTree;
import com.resource_farm.data.tree.ResourceTreeConfig;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public class TreeRegister {

    public static void createResourceTree(ResourceTreeConfig resourceTreeConfig) {
        String treeId = resourceTreeConfig.id() + "_tree";
        ResourceFarmBlocks.ResourceTreeMap.put(treeId, ResourceTree.create(resourceTreeConfig));
        ResourceFarmBlocks.ResourceTreeKeyList.add(treeId);
    }

    public static void createResourceTree(@Nullable String correspondingItem,
                                          @Nullable String translateKey,
                                          boolean autoRecipe,
                                          int productOutput,
                                          ResourceTreeType treeStyle,
                                          ResourceOreType oreStyle,
                                          FertilizeSettings fertilizeSetting,
                                          int growthFrequency,
                                          @Nullable String customPlaceBlock,
                                          @Nullable TagKey<Block> customPlaceBlockTag,
                                          int lightLevel,
                                          int colors) {
        createResourceTree(ResourceTreeConfig.create(
                correspondingItem, translateKey, autoRecipe, productOutput, treeStyle, oreStyle, fertilizeSetting, growthFrequency,
                customPlaceBlock, customPlaceBlockTag, lightLevel, colors));
    }

    public static void createSimpleResourceTree(@Nullable String itemId,
                                                @Nullable String translate,
                                                boolean autoRecipe,
                                                int count,
                                                ResourceTreeType treeStyle,
                                                ResourceOreType oreStyle,
                                                int lightLevel,
                                                int colors) {
        createResourceTree(itemId, translate, autoRecipe, count, treeStyle, oreStyle,
                FertilizeSettings.DEFAULT, 10, null, null, lightLevel, colors);
    }

    public static void createSimpleResourceTree(@Nullable String itemId,
                                                @Nullable String translate,
                                                boolean autoRecipe,
                                                int count,
                                                ResourceTreeType treeStyle,
                                                ResourceOreType oreStyle,
                                                int colors) {
        createSimpleResourceTree(itemId, translate, autoRecipe, count, treeStyle, oreStyle, 0, colors);
    }

    public static void createMissingItemResourceTree(@Nullable String translate,
                                                     ResourceTreeType treeStyle,
                                                     ResourceOreType oreStyle,
                                                     int lightLevel,
                                                     int colors) {
        createResourceTree(null, translate, false, 1, treeStyle, oreStyle,
                FertilizeSettings.DEFAULT, 10, null, null, lightLevel, colors);
    }

    public static void createMissingItemResourceTree(@Nullable String translate,
                                                     ResourceTreeType treeStyle,
                                                     ResourceOreType oreStyle,
                                                     int colors) {
        createMissingItemResourceTree(translate, treeStyle, oreStyle, 0, colors);
    }

    public static void removeResourceTree(String treeId) {
        ResourceFarmBlocks.ResourceTreeMap.remove(treeId);
        ResourceFarmBlocks.ResourceTreeKeyList.remove(treeId);
    }

    public static void removeResourceTrees(String... treeIds) {
        for (String treeId : treeIds) removeResourceTree(treeId);
    }
}
