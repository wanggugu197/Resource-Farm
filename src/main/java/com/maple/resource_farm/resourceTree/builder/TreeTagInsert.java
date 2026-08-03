package com.maple.resource_farm.resourceTree.builder;

import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.data.misc.ResourceFarmBlockTags;
import com.maple.resource_farm.data.misc.ResourceFarmItemTags;
import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;

import com.gto.registrylib.util.entry.BlockEntry;
import com.gto.registrylib.util.entry.ItemEntry;
import com.mapleutillib.utils.RLUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.List;
import java.util.Map;

import static com.mapleutillib.api.dynamicdata.DynamicDataManager.addTagMember;

/**
 * 资源树 Tag 成员收集。
 */
public class TreeTagInsert {

    // === BlockTags ===
    private static final Identifier BLOCK_TAG_SAPLINGS = BlockTags.SAPLINGS.location();
    private static final Identifier BLOCK_TAG_LEAVES = BlockTags.LEAVES.location();
    private static final Identifier BLOCK_TAG_MINEABLE_WITH_HOE = BlockTags.MINEABLE_WITH_HOE.location();
    private static final Identifier BLOCK_TAG_LOGS = BlockTags.LOGS.location();
    private static final Identifier BLOCK_TAG_PREVENTS_NEARBY_LEAF_DECAY = BlockTags.PREVENTS_NEARBY_LEAF_DECAY.location();
    private static final Identifier BLOCK_TAG_STRIPPED_LOGS = RLUtils.parse("c:stripped_logs");
    private static final Identifier BLOCK_TAG_STRIPPED_WOODS = RLUtils.parse("c:stripped_woods");
    private static final Identifier BLOCK_TAG_PLANKS = BlockTags.PLANKS.location();
    private static final Identifier BLOCK_TAG_MINEABLE_WITH_AXE = BlockTags.MINEABLE_WITH_AXE.location();

    private static final Identifier BLOCK_TAG_RESOURCE_SAPLING = ResourceFarmBlockTags.RESOURCE_SAPLING.location();
    private static final Identifier BLOCK_TAG_RESOURCE_LEAVES = ResourceFarmBlockTags.RESOURCE_LEAVES.location();
    private static final Identifier BLOCK_TAG_RESOURCE_LOG = ResourceFarmBlockTags.RESOURCE_LOG.location();
    private static final Identifier BLOCK_TAG_RESOURCE_PLANKS = ResourceFarmBlockTags.RESOURCE_PLANKS.location();

    // === ItemTags ===
    private static final Identifier ITEM_TAG_SAPLINGS = ItemTags.SAPLINGS.location();
    private static final Identifier ITEM_TAG_LEAVES = ItemTags.LEAVES.location();
    private static final Identifier ITEM_TAG_LOGS_THAT_BURN = ItemTags.LOGS_THAT_BURN.location();
    private static final Identifier ITEM_TAG_STRIPPED_LOGS = RLUtils.parse("c:stripped_logs");
    private static final Identifier ITEM_TAG_STRIPPED_WOODS = RLUtils.parse("c:stripped_woods");
    private static final Identifier ITEM_TAG_PLANKS = ItemTags.PLANKS.location();

    private static final Identifier ITEM_TAG_RESOURCE_SAPLING = ResourceFarmItemTags.RESOURCE_SAPLING.location();
    private static final Identifier ITEM_TAG_RESOURCE_LEAVES = ResourceFarmItemTags.RESOURCE_LEAVES.location();
    private static final Identifier ITEM_TAG_RESOURCE_LOG = ResourceFarmItemTags.RESOURCE_LOG.location();
    private static final Identifier ITEM_TAG_RESOURCE_PLANKS = ResourceFarmItemTags.RESOURCE_PLANKS.location();
    private static final Identifier ITEM_TAG_RESOURCE_RESIN = ResourceFarmItemTags.RESOURCE_RESIN.location();
    private static final Identifier ITEM_TAG_RESOURCE_FRUIT = ResourceFarmItemTags.RESOURCE_FRUIT.location();
    private static final Identifier ITEM_TAG_RESOURCE_CLUMP = ResourceFarmItemTags.RESOURCE_CLUMP.location();

