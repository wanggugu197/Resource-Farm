package com.resource_farm.utils;

import com.resource_farm.api.ItemWeightCountHolder;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
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

    protected static final LootItemCondition.Builder HAS_SHEARS;

    static {
        HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
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
        LootItem.Builder<?> selfItemEntry = LootItem.lootTableItem(block)
                .apply(ApplyExplosionDecay.explosionDecay())
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)));

        LootPool.Builder selfPool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .setBonusRolls(ConstantValue.exactly(0.0F))
                .add(selfItemEntry);

        return LootTable.lootTable()
                .withPool(selfPool)
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
                                                                     VanillaBlockLoot blockLoot,
                                                                     Holder<Enchantment> fortune) {
        // 构建「精准采集」池
        LootPool.Builder selfDropPool = LootPool.lootPool()
                .setRolls(exactly(1))
                .add(LootItem.lootTableItem(block)
                        .when(blockLoot.doesNotHaveSilkTouch().invert())
                        .apply(setCount(exactly(1))));
        // 构建「无精准采集带时运加成」的多物品池
        LootPool.Builder fortuneBonusPool = LootPool.lootPool()
                .setRolls(exactly(1));
        for (ItemWeightCountHolder holder : oreDropHolders) {
            fortuneBonusPool.add(LootItem.lootTableItem(holder.item())
                    .when(blockLoot.doesNotHaveSilkTouch())
                    .setWeight(holder.number())
                    .apply(setCount(holder.countProvider()))
                    .apply(addOreBonusCount(fortune)));
        }
        return LootTable.lootTable()
                .withPool(selfDropPool) // 自身掉落池（精准采集生效）
                .withPool(fortuneBonusPool) // 时运加成池（无精准采集生效）
                .setParamSet(LootContextParamSets.BLOCK); // 触发上下文：方块被破坏
    }

    /**
     * 树叶风格战利品表
     */
    public static @NotNull LootTable.Builder createLeavesStyleLootTable(
                                                                        ItemLike block,
                                                                        ItemLike spring,
                                                                        List<ItemWeightCountHolder> leafDropHolders,
                                                                        Holder<Enchantment> fortune) {
        // 构建「剪刀掉落自身」的池
        LootPool.Builder selfDropPool = LootPool.lootPool()
                .setRolls(exactly(1))
                .add(LootItem.lootTableItem(block)
                        .when(HAS_SHEARS)
                        .apply(setCount(exactly(1))));

        // 构建「树苗掉落」的池
        LootPool.Builder springDropPool = LootPool.lootPool()
                .setRolls(exactly(1))
                .add(LootItem.lootTableItem(spring)
                        .when(HAS_SHEARS.invert())
                        .when(BonusLevelTableCondition.bonusLevelFlatChance(fortune, 0.05F, 0.0625F, 0.0833F, 0.1F))
                        .apply(setCount(exactly(1))));

        // 构建「额外物品的」的多物品权重池
        LootPool.Builder normalDropPool = LootPool.lootPool()
                .setRolls(exactly(1))
                .when(HAS_SHEARS.invert())
                .apply(addOreBonusCount(fortune));
        // 遍历添加每个掉落物品条目（按权重分配概率）
        for (ItemWeightCountHolder holder : leafDropHolders) {
            normalDropPool.add(LootItem.lootTableItem(holder.item())
                    .setWeight(holder.number())
                    .apply(setCount(holder.countProvider())));
        }

        return LootTable.lootTable()
                .withPool(selfDropPool) // 自身掉落池（剪刀生效）
                .withPool(springDropPool) // 树苗掉落池（非剪刀生效）
                .withPool(normalDropPool) // 普通掉落池（非剪刀生效）
                .setParamSet(LootContextParamSets.BLOCK); // 触发上下文：方块被破坏
    }
}
