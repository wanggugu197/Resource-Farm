package com.maple.resource_farm.resourceTree.builder;

import com.maple.resource_farm.api.ItemWeightCountHolder;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.mixin.BlockBehaviourAccessor;
import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.utils.LootTableUtils;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import com.gto.registrylib.util.entry.BlockEntry;
import com.gto.registrylib.util.entry.ItemEntry;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class TreeLootInsert {

    private static void setBlockDrops(BlockEntry<?> blockEntry, Identifier lootTableId) {
        ((BlockBehaviourAccessor) blockEntry.get()).setDrops(
                Optional.of(ResourceKey.create(Registries.LOOT_TABLE, lootTableId)));
    }

    /**
     * @param lootTables 接收 (lootTableId, table)；对象直注，无 JSON
     */
    public static void generateBlockLoot(BiConsumer<Identifier, LootTable> lootTables,
                                         final RegistryAccess.Frozen access) {
        Holder<Enchantment> fortune = access.lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.FORTUNE);

        final boolean STRIPPED_LOG_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedLog;
        final boolean WOOD_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateWood;
        final boolean STRIPPED_WOOD_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedWood;
        final boolean PLANKS_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generatePlanks;

        ResourceTreeAccessManagement.ResourceTreeMap.forEach((a, resourceTree) -> {
            if (resourceTree == null) return;
            BlockEntry<?> saplingEntry = resourceTree.getSapling();
            if (saplingEntry != null) {
                Identifier saplingId = getId(saplingEntry);
                lootTables.accept(saplingId, LootTableUtils.createSelf(saplingEntry.get().asItem()).build());
                setBlockDrops(saplingEntry, saplingId);

                BlockEntry<?> leavesEntry = resourceTree.getLeaves();
                ItemEntry<?> resinEntry = resourceTree.getResin();
                ItemEntry<?> fruitEntry = resourceTree.getFruit();
                if (leavesEntry != null && resinEntry != null && fruitEntry != null) {
                    Identifier leavesId = getId(leavesEntry);
                    lootTables.accept(leavesId, LootTableUtils.createLeavesStyleLootTable(leavesEntry.get().asItem(), saplingEntry.get().asItem(),
                            List.of(ItemWeightCountHolder.of(resinEntry.get().asItem(), 1, UniformGenerator.between(-1, 1)),
                                    ItemWeightCountHolder.of(fruitEntry.get().asItem(), 4, UniformGenerator.between(-1, 1))),
                            fortune).build());
                    setBlockDrops(leavesEntry, leavesId);
                }
            }
            BlockEntry<?> logEntry = resourceTree.getLog();
            if (logEntry != null) {
                Identifier logId = getId(logEntry);
                lootTables.accept(logId, LootTableUtils.createSelf(logEntry.get().asItem()).build());
                setBlockDrops(logEntry, logId);
            }
            if (STRIPPED_LOG_ENABLED) {
                BlockEntry<?> strippedLogEntry = resourceTree.getStrippedLog();
                if (strippedLogEntry != null) {
                    Identifier strippedLogId = getId(strippedLogEntry);
                    lootTables.accept(strippedLogId, LootTableUtils.createSelf(strippedLogEntry.get().asItem()).build());
                    setBlockDrops(strippedLogEntry, strippedLogId);
                }
            }
            if (WOOD_ENABLED) {
                BlockEntry<?> woodEntry = resourceTree.getWood();
                if (woodEntry != null) {
                    Identifier woodId = getId(woodEntry);
                    lootTables.accept(woodId, LootTableUtils.createSelf(woodEntry.get().asItem()).build());
                    setBlockDrops(woodEntry, woodId);
                }
            }
            if (STRIPPED_WOOD_ENABLED) {
                BlockEntry<?> strippedWoodEntry = resourceTree.getStrippedWood();
                if (strippedWoodEntry != null) {
                    Identifier strippedWoodId = getId(strippedWoodEntry);
                    lootTables.accept(strippedWoodId, LootTableUtils.createSelf(strippedWoodEntry.get().asItem()).build());
                    setBlockDrops(strippedWoodEntry, strippedWoodId);
                }
            }
            if (PLANKS_ENABLED) {
                BlockEntry<?> planksEntry = resourceTree.getPlanks();
                if (planksEntry != null) {
                    Identifier planksId = getId(planksEntry);
                    lootTables.accept(planksId, LootTableUtils.createSelf(planksEntry.get().asItem()).build());
                    setBlockDrops(planksEntry, planksId);
                }
            }
        });
    }

    private static Identifier getId(BlockEntry<?> blockEntry) {
        return blockEntry.identifier().withPrefix("blocks/");
    }
}
