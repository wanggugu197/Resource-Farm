package com.maple.resource_farm.plantPot.block;

import com.maple.resource_farm.plantPot.ResourcePlantPotRegister;
import com.maple.resource_farm.utils.DelegatingResourceHandlers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.mapleutillib.api.resource.ObservableItemResourceHandler;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class HoppingBonsaiPotBlockEntity extends BonsaiPotBlockEntity {

    // ========== 常量 ==========
    private static final int FERTILIZER_SLOT = 2;
    private static final int OUTPUT_SLOT_START = 3;
    private static final int OUTPUT_SLOT_END = 17;
    private static final int OUTPUT_SLOT_COUNT = OUTPUT_SLOT_END - OUTPUT_SLOT_START + 1; // 15

    // ---------- 存储 ----------
    @Persisted(subPersisted = true)
    @DescSynced
    public final ObservableItemResourceHandler fertilizerInventory = new ObservableItemResourceHandler(1);

    @Persisted(subPersisted = true)
    @DescSynced
    public final ObservableItemResourceHandler outputInventory = new ObservableItemResourceHandler(OUTPUT_SLOT_COUNT);

    @Setter
    @Getter
    @Persisted
    @DescSynced
    private int clochedTier = 0;

    @Getter
    private final ResourceHandler<ItemResource> itemCapability;

    // ========== 构造 ==========
    public HoppingBonsaiPotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.itemCapability = new CombinedResourceHandler<>(
                new DelegatingResourceHandlers.InsertOnly<>(fertilizerInventory),
                new DelegatingResourceHandlers.ExtractOnly<>(outputInventory));
    }

    // ============================================================
    // 库存访问
    // ============================================================
    @Override
    public ItemStack getStack(int slot) {
        return switch (slot) {
            case 0, 1 -> super.getStack(slot);
            case FERTILIZER_SLOT -> {
                ItemResource res = fertilizerInventory.getResource(0);
                yield res.isEmpty() ? ItemStack.EMPTY : res.toStack(fertilizerInventory.getAmountAsInt(0));
            }
            default -> {
                if (slot >= OUTPUT_SLOT_START && slot <= OUTPUT_SLOT_END) {
                    int idx = slot - OUTPUT_SLOT_START;
                    ItemResource res = outputInventory.getResource(idx);
                    yield res.isEmpty() ? ItemStack.EMPTY : res.toStack(outputInventory.getAmountAsInt(idx));
                }
                yield ItemStack.EMPTY;
            }
        };
    }

    // ============================================================
    // 收获逻辑
    // ============================================================
    @Override
    public void harvestPlant(Level level, BlockPos pos) {
        if (!isReadyToHarvest()) return;

        ItemStack seed = getStack(0);
        float fertYield = getFertilizerYieldModifier();
        float clocheYield = getClocheYieldModifier();
        List<ItemStack> drops = applyYieldModifier(getHarvestDrops(seed), fertYield * clocheYield);

        for (ItemStack drop : drops) {
            int remaining = drop.getCount();
            ItemResource resource = ItemResource.of(drop);

            // 1. 尝试插入已有同种物品的槽位
            remaining = tryInsertIntoExistingSlots(resource, drop.getItem(), remaining);

            // 2. 尝试插入空槽位
            remaining = tryInsertIntoEmptySlots(resource, drop, remaining);

            // 3. 剩余部分掉落为物品实体
            if (remaining > 0) {
                spawnItemEntity(level, pos, drop.copyWithCount(remaining));
            }
        }

        consumeFertilizer();
        resetGrowth();
        sync();
    }

    /**
     * 尝试将剩余物品插入已有同种物品的槽位（优先填满已有堆）
     */
    private int tryInsertIntoExistingSlots(ItemResource resource, Item item, int amount) {
        int remaining = amount;
        for (int slot = OUTPUT_SLOT_START; slot <= OUTPUT_SLOT_END && remaining > 0; slot++) {
            ItemStack existing = getStack(slot);
            if (existing.isEmpty() || !existing.is(item)) continue;

            int space = existing.getMaxStackSize() - existing.getCount();
            if (space <= 0) continue;

            int toAdd = Math.min(space, remaining);
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = outputInventory.insert(slot - OUTPUT_SLOT_START, resource, toAdd, tx);
                tx.commit();
                remaining -= inserted;
            }
        }
        return remaining;
    }

    /**
     * 尝试将剩余物品插入第一个空槽位
     */
    private int tryInsertIntoEmptySlots(ItemResource resource, ItemStack drop, int amount) {
        int remaining = amount;
        for (int slot = OUTPUT_SLOT_START; slot <= OUTPUT_SLOT_END && remaining > 0; slot++) {
            if (!getStack(slot).isEmpty()) continue;

            int toPlace = Math.min(remaining, drop.getMaxStackSize());
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = outputInventory.insert(slot - OUTPUT_SLOT_START, resource, toPlace, tx);
                tx.commit();
                remaining -= inserted;
            }
        }
        return remaining;
    }

    private void spawnItemEntity(Level level, BlockPos pos, ItemStack stack) {
        double x = pos.getX() + 0.25 + level.getRandom().nextDouble() * 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.25 + level.getRandom().nextDouble() * 0.5;
        ItemEntity entity = new ItemEntity(level, x, y, z, stack);
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
    }

    // ============================================================
    // 修饰符计算
    // ============================================================
    @Override
    protected float calculateGrowthModifier(ItemStack soilStack) {
        return getClocheGrowthModifier() * super.calculateGrowthModifier(soilStack);
    }

    private float getFertilizerYieldModifier() {
        ItemStack stack = getStack(FERTILIZER_SLOT);
        if (stack.isEmpty()) return 1.0F;
        var data = stack.typeHolder().getData(ResourcePlantPotRegister.FERTILIZERS);
        return data != null ? data.yieldMultiplier() : 1.0F;
    }

    public float getClocheGrowthModifier() {
        return ClocheHelper.speedModifierMap.getOrDefault(clochedTier, 1.0f);
    }

    public float getClocheYieldModifier() {
        return ClocheHelper.yieldModifierMap.getOrDefault(clochedTier, 1.0f);
    }

    private List<ItemStack> applyYieldModifier(List<ItemStack> drops, float mod) {
        if (mod == 1.0F) return drops;
        List<ItemStack> out = new ArrayList<>();
        for (ItemStack drop : drops) {
            int newCount = Math.max(1, Math.round(drop.getCount() * mod));
            out.add(drop.copyWithCount(newCount));
        }
        return out;
    }

    private void consumeFertilizer() {
        ItemStack stack = getStack(FERTILIZER_SLOT);
        if (stack.isEmpty()) return;
        try (Transaction tx = Transaction.openRoot()) {
            fertilizerInventory.extract(0, ItemResource.of(stack), 1, tx);
            tx.commit();
        }
        setChanged();
    }

    // ============================================================
    // 空间检查
    // ============================================================
    public boolean hasOutputSpace() {
        List<ItemStack> drops = getHarvestDrops(getStack(0));
        if (drops.isEmpty()) return true;
        for (int slot = OUTPUT_SLOT_START; slot <= OUTPUT_SLOT_END; slot++) {
            if (getStack(slot).isEmpty()) return true;
        }
        for (ItemStack drop : drops) {
            boolean canFit = false;
            for (int slot = OUTPUT_SLOT_START; slot <= OUTPUT_SLOT_END; slot++) {
                ItemStack existing = getStack(slot);
                if (!existing.isEmpty() && existing.is(drop.getItem()) && existing.getCount() < existing.getMaxStackSize()) {
                    canFit = true;
                    break;
                }
            }
            if (!canFit) {
                return false;
            }
        }
        return true;
    }

    // ============================================================
    // 输出到下方容器
    // ============================================================
    private void tryOutputItemsBelow(Level level, BlockPos pos) {
        ResourceHandler<ItemResource> target = level.getCapability(Capabilities.Item.BLOCK, pos.below(), Direction.UP);
        if (target == null) return;

        boolean changed = false;
        for (int slot = 0; slot < OUTPUT_SLOT_COUNT; slot++) {
            ItemResource res = outputInventory.getResource(slot);
            if (res.isEmpty()) continue;
            int available = outputInventory.getAmountAsInt(slot);
            if (available <= 0) continue;

            try (Transaction tx = Transaction.openRoot()) {
                int insertable = target.insert(res, available, tx);
                if (insertable <= 0) continue;
                int extracted = outputInventory.extract(slot, res, insertable, tx);
                if (extracted != insertable) continue;
                tx.commit();
                changed = true;
            }
        }

        if (changed) {
            sync();
        }
    }

    // ============================================================
    // Tick
    // ============================================================
    public static void tick(Level level, BlockPos pos, HoppingBonsaiPotBlockEntity be) {
        if (level.isClientSide()) return;

        // 1. 父类生长逻辑
        BonsaiPotBlockEntity.tick(level, be);

        // 2. 成熟且有空间 → 自动收获
        if (be.isReadyToHarvest() && be.hasOutputSpace()) {
            be.harvestPlant(level, pos);
        }

        // 3. 每30秒尝试向下输出一次
        if (level.getGameTime() % 600 == 0) {
            be.tryOutputItemsBelow(level, pos);
        }
    }

    // ============================================================
    // 掉落
    // ============================================================
    @Override
    public void preRemoveSideEffects(@NonNull BlockPos pos, @NonNull BlockState state) {
        if (level == null) return;
        // 掉落玻璃罩
        ItemStack cloche = ClocheHelper.getClocheItem(clochedTier);
        if (!cloche.isEmpty())
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, cloche));
        drops();
    }

    @Override
    public void drops() {
        super.drops();
        // 掉落肥料槽
        if (level != null) {
            SimpleContainer fertInv = new SimpleContainer(fertilizerInventory.size());
            for (int i = 0; i < fertilizerInventory.size(); i++) {
                ItemResource res = fertilizerInventory.getResource(i);
                if (!res.isEmpty()) {
                    fertInv.setItem(i, res.toStack(fertilizerInventory.getAmountAsInt(i)));
                }
            }
            Containers.dropContents(level, worldPosition, fertInv);
            // 掉落输出槽
            SimpleContainer outInv = new SimpleContainer(outputInventory.size());
            for (int i = 0; i < outputInventory.size(); i++) {
                ItemResource res = outputInventory.getResource(i);
                if (!res.isEmpty()) {
                    outInv.setItem(i, res.toStack(outputInventory.getAmountAsInt(i)));
                }
            }
            Containers.dropContents(level, worldPosition, outInv);
        }
    }

    // 辅助：同步更新
    private void sync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }
}
