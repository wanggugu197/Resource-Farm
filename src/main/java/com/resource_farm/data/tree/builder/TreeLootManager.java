package com.resource_farm.data.tree.builder;

import com.resource_farm.api.ItemWeightCountHolder;
import com.resource_farm.config.ConfigHolder;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.mixin.BlockBehaviourAccessor;
import com.resource_farm.utils.LootTableUtils;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;

public class TreeLootManager {

    public static void generateBlockLoot(TriConsumer<ResourceLocation, LootTable, RegistryAccess.Frozen> lootTables,
                                         final RegistryAccess.Frozen access) {
        final VanillaBlockLoot blockLoot = new VanillaBlockLoot(access);

        Holder<Enchantment> fortune = access.registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.FORTUNE);
        ResourceFarmBlocks.ResourceTreeMap.forEach((a, resourceTree) -> {
            if (resourceTree == null) return;
            BlockEntry<?> saplingEntry = resourceTree.getSapling();
            if (saplingEntry != null) {
                ResourceLocation saplingId = getId(saplingEntry);
                lootTables.accept(saplingId, LootTableUtils.createSelf(saplingEntry.get().asItem()).build(), access);
                ((BlockBehaviourAccessor) saplingEntry.get()).setDrops(ResourceKey.create(Registries.LOOT_TABLE, saplingId));

                BlockEntry<?> leavesEntry = resourceTree.getLeaves();
                ItemEntry<?> resinEntry = resourceTree.getResin();
                ItemEntry<?> fruitEntry = resourceTree.getFruit();
                if (leavesEntry != null && resinEntry != null && fruitEntry != null) {
                    ResourceLocation leavesId = getId(leavesEntry);
                    lootTables.accept(leavesId, LootTableUtils.createLeavesStyleLootTable(leavesEntry.get().asItem(), saplingEntry.get().asItem(),
                            List.of(ItemWeightCountHolder.of(resinEntry.get().asItem(), 1, UniformGenerator.between(-1, 2)),
                                    ItemWeightCountHolder.of(fruitEntry.get().asItem(), 4, UniformGenerator.between(-1, 2))),
                            fortune).build(), access);
                    ((BlockBehaviourAccessor) leavesEntry.get()).setDrops(ResourceKey.create(Registries.LOOT_TABLE, leavesId));
                }
            }
            BlockEntry<?> logEntry = resourceTree.getLog();
            if (logEntry != null) {
                ResourceLocation logId = getId(logEntry);
                lootTables.accept(logId, LootTableUtils.createSelf(logEntry.get().asItem()).build(), access);
                ((BlockBehaviourAccessor) logEntry.get()).setDrops(ResourceKey.create(Registries.LOOT_TABLE, logId));
            }
            if (ConfigHolder.INSTANCE.tree.blockGeneration.generateStrippedLog) {
                BlockEntry<?> strippedLogEntry = resourceTree.getStrippedLog();
                if (strippedLogEntry != null) {
                    ResourceLocation strippedLogId = getId(strippedLogEntry);
                    lootTables.accept(strippedLogId, LootTableUtils.createSelf(strippedLogEntry.get().asItem()).build(), access);
                    ((BlockBehaviourAccessor) strippedLogEntry.get()).setDrops(ResourceKey.create(Registries.LOOT_TABLE, strippedLogId));
                }
            }
            if (ConfigHolder.INSTANCE.tree.blockGeneration.generateWood) {
                BlockEntry<?> woodEntry = resourceTree.getWood();
                if (woodEntry != null) {
                    ResourceLocation woodId = getId(woodEntry);
                    lootTables.accept(woodId, LootTableUtils.createSelf(woodEntry.get().asItem()).build(), access);
                    ((BlockBehaviourAccessor) woodEntry.get()).setDrops(ResourceKey.create(Registries.LOOT_TABLE, woodId));
                }
            }
            if (ConfigHolder.INSTANCE.tree.blockGeneration.generateStrippedWood) {
                BlockEntry<?> strippedWoodEntry = resourceTree.getStrippedWood();
                if (strippedWoodEntry != null) {
                    ResourceLocation strippedWoodId = getId(strippedWoodEntry);
                    lootTables.accept(strippedWoodId, LootTableUtils.createSelf(strippedWoodEntry.get().asItem()).build(), access);
                    ((BlockBehaviourAccessor) strippedWoodEntry.get()).setDrops(ResourceKey.create(Registries.LOOT_TABLE, strippedWoodId));
                }
            }
            if (ConfigHolder.INSTANCE.tree.blockGeneration.generatePlanks) {
                BlockEntry<?> planksEntry = resourceTree.getPlanks();
                if (planksEntry != null) {
                    ResourceLocation planksId = getId(planksEntry);
                    lootTables.accept(planksId, LootTableUtils.createSelf(planksEntry.get().asItem()).build(), access);
                    ((BlockBehaviourAccessor) planksEntry.get()).setDrops(ResourceKey.create(Registries.LOOT_TABLE, planksId));
                }
            }
        });
    }

    private static ResourceLocation getId(BlockEntry<?> blockEntry) {
        return blockEntry.getId().withPrefix("blocks/");
    }
}
