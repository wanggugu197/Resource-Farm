package com.maple.resource_farm.resourceTree.builder;

import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.resourceTree.block.*;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceFarmMaps;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeConfig;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeGrower;
import com.maple.resource_farm.resourceTree.item.ResourceBlockItem;
import com.maple.resource_farm.resourceTree.item.ResourceClumpItem;
import com.maple.resource_farm.resourceTree.item.ResourceFruitItem;
import com.maple.resource_farm.resourceTree.item.ResourceResinItem;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;

import static com.maple.resource_farm.ResourceFarm.REGISTRY;
import static com.maple.resource_farm.data.misc.ResourceFarmCreativeModeTabs.TREE_TAB;
import static com.maple.resource_farm.resourceTree.builder.TreeBuilderClientHelper.leavesLayers;
import static com.maple.resource_farm.resourceTree.builder.TreeBuilderClientHelper.overlayOnlyLayers;

/**
 * ResourceFarm tree block/item registration.
 */
public class TreeBuilder {

    public static void registerResourceTree() {
        ResourceTreeAccessManagement.ResourceTreeMap.forEach((treeId, resourceTree) -> {
            if (resourceTree == null) return;

            ResourceTreeConfig resourceTreeConfig = resourceTree.getResourceTreeConfig();

            String name = resourceTreeConfig.id();

            resourceTree.setTreeConfigKey(com.maple.resource_farm.ResourceFarm.id(treeId));

            Identifier growerKey = resourceTreeConfig.growerId();
            ResourceTreeGrower grower = ResourceFarmMaps.getGrower(growerKey);
            if (grower == null) {
                throw new IllegalStateException(
                        "ResourceTreeGrower not found for key " + growerKey + " (tree " + treeId + ")");
            }

            resourceTree.setSapling(
                    REGISTRY.block(name + "_sapling", props -> ResourceSaplingBlock.create(
                            treeId,
                            grower,
                            props.noCollision().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY),
                            resourceTreeConfig.fertilizeSetting(),
                            resourceTreeConfig.growthFrequency(),
                            resourceTreeConfig.customPlaceBlock(),
                            resourceTreeConfig.customPlaceBlockTag()))
                            .initialProperties(() -> Blocks.OAK_SAPLING)
                            .noBlockstate()
                            .blockTintSource(() -> () -> overlayOnlyLayers(resourceTreeConfig.color()))
                            .item(
                                    ResourceBlockItem::new,
                                    item -> item
                                            .addTab(TREE_TAB.getKey())
                                            .addTooltip((collector, stack) -> {
                                                if (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ResourceSaplingBlock sapling) {
                                                    sapling.appendSaplingTooltip(collector);
                                                }
                                            }))
                            .register());

            resourceTree.setLeaves(
                    REGISTRY.block(name + "_leaves", props -> ResourceLeavesBlock.create(treeId, props))
                            .initialProperties(() -> Blocks.OAK_LEAVES)
                            .noBlockstate()
                            .blockTintSource(() -> () -> leavesLayers(resourceTreeConfig.color(), resourceTreeConfig.treeType()))
                            .item(
                                    ResourceBlockItem::new,
                                    item -> item.addTab(TREE_TAB.getKey()))
                            .register());

            resourceTree.setLog(
                    REGISTRY.block(name + "_log", props -> ResourceLogBlock.create(treeId, props))
                            .initialProperties(() -> Blocks.OAK_LOG)
                            .noBlockstate()
                            .blockTintSource(() -> () -> overlayOnlyLayers(resourceTreeConfig.color()))
                            .item(
                                    ResourceBlockItem::new,
                                    item -> item.addTab(TREE_TAB.getKey()))
                            .register());

            if (ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedLog) {
                resourceTree.setStrippedLog(
                        REGISTRY.block("stripped_" + name + "_log", props -> ResourceStrippedLogBlock.create(treeId, props))
                                .initialProperties(() -> Blocks.STRIPPED_OAK_LOG)
                                .noBlockstate()
                                .blockTintSource(() -> () -> overlayOnlyLayers(resourceTreeConfig.color()))
                                .item(
                                        ResourceBlockItem::new,
                                        item -> item.addTab(TREE_TAB.getKey()))
                                .register());
            }

            if (ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateWood) {
                resourceTree.setWood(
                        REGISTRY.block(name + "_wood", props -> ResourceWoodBlock.create(treeId, props))
                                .initialProperties(() -> Blocks.OAK_WOOD)
                                .noBlockstate()
                                .blockTintSource(() -> () -> overlayOnlyLayers(resourceTreeConfig.color()))
                                .item(
                                        ResourceBlockItem::new,
                                        item -> item.addTab(TREE_TAB.getKey()))
                                .register());
            }

            if (ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedWood) {
                resourceTree.setStrippedWood(
                        REGISTRY.block("stripped_" + name + "_wood", props -> ResourceStrippedWoodBlock.create(treeId, props))
                                .initialProperties(() -> Blocks.STRIPPED_OAK_WOOD)
                                .noBlockstate()
                                .blockTintSource(() -> () -> overlayOnlyLayers(resourceTreeConfig.color()))
                                .item(
                                        ResourceBlockItem::new,
                                        item -> item.addTab(TREE_TAB.getKey()))
                                .register());
            }

            if (ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generatePlanks) {
                resourceTree.setPlanks(
                        REGISTRY.block(name + "_planks", props -> ResourcePlanksBlock.create(treeId, props))
                                .initialProperties(() -> Blocks.OAK_PLANKS)
                                .noBlockstate()
                                .blockTintSource(() -> () -> overlayOnlyLayers(resourceTreeConfig.color()))
                                .item(
                                        ResourceBlockItem::new,
                                        item -> item.addTab(TREE_TAB.getKey()))
                                .register());
            }

            resourceTree.setResin(
                    REGISTRY.item(name + "_resin", props -> ResourceResinItem.create(treeId, props))
                            .addTab(TREE_TAB.getKey())
                            .register());

            resourceTree.setFruit(
                    REGISTRY.item(name + "_fruit", props -> ResourceFruitItem.create(treeId, props))
                            .addTab(TREE_TAB.getKey())
                            .register());

            if (ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateClump()) {
                resourceTree.setClump(
                        REGISTRY.item(name + "_clump", props -> ResourceClumpItem.create(treeId, props))
                                .addTab(TREE_TAB.getKey())
                                .register());
            }

        });
    }
}
