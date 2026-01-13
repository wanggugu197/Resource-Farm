package com.resource_farm.data.tree.builder;

import com.resource_farm.config.ResourceFarmConfigHolder;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.data.misc.ResourceFarmBlockTags;
import com.resource_farm.data.misc.ResourceFarmItemTags;
import com.resource_farm.data.tree.ResourceTree;
import com.resource_farm.utils.RLUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagLoader;

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
    private static final ResourceLocation BLOCK_TAG_STRIPPED_LOGS = RLUtils.parse("c:stripped_logs");
    private static final ResourceLocation BLOCK_TAG_STRIPPED_WOODS = RLUtils.parse("c:stripped_woods");
    private static final ResourceLocation BLOCK_TAG_PLANKS = BlockTags.PLANKS.location();
    private static final ResourceLocation BLOCK_TAG_MINEABLE_WITH_AXE = BlockTags.MINEABLE_WITH_AXE.location();

    private static final ResourceLocation BLOCK_TAG_RESOURCE_SAPLING = ResourceFarmBlockTags.RESOURCE_SAPLING.location();
    private static final ResourceLocation BLOCK_TAG_RESOURCE_LEAVES = ResourceFarmBlockTags.RESOURCE_LEAVES.location();
    private static final ResourceLocation BLOCK_TAG_RESOURCE_LOG = ResourceFarmBlockTags.RESOURCE_LOG.location();
    private static final ResourceLocation BLOCK_TAG_RESOURCE_PLANKS = ResourceFarmBlockTags.RESOURCE_PLANKS.location();

    public static void generateBlockTags(Object2ObjectOpenHashMap<ResourceLocation, List<TagLoader.EntryWithSource>> tempBlockTagMap) {
        final boolean STRIPPED_LOG_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog;
        final boolean WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood;
        final boolean STRIPPED_WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood;
        final boolean PLANKS_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks;
        // 遍历ResourceTree，条目收集
        ResourceFarmBlocks.ResourceTreeKeyList.forEach(treeId -> {
            ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
            if (resourceTree == null) return;

            TagLoader.EntryWithSource saplingBlockEntry = makeBlockTagEntry(resourceTree.getSapling());
            tempBlockTagMap.computeIfAbsent(BLOCK_TAG_SAPLINGS, k -> new ArrayList<>()).add(saplingBlockEntry);
            tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_SAPLING, k -> new ArrayList<>()).add(saplingBlockEntry);

            TagLoader.EntryWithSource leavesBlockEntry = makeBlockTagEntry(resourceTree.getLeaves());
            tempBlockTagMap.computeIfAbsent(BLOCK_TAG_LEAVES, k -> new ArrayList<>()).add(leavesBlockEntry);
            tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_LEAVES, k -> new ArrayList<>()).add(leavesBlockEntry);
            tempBlockTagMap.computeIfAbsent(BLOCK_TAG_MINEABLE_WITH_HOE, k -> new ArrayList<>()).add(leavesBlockEntry);

            TagLoader.EntryWithSource logBlockEntry = makeBlockTagEntry(resourceTree.getLog());
            tempBlockTagMap.computeIfAbsent(BLOCK_TAG_LOGS, k -> new ArrayList<>()).add(logBlockEntry);
            tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(logBlockEntry);

            if (STRIPPED_LOG_ENABLED) {
                TagLoader.EntryWithSource strippedLogBlockEntry = makeBlockTagEntry(resourceTree.getStrippedLog());
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_LOGS, k -> new ArrayList<>()).add(strippedLogBlockEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_STRIPPED_LOGS, k -> new ArrayList<>()).add(strippedLogBlockEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(strippedLogBlockEntry);
            }
            if (WOOD_ENABLED) {
                TagLoader.EntryWithSource woodBlockEntry = makeBlockTagEntry(resourceTree.getWood());
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_LOGS, k -> new ArrayList<>()).add(woodBlockEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(woodBlockEntry);
            }
            if (STRIPPED_WOOD_ENABLED) {
                TagLoader.EntryWithSource strippedWoodBlockEntry = makeBlockTagEntry(resourceTree.getStrippedWood());
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_LOGS, k -> new ArrayList<>()).add(strippedWoodBlockEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_STRIPPED_WOODS, k -> new ArrayList<>()).add(strippedWoodBlockEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(strippedWoodBlockEntry);
            }
            if (PLANKS_ENABLED) {
                TagLoader.EntryWithSource planksBlockEntry = makeBlockTagEntry(resourceTree.getPlanks());
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_PLANKS, k -> new ArrayList<>()).add(planksBlockEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_RESOURCE_PLANKS, k -> new ArrayList<>()).add(planksBlockEntry);
                tempBlockTagMap.computeIfAbsent(BLOCK_TAG_MINEABLE_WITH_AXE, k -> new ArrayList<>()).add(planksBlockEntry);
            }
        });
    }

    // === ItemTags 缓存 ===
    private static final ResourceLocation ITEM_TAG_SAPLINGS = ItemTags.SAPLINGS.location();
    private static final ResourceLocation ITEM_TAG_LEAVES = ItemTags.LEAVES.location();
    private static final ResourceLocation ITEM_TAG_LOGS_THAT_BURN = ItemTags.LOGS_THAT_BURN.location();
    private static final ResourceLocation ITEM_TAG_STRIPPED_LOGS = RLUtils.parse("c:stripped_logs");
    private static final ResourceLocation ITEM_TAG_STRIPPED_WOODS = RLUtils.parse("c:stripped_woods");
    private static final ResourceLocation ITEM_TAG_PLANKS = ItemTags.PLANKS.location();

    private static final ResourceLocation ITEM_TAG_RESOURCE_SAPLING = ResourceFarmItemTags.RESOURCE_SAPLING.location();
    private static final ResourceLocation ITEM_TAG_RESOURCE_LEAVES = ResourceFarmItemTags.RESOURCE_LEAVES.location();
    private static final ResourceLocation ITEM_TAG_RESOURCE_LOG = ResourceFarmItemTags.RESOURCE_LOG.location();
    private static final ResourceLocation ITEM_TAG_RESOURCE_PLANKS = ResourceFarmItemTags.RESOURCE_PLANKS.location();
    private static final ResourceLocation ITEM_TAG_RESOURCE_RESIN = ResourceFarmItemTags.RESOURCE_RESIN.location();
    private static final ResourceLocation ITEM_TAG_RESOURCE_FRUIT = ResourceFarmItemTags.RESOURCE_FRUIT.location();
    private static final ResourceLocation ITEM_TAG_RESOURCE_CLUMP = ResourceFarmItemTags.RESOURCE_CLUMP.location();

    public static void generateItemTags(Object2ObjectOpenHashMap<ResourceLocation, List<TagLoader.EntryWithSource>> tempItemTagMap) {
        final boolean STRIPPED_LOG_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog;
        final boolean WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood;
        final boolean STRIPPED_WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood;
        final boolean PLANKS_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks;
        final boolean CLUMP_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateClump();
        // 遍历ResourceTree，条目收集
        ResourceFarmBlocks.ResourceTreeKeyList.forEach(treeId -> {
            ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
            if (resourceTree == null) return;

            TagLoader.EntryWithSource saplingItemEntry = makeItemTagEntry(resourceTree.getSapling());
            tempItemTagMap.computeIfAbsent(ITEM_TAG_SAPLINGS, k -> new ArrayList<>()).add(saplingItemEntry);
            tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_SAPLING, k -> new ArrayList<>()).add(saplingItemEntry);

            TagLoader.EntryWithSource leavesItemEntry = makeItemTagEntry(resourceTree.getLeaves());
            tempItemTagMap.computeIfAbsent(ITEM_TAG_LEAVES, k -> new ArrayList<>()).add(leavesItemEntry);
            tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_LEAVES, k -> new ArrayList<>()).add(leavesItemEntry);

            TagLoader.EntryWithSource logItemEntry = makeItemTagEntry(resourceTree.getLog());
            tempItemTagMap.computeIfAbsent(ITEM_TAG_LOGS_THAT_BURN, k -> new ArrayList<>()).add(logItemEntry);
            tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(logItemEntry);

            if (STRIPPED_LOG_ENABLED) {
                TagLoader.EntryWithSource strippedLogItemEntry = makeItemTagEntry(resourceTree.getStrippedLog());
                tempItemTagMap.computeIfAbsent(ITEM_TAG_LOGS_THAT_BURN, k -> new ArrayList<>()).add(strippedLogItemEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_STRIPPED_LOGS, k -> new ArrayList<>()).add(strippedLogItemEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(strippedLogItemEntry);
            }
            if (WOOD_ENABLED) {
                TagLoader.EntryWithSource woodItemEntry = makeItemTagEntry(resourceTree.getWood());
                tempItemTagMap.computeIfAbsent(ITEM_TAG_LOGS_THAT_BURN, k -> new ArrayList<>()).add(woodItemEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(woodItemEntry);
            }
            if (STRIPPED_WOOD_ENABLED) {
                TagLoader.EntryWithSource strippedWoodItemEntry = makeItemTagEntry(resourceTree.getStrippedWood());
                tempItemTagMap.computeIfAbsent(ITEM_TAG_LOGS_THAT_BURN, k -> new ArrayList<>()).add(strippedWoodItemEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_STRIPPED_WOODS, k -> new ArrayList<>()).add(strippedWoodItemEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_LOG, k -> new ArrayList<>()).add(strippedWoodItemEntry);

            }
            if (PLANKS_ENABLED) {
                TagLoader.EntryWithSource planksItemEntry = makeItemTagEntry(resourceTree.getPlanks());
                tempItemTagMap.computeIfAbsent(ITEM_TAG_PLANKS, k -> new ArrayList<>()).add(planksItemEntry);
                tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_PLANKS, k -> new ArrayList<>()).add(planksItemEntry);
            }
            TagLoader.EntryWithSource resinItemEntry = makeItemTagEntry(resourceTree.getResin());
            tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_RESIN, k -> new ArrayList<>()).add(resinItemEntry);

            TagLoader.EntryWithSource fruitItemEntry = makeItemTagEntry(resourceTree.getFruit());
            tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_FRUIT, k -> new ArrayList<>()).add(fruitItemEntry);

            if (CLUMP_ENABLED) {
                TagLoader.EntryWithSource clumpItemEntry = makeItemTagEntry(resourceTree.getClump());
                tempItemTagMap.computeIfAbsent(ITEM_TAG_RESOURCE_CLUMP, k -> new ArrayList<>()).add(clumpItemEntry);
            }
        });
    }
}
