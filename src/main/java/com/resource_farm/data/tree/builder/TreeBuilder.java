package com.resource_farm.data.tree.builder;

import com.resource_farm.ResourceFarm;
import com.resource_farm.api.block.ColoringSettings;
import com.resource_farm.api.block.TintableBlock;
import com.resource_farm.api.item.TintableItem;
import com.resource_farm.common.block.ResourceTree.*;
import com.resource_farm.common.block.grower.ResourceTreeGrowerType;
import com.resource_farm.common.item.ColoringBlockItem;
import com.resource_farm.common.item.ResourceTree.ResourceFruitItem;
import com.resource_farm.common.item.ResourceTree.ResourceResinItem;
import com.resource_farm.config.ConfigHolder;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.data.tree.ResourceTreeConfig;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;

import static com.resource_farm.api.registry.ResourceFarmRegistration.REGISTRATE;
import static com.resource_farm.data.misc.ResourceFarmCreativeModeTabs.TREE_TAB;

/**
 * ResourceFarmCore 树木方块注册工具
 */
public class TreeBuilder {

    public static void registerResourceTree() {
        ResourceFarmBlocks.ResourceTreeMap.forEach((treeId, resourceTree) -> {
            ResourceTreeConfig resourceTreeConfig = resourceTree.getResourceTreeConfig();

            String name = resourceTreeConfig.id();
            int lightLevel = resourceTreeConfig.lightLevel();
            ColoringSettings coloringSettings = resourceTreeConfig.coloringSettings();
            ColoringSettings leavesColoringSettings = ColoringSettings.getLeave(coloringSettings);

            resourceTree.setTreeConfigKey(ResourceFarm.id(treeId));

            resourceTree.setSapling(
                    REGISTRATE.object(name + "_sapling")
                            .block(props -> ResourceSaplingBlock.create(
                                    treeId,
                                    ResourceTreeGrowerType.ResourceTreeGrowerBuilderMap.get(resourceTreeConfig.treeType().type()).id(treeId).build(),
                                    props.noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY),
                                    resourceTreeConfig.fertilizeSetting(),
                                    resourceTreeConfig.growthFrequency(),
                                    resourceTreeConfig.customPlaceBlock(),
                                    resourceTreeConfig.customPlaceBlockTag(),
                                    lightLevel,
                                    coloringSettings))
                            .color(() -> TintableBlock::tintColor)
                            .initialProperties(() -> Blocks.OAK_SAPLING)
                            .properties(props -> props
                                    .noOcclusion()
                                    .lightLevel(state -> lightLevel))
                            .item((block, props) -> ColoringBlockItem.create(block, props, coloringSettings))
                            .color(() -> TintableItem::tintColor)
                            .tab(TREE_TAB.getKey())
                            .build()
                            .register());

            resourceTree.setLeaves(
                    REGISTRATE.object(name + "_leaves")
                            .block(props -> ResourceLeavesBlock.create(
                                    treeId,
                                    props,
                                    lightLevel,
                                    leavesColoringSettings))
                            .color(() -> TintableBlock::tintColor)
                            .initialProperties(() -> Blocks.OAK_LEAVES)
                            .item((block, props) -> ColoringBlockItem.create(block, props, leavesColoringSettings))
                            .color(() -> TintableItem::tintColor)
                            .tab(TREE_TAB.getKey())
                            .build()
                            .register());

            resourceTree.setLog(
                    REGISTRATE.object(name + "_log")
                            .block(props -> ResourceLogBlock.create(
                                    treeId,
                                    props,
                                    lightLevel,
                                    coloringSettings))
                            .color(() -> TintableBlock::tintColor)
                            .initialProperties(() -> Blocks.OAK_LOG)
                            .item((block, props) -> ColoringBlockItem.create(block, props, coloringSettings))
                            .color(() -> TintableItem::tintColor)
                            .tab(TREE_TAB.getKey())
                            .build()
                            .register()

            );

            if (ConfigHolder.INSTANCE.tree.blockGeneration.generateStrippedLog) {
                resourceTree.setStrippedLog(
                        REGISTRATE.object("stripped_" + name + "_log")
                                .block(props -> ResourceStrippedLogBlock.create(
                                        treeId,
                                        props,
                                        lightLevel,
                                        coloringSettings))
                                .color(() -> TintableBlock::tintColor)
                                .initialProperties(() -> Blocks.STRIPPED_OAK_LOG)
                                .item((block, props) -> ColoringBlockItem.create(block, props, coloringSettings))
                                .color(() -> TintableItem::tintColor)
                                .tab(TREE_TAB.getKey())
                                .build()
                                .register());
            }

            if (ConfigHolder.INSTANCE.tree.blockGeneration.generateWood) {
                resourceTree.setWood(
                        REGISTRATE.object(name + "_wood")
                                .block(props -> ResourceWoodBlock.create(
                                        treeId,
                                        props,
                                        lightLevel,
                                        coloringSettings))
                                .color(() -> TintableBlock::tintColor)
                                .initialProperties(() -> Blocks.OAK_WOOD)
                                .item((block, props) -> ColoringBlockItem.create(block, props, coloringSettings))
                                .color(() -> TintableItem::tintColor)
                                .tab(TREE_TAB.getKey())
                                .build()
                                .register());
            }

            if (ConfigHolder.INSTANCE.tree.blockGeneration.generateStrippedWood) {
                resourceTree.setStrippedWood(
                        REGISTRATE.object("stripped_" + name + "_wood")
                                .block(props -> ResourceStrippedWoodBlock.create(
                                        treeId,
                                        props,
                                        lightLevel,
                                        coloringSettings))
                                .color(() -> TintableBlock::tintColor)
                                .initialProperties(() -> Blocks.STRIPPED_OAK_WOOD)
                                .item((block, props) -> ColoringBlockItem.create(block, props, coloringSettings))
                                .color(() -> TintableItem::tintColor)
                                .tab(TREE_TAB.getKey())
                                .build()
                                .register());
            }

            if (ConfigHolder.INSTANCE.tree.blockGeneration.generatePlanks) {
                resourceTree.setPlanks(
                        REGISTRATE.object(name + "_planks")
                                .block(props -> ResourcePlanksBlock.create(
                                        treeId,
                                        props,
                                        lightLevel,
                                        coloringSettings))
                                .color(() -> TintableBlock::tintColor)
                                .initialProperties(() -> Blocks.OAK_PLANKS)
                                .item((block, props) -> ColoringBlockItem.create(block, props, coloringSettings))
                                .color(() -> TintableItem::tintColor)
                                .tab(TREE_TAB.getKey())
                                .build()
                                .register());
            }

            resourceTree.setResin(
                    REGISTRATE.object(name + "_resin")
                            .item((props) -> ResourceResinItem.create(treeId, props, coloringSettings))
                            .color(() -> TintableItem::tintColor)
                            .tab(TREE_TAB.getKey())
                            .register());

            resourceTree.setFruit(
                    REGISTRATE.object(name + "_fruit")
                            .item((props) -> ResourceFruitItem.create(treeId, props, coloringSettings))
                            .color(() -> TintableItem::tintColor)
                            .tab(TREE_TAB.getKey())
                            .register());

        });
    }
}