    public static Map<Identifier, List<Identifier>> collectBlockTagMembers() {
        Object2ObjectOpenHashMap<Identifier, List<Identifier>> map = new Object2ObjectOpenHashMap<>();
        final boolean STRIPPED_LOG_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedLog;
        final boolean WOOD_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateWood;
        final boolean STRIPPED_WOOD_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedWood;
        final boolean PLANKS_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generatePlanks;

        ResourceTreeAccessManagement.ResourceTreeMap.forEach((treeId, resourceTree) -> {
            if (resourceTree == null) return;

            Identifier sapling = id(resourceTree.getSapling());
            addTagMember(map, BLOCK_TAG_SAPLINGS, sapling);
            addTagMember(map, BLOCK_TAG_RESOURCE_SAPLING, sapling);

            Identifier leaves = id(resourceTree.getLeaves());
            addTagMember(map, BLOCK_TAG_LEAVES, leaves);
            addTagMember(map, BLOCK_TAG_RESOURCE_LEAVES, leaves);
            addTagMember(map, BLOCK_TAG_MINEABLE_WITH_HOE, leaves);

            Identifier log = id(resourceTree.getLog());
            addTagMember(map, BLOCK_TAG_LOGS, log);
            addTagMember(map, BLOCK_TAG_PREVENTS_NEARBY_LEAF_DECAY, log);
            addTagMember(map, BLOCK_TAG_RESOURCE_LOG, log);
            addTagMember(map, BLOCK_TAG_MINEABLE_WITH_AXE, log);

            if (STRIPPED_LOG_ENABLED) {
                Identifier strippedLog = id(resourceTree.getStrippedLog());
                addTagMember(map, BLOCK_TAG_LOGS, strippedLog);
                addTagMember(map, BLOCK_TAG_PREVENTS_NEARBY_LEAF_DECAY, strippedLog);
                addTagMember(map, BLOCK_TAG_STRIPPED_LOGS, strippedLog);
                addTagMember(map, BLOCK_TAG_RESOURCE_LOG, strippedLog);
                addTagMember(map, BLOCK_TAG_MINEABLE_WITH_AXE, strippedLog);
            }
            if (WOOD_ENABLED) {
                Identifier wood = id(resourceTree.getWood());
                addTagMember(map, BLOCK_TAG_LOGS, wood);
                addTagMember(map, BLOCK_TAG_PREVENTS_NEARBY_LEAF_DECAY, wood);
                addTagMember(map, BLOCK_TAG_RESOURCE_LOG, wood);
                addTagMember(map, BLOCK_TAG_MINEABLE_WITH_AXE, wood);
            }
            if (STRIPPED_WOOD_ENABLED) {
                Identifier strippedWood = id(resourceTree.getStrippedWood());
                addTagMember(map, BLOCK_TAG_LOGS, strippedWood);
                addTagMember(map, BLOCK_TAG_PREVENTS_NEARBY_LEAF_DECAY, strippedWood);
                addTagMember(map, BLOCK_TAG_STRIPPED_WOODS, strippedWood);
                addTagMember(map, BLOCK_TAG_RESOURCE_LOG, strippedWood);
                addTagMember(map, BLOCK_TAG_MINEABLE_WITH_AXE, strippedWood);
            }
            if (PLANKS_ENABLED) {
                Identifier planks = id(resourceTree.getPlanks());
                addTagMember(map, BLOCK_TAG_PLANKS, planks);
                addTagMember(map, BLOCK_TAG_RESOURCE_PLANKS, planks);
                addTagMember(map, BLOCK_TAG_MINEABLE_WITH_AXE, planks);
            }
        });
        return map;
    }

    public static Map<Identifier, List<Identifier>> collectItemTagMembers() {
        Object2ObjectOpenHashMap<Identifier, List<Identifier>> map = new Object2ObjectOpenHashMap<>();
        final boolean STRIPPED_LOG_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedLog;
        final boolean WOOD_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateWood;
        final boolean STRIPPED_WOOD_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedWood;
        final boolean PLANKS_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generatePlanks;
        final boolean CLUMP_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateClump();

        ResourceTreeAccessManagement.ResourceTreeMap.forEach((a, resourceTree) -> {
            if (resourceTree == null) return;

            // BlockItem 与方块同 id
            Identifier sapling = id(resourceTree.getSapling());
            addTagMember(map, ITEM_TAG_SAPLINGS, sapling);
            addTagMember(map, ITEM_TAG_RESOURCE_SAPLING, sapling);

            Identifier leaves = id(resourceTree.getLeaves());
            addTagMember(map, ITEM_TAG_LEAVES, leaves);
            addTagMember(map, ITEM_TAG_RESOURCE_LEAVES, leaves);

            Identifier log = id(resourceTree.getLog());
            addTagMember(map, ITEM_TAG_LOGS_THAT_BURN, log);
            addTagMember(map, ITEM_TAG_RESOURCE_LOG, log);

            if (STRIPPED_LOG_ENABLED) {
                Identifier strippedLog = id(resourceTree.getStrippedLog());
                addTagMember(map, ITEM_TAG_LOGS_THAT_BURN, strippedLog);
                addTagMember(map, ITEM_TAG_STRIPPED_LOGS, strippedLog);
                addTagMember(map, ITEM_TAG_RESOURCE_LOG, strippedLog);
            }
            if (WOOD_ENABLED) {
                Identifier wood = id(resourceTree.getWood());
                addTagMember(map, ITEM_TAG_LOGS_THAT_BURN, wood);
                addTagMember(map, ITEM_TAG_RESOURCE_LOG, wood);
            }
            if (STRIPPED_WOOD_ENABLED) {
                Identifier strippedWood = id(resourceTree.getStrippedWood());
                addTagMember(map, ITEM_TAG_LOGS_THAT_BURN, strippedWood);
                addTagMember(map, ITEM_TAG_STRIPPED_WOODS, strippedWood);
                addTagMember(map, ITEM_TAG_RESOURCE_LOG, strippedWood);
            }
            if (PLANKS_ENABLED) {
                Identifier planks = id(resourceTree.getPlanks());
                addTagMember(map, ITEM_TAG_PLANKS, planks);
                addTagMember(map, ITEM_TAG_RESOURCE_PLANKS, planks);
            }

            addTagMember(map, ITEM_TAG_RESOURCE_RESIN, id(resourceTree.getResin()));
            addTagMember(map, ITEM_TAG_RESOURCE_FRUIT, id(resourceTree.getFruit()));
            if (CLUMP_ENABLED) {
                addTagMember(map, ITEM_TAG_RESOURCE_CLUMP, id(resourceTree.getClump()));
            }
        });
        return map;
    }

    private static Identifier id(BlockEntry<?> entry) {
        return entry == null ? null : entry.identifier();
    }

    private static Identifier id(ItemEntry<?> entry) {
        return entry == null ? null : entry.identifier();
    }
}
