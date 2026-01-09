package com.resource_farm.data.tree.builder;

import com.resource_farm.config.ResourceFarmConfigHolder;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.data.misc.ResourceFarmBlockTags;
import com.resource_farm.data.misc.ResourceFarmItemTags;
import com.resource_farm.data.tree.ResourceTree;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagLoader;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;

import static com.resource_farm.common.Manager.ResourceFarmTagManager.makeBlockTagEntry;
import static com.resource_farm.common.Manager.ResourceFarmTagManager.makeItemTagEntry;

public class TreeTagInsert {

    // === BlockTags 缓存 ===
    private static final ResourceLocation BLOCK_TAG_SAPLINGS = BlockTags.SAPLINGS.location();
    private static final ResourceLocation BLOCK_TAG_LEAVES = BlockTags.LEAVES.location();
    private static final ResourceLocation BLOCK_TAG_MINEABLE_WITH_HOE = BlockTags.MINEABLE_WITH_HOE.location();
    private static final ResourceLocation BLOCK_TAG_LOGS = BlockTags.LOGS.location();
    private static final ResourceLocation BLOCK_TAG_PLANKS = BlockTags.PLANKS.location();
    private static final ResourceLocation BLOCK_TAG_MINEABLE_WITH_AXE = BlockTags.MINEABLE_WITH_AXE.location();

    private static final ResourceLocation BLOCK_TAG_RESOURCE_SAPLING = ResourceFarmBlockTags.RESOURCE_SAPLING.location();
    private static final ResourceLocation BLOCK_TAG_RESOURCE_LEAVES = ResourceFarmBlockTags.RESOURCE_LEAVES.location();
    private static final ResourceLocation BLOCK_TAG_RESOURCE_LOG = ResourceFarmBlockTags.RESOURCE_LOG.location();
    private static final ResourceLocation BLOCK_TAG_RESOURCE_PLANKS = ResourceFarmBlockTags.RESOURCE_PLANKS.location();

    // === ItemTags 缓存 ===
    private static final ResourceLocation ITEM_TAG_SAPLINGS = ItemTags.SAPLINGS.location();
    private static final ResourceLocation ITEM_TAG_LEAVES = ItemTags.LEAVES.location();
    private static final ResourceLocation ITEM_TAG_LOGS_THAT_BURN = ItemTags.LOGS_THAT_BURN.location();
    private static final ResourceLocation ITEM_TAG_PLANKS = ItemTags.PLANKS.location();

    private static final ResourceLocation ITEM_TAG_RESOURCE_SAPLING = ResourceFarmItemTags.RESOURCE_SAPLING.location();
    private static final ResourceLocation ITEM_TAG_RESOURCE_LEAVES = ResourceFarmItemTags.RESOURCE_LEAVES.location();
    private static final ResourceLocation ITEM_TAG_RESOURCE_LOG = ResourceFarmItemTags.RESOURCE_LOG.location();
    private static final ResourceLocation ITEM_TAG_RESOURCE_PLANKS = ResourceFarmItemTags.RESOURCE_PLANKS.location();
    private static final ResourceLocation ITEM_TAG_RESOURCE_RESIN = ResourceFarmItemTags.RESOURCE_RESIN.location();
    private static final ResourceLocation ITEM_TAG_RESOURCE_FRUIT = ResourceFarmItemTags.RESOURCE_FRUIT.location();

    public static void generateBlockTags(Object2ObjectOpenHashMap<ResourceLocation, List<TagLoader.EntryWithSource>> tempBlockTagMap) {
        // 遍历ResourceTree，条目收集
        ResourceFarmBlocks.ResourceTreeKeyList.forEach(treeId -> {
            ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
            if (resourceTree == null) return;

            BlockEntry<?> saplingEntry = resourceTree.getSapling();
            if (saplingEntry != null) {
                TagLoader.EntryWithSource saplingBlockEntry = makeBlockTagEntry(saplingEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_SAPLINGS, k -> new ArrayList<>()).add(saplingBlockEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_SAPLING, k -> new ArrayList<>()).add(saplingBlockEntry);
            }
            BlockEntry<?> leavesEntry = resourceTree.getLeaves();
            if (leavesEntry != null) {
                TagLoader.EntryWithSource leavesBlockEntry = makeBlockTagEntry(leavesEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_LEAVES, k -> new ArrayList<>()).add(leavesBlockEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_LEAVES, k -> new ArrayList<>()).add(leavesBlockEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_MINEABLE_WITH_HOE, k -> new ArrayList<>()).add(leavesBlockEntry);
            }
            BlockEntry<?> logEntry = resourceTree.getLog();
            if (logEntry != null) {
                TagLoader.EntryWithSource logBlockEntry = makeBlockTagEntry(logEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_LOGS, k -> new ArrayList<>()).add(logBlockEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(logBlockEntry);
            }
            if (ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.blockGeneration.generateStrippedLog) {
                BlockEntry<?> strippedLogEntry = resourceTree.getStrippedLog();
                if (strippedLogEntry != null) {
                    TagLoader.EntryWithSource strippedLogBlockEntry = makeBlockTagEntry(strippedLogEntry);
                    tempBlockTagMap.computeIfAbsent(BLOCK_TAG_LOGS, k -> new ArrayList<>()).add(strippedLogBlockEntry);
                    tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(strippedLogBlockEntry);
                }
            }
            if (ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.blockGeneration.generateWood) {
                BlockEntry<?> woodEntry = resourceTree.getWood();
                if (woodEntry != null) {
                    TagLoader.EntryWithSource woodBlockEntry = makeBlockTagEntry(woodEntry);
                    tempBlockTagMap.computeIfAbsent(BLOCK_TAG_LOGS, k -> new ArrayList<>()).add(woodBlockEntry);
                    tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(woodBlockEntry);
                }
            }
            if (ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.blockGeneration.generateStrippedWood) {
                BlockEntry<?> strippedWoodEntry = resourceTree.getStrippedWood();
                if (strippedWoodEntry != null) {
                    TagLoader.EntryWithSource strippedWoodBlockEntry = makeBlockTagEntry(strippedWoodEntry);
                    tempBlockTagMap.computeIfAbsent(BLOCK_TAG_LOGS, k -> new ArrayList<>()).add(strippedWoodBlockEntry);
                    tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(strippedWoodBlockEntry);
                }
            }
            if (ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.blockGeneration.generatePlanks) {
                BlockEntry<?> planksEntry = resourceTree.getPlanks();
                if (planksEntry != null) {
                    TagLoader.EntryWithSource planksBlockEntry = makeBlockTagEntry(planksEntry);
                    tempBlockTagMap.computeIfAbsent(BLOCK_TAG_PLANKS, k -> new ArrayList<>()).add(planksBlockEntry);
                    tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_PLANKS, k -> new ArrayList<>()).add(planksBlockEntry);
                    tempBlockTagMap.computeIfAbsent(BLOCK_TAG_MINEABLE_WITH_AXE, k -> new ArrayList<>()).add(planksBlockEntry);
                }
            }
        });
    }

