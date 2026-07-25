package com.maple.resource_farm.data.tree.builder;

import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.data.ResourceFarmBlocks;
import com.maple.resource_farm.data.misc.ResourceFarmBlockTags;
import com.maple.resource_farm.data.misc.ResourceFarmItemTags;
import com.maple.resource_farm.data.tree.ResourceTree;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;

import com.gto.registrylib.util.entry.BlockEntry;
import com.gto.registrylib.util.entry.ItemEntry;
import com.mapleutillib.utils.RLUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.List;
import java.util.Map;

import static com.maple.resource_farm.common.Manager.ResourceFarmTagManager.addMember;

/**
 * 资源树 Tag 成员收集。
 * <p>
 * 输出为 {@code tagId → [elementId, ...]}，由 {@link com.maple.resource_farm.common.Manager.ResourceFarmTagManager}
 * 在 {@code TagLoader#build} 之后解析为 {@link net.minecraft.core.Holder} 并合并进结果 Map。
 */
public class TreeTagInsert {

    // === BlockTags ===
    private static final Identifier BLOCK_TAG_SAPLINGS = BlockTags.SAPLINGS.location();
    private static final Identifier BLOCK_TAG_LEAVES = BlockTags.LEAVES.location();
    private static final Identifier BLOCK_TAG_MINEABLE_WITH_HOE = BlockTags.MINEABLE_WITH_HOE.location();
    private static final Identifier BLOCK_TAG_LOGS = BlockTags.LOGS.location();
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
        final boolean STRIPPED_LOG_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog;
        final boolean WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood;
        final boolean STRIPPED_WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood;
        final boolean PLANKS_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks;

        ResourceFarmBlocks.ResourceTreeKeyList.forEach(treeId -> {
            ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
            if (resourceTree == null) return;

            Identifier sapling = id(resourceTree.getSapling());
            addMember(map, BLOCK_TAG_SAPLINGS, sapling);
            addMember(map, BLOCK_TAG_RESOURCE_SAPLING, sapling);

            Identifier leaves = id(resourceTree.getLeaves());
            addMember(map, BLOCK_TAG_LEAVES, leaves);
            addMember(map, BLOCK_TAG_RESOURCE_LEAVES, leaves);
            addMember(map, BLOCK_TAG_MINEABLE_WITH_HOE, leaves);

            Identifier log = id(resourceTree.getLog());
            addMember(map, BLOCK_TAG_LOGS, log);
            addMember(map, BLOCK_TAG_RESOURCE_LOG, log);

            if (STRIPPED_LOG_ENABLED) {
                Identifier strippedLog = id(resourceTree.getStrippedLog());
                addMember(map, BLOCK_TAG_LOGS, strippedLog);
                addMember(map, BLOCK_TAG_STRIPPED_LOGS, strippedLog);
                addMember(map, BLOCK_TAG_RESOURCE_LOG, strippedLog);
            }
            if (WOOD_ENABLED) {
                Identifier wood = id(resourceTree.getWood());
                addMember(map, BLOCK_TAG_LOGS, wood);
                addMember(map, BLOCK_TAG_RESOURCE_LOG, wood);
            }
            if (STRIPPED_WOOD_ENABLED) {
                Identifier strippedWood = id(resourceTree.getStrippedWood());
                addMember(map, BLOCK_TAG_LOGS, strippedWood);
                addMember(map, BLOCK_TAG_STRIPPED_WOODS, strippedWood);
                addMember(map, BLOCK_TAG_RESOURCE_LOG, strippedWood);
            }
            if (PLANKS_ENABLED) {
                Identifier planks = id(resourceTree.getPlanks());
                addMember(map, BLOCK_TAG_PLANKS, planks);
                addMember(map, BLOCK_TAG_RESOURCE_PLANKS, planks);
                addMember(map, BLOCK_TAG_MINEABLE_WITH_AXE, planks);
            }
        });
        return map;
    }

    public static Map<Identifier, List<Identifier>> collectItemTagMembers() {
        Object2ObjectOpenHashMap<Identifier, List<Identifier>> map = new Object2ObjectOpenHashMap<>();
        final boolean STRIPPED_LOG_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog;
        final boolean WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood;
        final boolean STRIPPED_WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood;
        final boolean PLANKS_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks;
        final boolean CLUMP_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateClump();

        ResourceFarmBlocks.ResourceTreeKeyList.forEach(treeId -> {
            ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
            if (resourceTree == null) return;

            // BlockItem 与方块同 id
            Identifier sapling = id(resourceTree.getSapling());
            addMember(map, ITEM_TAG_SAPLINGS, sapling);
            addMember(map, ITEM_TAG_RESOURCE_SAPLING, sapling);

            Identifier leaves = id(resourceTree.getLeaves());
            addMember(map, ITEM_TAG_LEAVES, leaves);
            addMember(map, ITEM_TAG_RESOURCE_LEAVES, leaves);

            Identifier log = id(resourceTree.getLog());
            addMember(map, ITEM_TAG_LOGS_THAT_BURN, log);
            addMember(map, ITEM_TAG_RESOURCE_LOG, log);

            if (STRIPPED_LOG_ENABLED) {
                Identifier strippedLog = id(resourceTree.getStrippedLog());
                addMember(map, ITEM_TAG_LOGS_THAT_BURN, strippedLog);
                addMember(map, ITEM_TAG_STRIPPED_LOGS, strippedLog);
                addMember(map, ITEM_TAG_RESOURCE_LOG, strippedLog);
            }
            if (WOOD_ENABLED) {
                Identifier wood = id(resourceTree.getWood());
                addMember(map, ITEM_TAG_LOGS_THAT_BURN, wood);
                addMember(map, ITEM_TAG_RESOURCE_LOG, wood);
            }
            if (STRIPPED_WOOD_ENABLED) {
                Identifier strippedWood = id(resourceTree.getStrippedWood());
                addMember(map, ITEM_TAG_LOGS_THAT_BURN, strippedWood);
                addMember(map, ITEM_TAG_STRIPPED_WOODS, strippedWood);
                addMember(map, ITEM_TAG_RESOURCE_LOG, strippedWood);
            }
            if (PLANKS_ENABLED) {
                Identifier planks = id(resourceTree.getPlanks());
                addMember(map, ITEM_TAG_PLANKS, planks);
                addMember(map, ITEM_TAG_RESOURCE_PLANKS, planks);
            }

            addMember(map, ITEM_TAG_RESOURCE_RESIN, id(resourceTree.getResin()));
            addMember(map, ITEM_TAG_RESOURCE_FRUIT, id(resourceTree.getFruit()));
            if (CLUMP_ENABLED) {
                addMember(map, ITEM_TAG_RESOURCE_CLUMP, id(resourceTree.getClump()));
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
