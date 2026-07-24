package com.maple.resource_farm.data.tree.builder;

import com.maple.resource_farm.api.block.ColoringSettings;
import com.maple.resource_farm.api.block.TintableBlock;
import com.maple.resource_farm.common.block.ResourceTree.*;
import com.maple.resource_farm.common.block.grower.ResourceTreeGrowerType;
import com.maple.resource_farm.common.item.ColoringBlockItem;
import com.maple.resource_farm.common.item.ResourceTree.ResourceClumpItem;
import com.maple.resource_farm.common.item.ResourceTree.ResourceFruitItem;
import com.maple.resource_farm.common.item.ResourceTree.ResourceResinItem;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.data.ResourceFarmBlocks;
import com.maple.resource_farm.data.tree.ResourceTree;
import com.maple.resource_farm.data.tree.ResourceTreeConfig;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;

import static com.maple.resource_farm.common.registry.ResourceFarmRegistration.REGISTRY;
import static com.maple.resource_farm.data.misc.ResourceFarmCreativeModeTabs.TREE_TAB;

/**
 * ResourceFarm 树木方块/物品注册（RegistryLib + MapleUtilLib）。
 * <p>
 * 模型/loot/标签由动态包注入，此处仅做游戏对象注册；跳过 blockstate/default model 生成。
 */
public class TreeBuilder {

    private static final BlockTintSource[] BLOCK_TINTS = TintableBlock.tintSources().toArray(BlockTintSource[]::new);

    public static void registerResourceTree() {
        ResourceFarmBlocks.ResourceTreeKeyList.forEach(treeId -> {
            ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
            if (resourceTree == null) return;

            ResourceTreeConfig resourceTreeConfig = resourceTree.getResourceTreeConfig();

            String name = resourceTreeConfig.id();
            int lightLevel = resourceTreeConfig.lightLevel();
            ColoringSettings coloringSettings = resourceTreeConfig.coloringSettings();
            ColoringSettings leavesColoringSettings = ColoringSettings.getLeave(coloringSettings);

            resourceTree.setTreeConfigKey(com.maple.resource_farm.ResourceFarm.id(treeId));

            resourceTree.setSapling(
                    REGISTRY.block(name + "_sapling", props -> ResourceSaplingBlock.create(
                            treeId,
                            ResourceTreeGrowerType.ResourceTreeGrowerBuilderMap.get(resourceTreeConfig.treeType().type()).id(treeId).build(),
                            props.noCollision().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY),
                            resourceTreeConfig.fertilizeSetting(),
                            resourceTreeConfig.growthFrequency(),
                            resourceTreeConfig.customPlaceBlock(),
                            resourceTreeConfig.customPlaceBlockTag(),
                            lightLevel,
                            coloringSettings))
                            .initialProperties(() -> Blocks.OAK_SAPLING)
                            .properties(props -> props
                                    .noOcclusion()
                                    .lightLevel(state -> lightLevel))
                            .noBlockstate()
                            .blockTintSource(BLOCK_TINTS)
                            .item(
                                    (block, props) -> ColoringBlockItem.create(block, props, coloringSettings),
                                    item -> item.addTab(TREE_TAB.getKey()))
                            .register());

            resourceTree.setLeaves(
                    REGISTRY.block(name + "_leaves", props -> ResourceLeavesBlock.create(
                            treeId,
                            props,
                            lightLevel,
                            leavesColoringSettings))
                            .initialProperties(() -> Blocks.OAK_LEAVES)
                            .noBlockstate()
                            .blockTintSource(BLOCK_TINTS)
                            .item(
                                    (block, props) -> ColoringBlockItem.create(block, props, leavesColoringSettings),
                                    item -> item.addTab(TREE_TAB.getKey()))
                            .register());

            resourceTree.setLog(
                    REGISTRY.block(name + "_log", props -> ResourceLogBlock.create(
                            treeId,
                            props,
                            lightLevel,
                            coloringSettings))
                            .initialProperties(() -> Blocks.OAK_LOG)
                            .noBlockstate()
                            .blockTintSource(BLOCK_TINTS)
                            .item(
                                    (block, props) -> ColoringBlockItem.create(block, props, coloringSettings),
                                    item -> item.addTab(TREE_TAB.getKey()))
                            .register());

            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog) {
                resourceTree.setStrippedLog(
                        REGISTRY.block("stripped_" + name + "_log", props -> ResourceStrippedLogBlock.create(
                                treeId,
                                props,
                                lightLevel,
                                coloringSettings))
                                .initialProperties(() -> Blocks.STRIPPED_OAK_LOG)
                                .noBlockstate()
                                .blockTintSource(BLOCK_TINTS)
                                .item(
                                        (block, props) -> ColoringBlockItem.create(block, props, coloringSettings),
                                        item -> item.addTab(TREE_TAB.getKey()))
                                .register());
            }

            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood) {
                resourceTree.setWood(
                        REGISTRY.block(name + "_wood", props -> ResourceWoodBlock.create(
                                treeId,
                                props,
                                lightLevel,
                                coloringSettings))
                                .initialProperties(() -> Blocks.OAK_WOOD)
                                .noBlockstate()
                                .blockTintSource(BLOCK_TINTS)
                                .item(
                                        (block, props) -> ColoringBlockItem.create(block, props, coloringSettings),
                                        item -> item.addTab(TREE_TAB.getKey()))
                                .register());
            }

            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood) {
                resourceTree.setStrippedWood(
                        REGISTRY.block("stripped_" + name + "_wood", props -> ResourceStrippedWoodBlock.create(
                                treeId,
                                props,
                                lightLevel,
                                coloringSettings))
                                .initialProperties(() -> Blocks.STRIPPED_OAK_WOOD)
                                .noBlockstate()
                                .blockTintSource(BLOCK_TINTS)
                                .item(
                                        (block, props) -> ColoringBlockItem.create(block, props, coloringSettings),
                                        item -> item.addTab(TREE_TAB.getKey()))
                                .register());
            }

            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks) {
                resourceTree.setPlanks(
                        REGISTRY.block(name + "_planks", props -> ResourcePlanksBlock.create(
                                treeId,
                                props,
                                lightLevel,
                                coloringSettings))
                                .initialProperties(() -> Blocks.OAK_PLANKS)
                                .noBlockstate()
                                .blockTintSource(BLOCK_TINTS)
                                .item(
                                        (block, props) -> ColoringBlockItem.create(block, props, coloringSettings),
                                        item -> item.addTab(TREE_TAB.getKey()))
                                .register());
            }

            resourceTree.setResin(
                    REGISTRY.item(name + "_resin", props -> ResourceResinItem.create(treeId, props, coloringSettings))
                            .addTab(TREE_TAB.getKey())
                            .register());

            resourceTree.setFruit(
                    REGISTRY.item(name + "_fruit", props -> ResourceFruitItem.create(treeId, props, coloringSettings))
                            .addTab(TREE_TAB.getKey())
                            .register());

            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateClump()) {
                resourceTree.setClump(
                        REGISTRY.item(name + "_clump", props -> ResourceClumpItem.create(treeId, props, ColoringSettings.reversed(coloringSettings)))
                                .addTab(TREE_TAB.getKey())
                                .register());
            }

        });
    }
}