    public static void generateItemTags(Object2ObjectOpenHashMap<ResourceLocation, List<TagLoader.EntryWithSource>> tempItemTagMap) {
        // 遍历ResourceTree，条目收集
        ResourceFarmBlocks.ResourceTreeKeyList.forEach(treeId -> {
            ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
            if (resourceTree == null) return;

            BlockEntry<?> saplingEntry = resourceTree.getSapling();
            if (saplingEntry != null) {
                TagLoader.EntryWithSource saplingItemEntry = makeItemTagEntry(saplingEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_SAPLINGS, k -> new ArrayList<>()).add(saplingItemEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_SAPLING, k -> new ArrayList<>()).add(saplingItemEntry);
            }
            BlockEntry<?> leavesEntry = resourceTree.getLeaves();
            if (leavesEntry != null) {
                TagLoader.EntryWithSource leavesItemEntry = makeItemTagEntry(leavesEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_LEAVES, k -> new ArrayList<>()).add(leavesItemEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_LEAVES, k -> new ArrayList<>()).add(leavesItemEntry);
            }
            BlockEntry<?> logEntry = resourceTree.getLog();
            if (logEntry != null) {
                TagLoader.EntryWithSource logItemEntry = makeItemTagEntry(logEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_LOGS_THAT_BURN, k -> new ArrayList<>()).add(logItemEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(logItemEntry);
            }
            if (ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.blockGeneration.generateStrippedLog) {
                BlockEntry<?> strippedLogEntry = resourceTree.getStrippedLog();
                if (strippedLogEntry != null) {
                    TagLoader.EntryWithSource strippedLogItemEntry = makeItemTagEntry(strippedLogEntry);
                    tempItemTagMap.computeIfAbsent(ITEM_TAG_LOGS_THAT_BURN, k -> new ArrayList<>()).add(strippedLogItemEntry);
                    tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(strippedLogItemEntry);
                }
            }
            if (ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.blockGeneration.generateWood) {
                BlockEntry<?> woodEntry = resourceTree.getWood();
                if (woodEntry != null) {
                    TagLoader.EntryWithSource woodItemEntry = makeItemTagEntry(woodEntry);
                    tempItemTagMap.computeIfAbsent(ITEM_TAG_LOGS_THAT_BURN, k -> new ArrayList<>()).add(woodItemEntry);
                    tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(woodItemEntry);
                }
            }
            if (ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.blockGeneration.generateStrippedWood) {
                BlockEntry<?> strippedWoodEntry = resourceTree.getStrippedWood();
                if (strippedWoodEntry != null) {
                    TagLoader.EntryWithSource strippedWoodItemEntry = makeItemTagEntry(strippedWoodEntry);
                    tempItemTagMap.computeIfAbsent(ITEM_TAG_LOGS_THAT_BURN, k -> new ArrayList<>()).add(strippedWoodItemEntry);
                    tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(strippedWoodItemEntry);
                }
            }
            if (ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.blockGeneration.generatePlanks) {
                BlockEntry<?> planksEntry = resourceTree.getPlanks();
                if (planksEntry != null) {
                    TagLoader.EntryWithSource planksItemEntry = makeItemTagEntry(planksEntry);
                    tempItemTagMap.computeIfAbsent(ITEM_TAG_PLANKS, k -> new ArrayList<>()).add(planksItemEntry);
                    tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_PLANKS, k -> new ArrayList<>()).add(planksItemEntry);
                }
            }
            ItemEntry<?> resinEntry = resourceTree.getResin();
            if (resinEntry != null) {
                TagLoader.EntryWithSource resinItemEntry = makeItemTagEntry(resinEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_RESIN, k -> new ArrayList<>()).add(resinItemEntry);
            }
            ItemEntry<?> fruitEntry = resourceTree.getFruit();
            if (fruitEntry != null) {
                TagLoader.EntryWithSource fruitItemEntry = makeItemTagEntry(fruitEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_FRUIT, k -> new ArrayList<>()).add(fruitItemEntry);
            }
        });
    }
}
