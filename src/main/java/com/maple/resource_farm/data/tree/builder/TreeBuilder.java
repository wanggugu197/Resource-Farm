package com.maple.resource_farm.data.tree.builder;

import com.maple.resource_farm.common.block.ResourceTree.*;
import com.maple.resource_farm.common.block.grower.ResourceTreeGrowerType;
import com.maple.resource_farm.common.item.ResourceTree.ResourceBlockItem;
import com.maple.resource_farm.common.item.ResourceTree.ResourceClumpItem;
import com.maple.resource_farm.common.item.ResourceTree.ResourceFruitItem;
import com.maple.resource_farm.common.item.ResourceTree.ResourceResinItem;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.data.ResourceFarmBlocks;
import com.maple.resource_farm.data.tree.ResourceTree;
import com.maple.resource_farm.data.tree.ResourceTreeConfig;

import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;

import static com.maple.resource_farm.ResourceFarm.REGISTRY;
import static com.maple.resource_farm.data.misc.ResourceFarmCreativeModeTabs.TREE_TAB;

/**
 * ResourceFarm 树木方块/物品注册。
 * <p>
 * 着色：固定 {@link BlockTintSources} + 动态着色模型。
 * 发光：不在方块属性上设置 lightLevel，由 {@link TreeModelRenderer} 写入
 * element {@code light_emission}（配置 {@link ResourceTreeConfig#lightLevel()}）。
 */
public class TreeBuilder {

    public static void registerResourceTree() {
        ResourceFarmBlocks.ResourceTreeKeyList.forEach(treeId -> {
            ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
            if (resourceTree == null) return;

            ResourceTreeConfig resourceTreeConfig = resourceTree.getResourceTreeConfig();

            String name = resourceTreeConfig.id();
            var blockTint = BlockTintSources.constant(resourceTreeConfig.color());

            resourceTree.setTreeConfigKey(com.maple.resource_farm.ResourceFarm.id(treeId));

            resourceTree.setSapling(
                    REGISTRY.block(name + "_sapling", props -> ResourceSaplingBlock.create(
                            treeId,
                            ResourceTreeGrowerType.ResourceTreeGrowerBuilderMap.get(resourceTreeConfig.treeType().type()).id(treeId).build(),
                            props.noCollision().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY),
                            resourceTreeConfig.fertilizeSetting(),
                            resourceTreeConfig.growthFrequency(),
                            resourceTreeConfig.customPlaceBlock(),
                            resourceTreeConfig.customPlaceBlockTag()))
                            .initialProperties(() -> Blocks.OAK_SAPLING)
                            .noBlockstate()
                            .blockTintSource(blockTint)
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
                            .blockTintSource(blockTint)
                            .item(
                                    ResourceBlockItem::new,
                                    item -> item.addTab(TREE_TAB.getKey()))
                            .register());

            resourceTree.setLog(
                    REGISTRY.block(name + "_log", props -> ResourceLogBlock.create(treeId, props))
                            .initialProperties(() -> Blocks.OAK_LOG)
                            .noBlockstate()
                            .blockTintSource(blockTint)
                            .item(
                                    ResourceBlockItem::new,
                                    item -> item.addTab(TREE_TAB.getKey()))
                            .register());

            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog) {
                resourceTree.setStrippedLog(
                        REGISTRY.block("stripped_" + name + "_log", props -> ResourceStrippedLogBlock.create(treeId, props))
                                .initialProperties(() -> Blocks.STRIPPED_OAK_LOG)
                                .noBlockstate()
                                .blockTintSource(blockTint)
                                .item(
                                        ResourceBlockItem::new,
                                        item -> item.addTab(TREE_TAB.getKey()))
                                .register());
            }

            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood) {
                resourceTree.setWood(
                        REGISTRY.block(name + "_wood", props -> ResourceWoodBlock.create(treeId, props))
                                .initialProperties(() -> Blocks.OAK_WOOD)
                                .noBlockstate()
                                .blockTintSource(blockTint)
                                .item(
                                        ResourceBlockItem::new,
                                        item -> item.addTab(TREE_TAB.getKey()))
                                .register());
            }

            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood) {
                resourceTree.setStrippedWood(
                        REGISTRY.block("stripped_" + name + "_wood", props -> ResourceStrippedWoodBlock.create(treeId, props))
                                .initialProperties(() -> Blocks.STRIPPED_OAK_WOOD)
                                .noBlockstate()
                                .blockTintSource(blockTint)
                                .item(
                                        ResourceBlockItem::new,
                                        item -> item.addTab(TREE_TAB.getKey()))
                                .register());
            }

            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks) {
                resourceTree.setPlanks(
                        REGISTRY.block(name + "_planks", props -> ResourcePlanksBlock.create(treeId, props))
                                .initialProperties(() -> Blocks.OAK_PLANKS)
                                .noBlockstate()
                                .blockTintSource(blockTint)
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

            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateClump()) {
                resourceTree.setClump(
                        REGISTRY.item(name + "_clump", props -> ResourceClumpItem.create(treeId, props))
                                .addTab(TREE_TAB.getKey())
                                .register());
            }

        });
    }
}
