package com.maple.resource_farm.ResourceTree.builder;

import com.maple.resource_farm.ResourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.ResourceTree.block.*;
import com.maple.resource_farm.ResourceTree.data.dataMaps.ResourceFarmMaps;
import com.maple.resource_farm.ResourceTree.data.dataMaps.ResourceTreeBaseType;
import com.maple.resource_farm.ResourceTree.data.dataMaps.ResourceTreeConfig;
import com.maple.resource_farm.ResourceTree.data.dataMaps.ResourceTreeGrower;
import com.maple.resource_farm.ResourceTree.item.ResourceBlockItem;
import com.maple.resource_farm.ResourceTree.item.ResourceClumpItem;
import com.maple.resource_farm.ResourceTree.item.ResourceFruitItem;
import com.maple.resource_farm.ResourceTree.item.ResourceResinItem;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;

import static com.maple.resource_farm.ResourceFarm.REGISTRY;
import static com.maple.resource_farm.data.misc.ResourceFarmCreativeModeTabs.TREE_TAB;

/**
 * ResourceFarm tree block/item registration.
 */
public class TreeBuilder {

    /** No-op / white tint (faces without tintindex ignore this layer). */
    private static final BlockTintSource BLANK_TINT = BlockTintSources.constant(-1);

    public static void registerResourceTree() {
        ResourceTreeAccessManagement.ResourceTreeMap.forEach((treeId, resourceTree) -> {
            if (resourceTree == null) return;

            ResourceTreeConfig resourceTreeConfig = resourceTree.getResourceTreeConfig();

            String name = resourceTreeConfig.id();
            // layer 1: resource color for overlay models (tintindex 1)
            BlockTintSource overlayTint = BlockTintSources.constant(resourceTreeConfig.color());
            // Dual-layer for log/wood/planks/sapling: base uncolored, overlay colored
            BlockTintSource[] overlayOnlyLayers = { BLANK_TINT, overlayTint };
            // Leaves: base foliage (tintindex 0 on vanilla leaves models) + overlay resource color
            BlockTintSource[] leavesLayers = {
                    leavesBaseTint(resourceTreeConfig.treeType()),
                    overlayTint
            };

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
                            .blockTintSource(overlayOnlyLayers)
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
                            .blockTintSource(leavesLayers)
                            .item(
                                    ResourceBlockItem::new,
                                    item -> item.addTab(TREE_TAB.getKey()))
                            .register());

            resourceTree.setLog(
                    REGISTRY.block(name + "_log", props -> ResourceLogBlock.create(treeId, props))
                            .initialProperties(() -> Blocks.OAK_LOG)
                            .noBlockstate()
                            .blockTintSource(overlayOnlyLayers)
                            .item(
                                    ResourceBlockItem::new,
                                    item -> item.addTab(TREE_TAB.getKey()))
                            .register());

            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog) {
                resourceTree.setStrippedLog(
                        REGISTRY.block("stripped_" + name + "_log", props -> ResourceStrippedLogBlock.create(treeId, props))
                                .initialProperties(() -> Blocks.STRIPPED_OAK_LOG)
                                .noBlockstate()
                                .blockTintSource(overlayOnlyLayers)
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
                                .blockTintSource(overlayOnlyLayers)
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
                                .blockTintSource(overlayOnlyLayers)
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
                                .blockTintSource(overlayOnlyLayers)
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

    /**
     * Layer 0 tint for leaves base models (matches vanilla leaf block colors / item tints).
     * Cherry and pale oak textures are pre-colored — blank layer.
     */
    private static BlockTintSource leavesBaseTint(ResourceTreeBaseType treeType) {
        if (treeType == null || treeType.type() == null) {
            return BlockTintSources.foliage();
        }
        return switch (treeType.type()) {
            case "birch" -> BlockTintSources.constant(FoliageColor.FOLIAGE_BIRCH);
            case "spruce" -> BlockTintSources.constant(FoliageColor.FOLIAGE_EVERGREEN);
            case "mangrove" -> BlockTintSources.constant(FoliageColor.FOLIAGE_MANGROVE);
            case "cherry", "pale_oak" -> BLANK_TINT;
            default -> BlockTintSources.foliage(); // oak / jungle / acacia / dark_oak
        };
    }
}
