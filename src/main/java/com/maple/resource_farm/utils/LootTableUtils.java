package com.maple.resource_farm.utils;

import com.maple.resource_farm.api.ItemWeightCountHolder;

import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.annotation.Nonnull;

import static net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.addOreBonusCount;
import static net.minecraft.world.level.storage.loot.functions.SetItemCountFunction.setCount;
import static net.minecraft.world.level.storage.loot.providers.number.ConstantValue.exactly;

public class LootTableUtils {

    protected static final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(BuiltInRegistries.ITEM, Items.SHEARS));

    private static LootItemCondition.Builder hasSilkTouch(HolderLookup.Provider registries) {
        Holder<Enchantment> silkTouch = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
        return MatchTool.toolMatches(ItemPredicate.Builder.item()
                .withComponents(DataComponentMatchers.Builder.components()
                        .partial(
                                DataComponentPredicates.ENCHANTMENTS,
                                EnchantmentsPredicate.enchantments(
                                        List.of(new EnchantmentPredicate(silkTouch, MinMaxBounds.Ints.atLeast(1)))))
                        .build()));
    }

    private static LootItemCondition.Builder doesNotHaveSilkTouch(HolderLookup.Provider registries) {
        return hasSilkTouch(registries).invert();
    }

    /**
     * 创建设定数量的物品战利品条目
     */
    public static @NotNull LootItem.Builder<?> getLootItem(ItemWeightCountHolder holder) {
        return LootItem.lootTableItem(holder.item())
                .setWeight(holder.number())
                .apply(setCount(holder.countProvider()));
    }

    /**
     * 「掉落自身方块」的战利品表
     */
    public static @Nonnull LootTable.Builder createSelf(ItemLike block) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(block)
                                .apply(ApplyExplosionDecay.explosionDecay())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))))
                .setParamSet(LootContextParamSets.BLOCK);
    }

    /**
     * 多物品自定义权重（概率）/数量的战利品池
     */
    public static @NotNull LootPool.Builder createMultiItemWeightedPool(
                                                                        List<ItemWeightCountHolder> itemHolders,
                                                                        NumberProvider poolRolls) {
        LootPool.Builder lootPool = LootPool.lootPool().setRolls(poolRolls);
        for (ItemWeightCountHolder holder : itemHolders) lootPool.add(getLootItem(holder));
        return lootPool;
    }

    /**
     * 类矿石风格战利品表
     */
    public static @NotNull LootTable.Builder createOreStyleLootTable(
                                                                     ItemLike block,
                                                                     List<ItemWeightCountHolder> oreDropHolders,
                                                                     HolderLookup.Provider registries,
                                                                     Holder<Enchantment> fortune) {
        LootItemCondition.Builder noSilk = doesNotHaveSilkTouch(registries);

        LootPool.Builder selfDropPool = LootPool.lootPool()
                .setRolls(exactly(1))
                .add(LootItem.lootTableItem(block)
                        .when(noSilk.invert())
                        .apply(setCount(exactly(1))));
        LootPool.Builder fortuneBonusPool = LootPool.lootPool()
                .setRolls(exactly(1));
        for (ItemWeightCountHolder holder : oreDropHolders) {
            fortuneBonusPool.add(LootItem.lootTableItem(holder.item())
                    .when(noSilk)
                    .setWeight(holder.number())
                    .apply(setCount(holder.countProvider()))
                    .apply(addOreBonusCount(fortune)));
        }
        return LootTable.lootTable()
                .withPool(selfDropPool)
                .withPool(fortuneBonusPool)
                .setParamSet(LootContextParamSets.BLOCK);
    }

    /**
     * 树叶风格战利品表
     */
    public static @NotNull LootTable.Builder createLeavesStyleLootTable(
                                                                        ItemLike block,
                                                                        ItemLike spring,
                                                                        List<ItemWeightCountHolder> leafDropHolders,
                                                                        Holder<Enchantment> fortune) {
        LootPool.Builder normalDropPool = LootPool.lootPool()
                .when(HAS_SHEARS.invert())
                .apply(addOreBonusCount(fortune));
        for (ItemWeightCountHolder holder : leafDropHolders) {
            normalDropPool.add(LootItem.lootTableItem(holder.item())
                    .setWeight(holder.number())
                    .apply(setCount(holder.countProvider())));
        }

        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(block)
                                .when(HAS_SHEARS)
                                .apply(setCount(exactly(1)))))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(spring)
                                .when(HAS_SHEARS.invert())
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(fortune, 0.05F, 0.0625F, 0.0833F, 0.1F))
                                .apply(setCount(exactly(1)))))
                .withPool(normalDropPool)
                .setParamSet(LootContextParamSets.BLOCK);
    }
}
