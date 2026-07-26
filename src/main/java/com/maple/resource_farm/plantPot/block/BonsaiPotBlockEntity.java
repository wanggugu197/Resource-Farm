package com.maple.resource_farm.plantPot.block;

import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.plantPot.ResourcePlantPotRegister;
import com.maple.resource_farm.plantPot.recipe.GrowthRecipe;
import com.maple.resource_farm.plantPot.recipe.OutputEntry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;

import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.mapleutillib.api.resource.ObservableItemResourceHandler;
import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.util.*;

import javax.annotation.Nullable;

public class BonsaiPotBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    // ========== 常量 ==========
    private static final int SEED_SLOT = 0;
    private static final int SOIL_SLOT = 1;
    private static final int BASE_FERTILIZER_BOOST = ResourceFarmConfigHolder.bonsaiPotConfigHolder.plantPot.baseFertilizerBoost;
    private static final float SKY_DAY_MODIFIER = ResourceFarmConfigHolder.bonsaiPotConfigHolder.plantPot.skyDayModifier;

    // ---------- 同步存储 ----------
    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    // ---------- 配方缓存 ----------
    private @Nullable GrowthRecipe cachedGrowthRecipe = null;
    private @Nullable Item cachedSeedItem = null;
    private @Nullable Set<Item> cachedValidSoils = null;
    private boolean soilCacheValid = false;

    // ---------- 库存 ----------
    @Persisted(subPersisted = true)
    @DescSynced
    public final ObservableItemResourceHandler inventory = new ObservableItemResourceHandler(2);

    // ---------- 生长状态 ----------
    @Getter
    @Persisted
    @DescSynced
    private int growthProgress = 0;

    @Persisted
    @DescSynced
    private int growthTicks = 0;

    @Getter
    @Persisted
    @DescSynced
    private boolean readyToHarvest = false;

    @Persisted
    @DescSynced
    private int lastGrowthStage = -1;

    @Getter
    @Persisted
    @DescSynced
    private boolean isCrop = false;

    // ---------- 构造 ----------
    public BonsaiPotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        inventory.setSlotMaxStackSize(SEED_SLOT, 1);
        inventory.setSlotMaxStackSize(SOIL_SLOT, 1);
    }

    // ============================================================
    // 库存访问
    // ============================================================
    public ItemStack getStack(int slot) {
        ItemResource res = inventory.getResource(slot);
        if (res.isEmpty()) return ItemStack.EMPTY;
        return res.toStack(inventory.getAmountAsInt(slot));
    }

    // ============================================================
    // 配方查询
    // ============================================================

    private void invalidateGrowthRecipeCache() {
        cachedGrowthRecipe = null;
        cachedSeedItem = null;
        isCrop = false;
    }

    private @Nullable RecipeManager getRecipeManager() {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.recipeAccess();
        }
        return null;
    }

    private void refreshRecipeCacheIfNeeded(ItemStack seed) {
        if (seed.isEmpty()) {
            invalidateGrowthRecipeCache();
            return;
        }

        Item seedItem = seed.getItem();

        // 种子没变且已有配方 → 直接返回
        if (seedItem == cachedSeedItem && cachedGrowthRecipe != null) {
            return;
        }

        // 种子相同但配方为 null → 可能之前没找到，但土壤变了？仍然尝试重新查找
        RecipeManager rm = getRecipeManager();
        if (rm == null || level == null) return;

        cachedSeedItem = seedItem;
        ItemStack soil = getStack(SOIL_SLOT);
        var recipeHolder = rm.recipeMap()
                .getRecipesFor(ResourcePlantPotRegister.GROWTH.get(), new SingleRecipeInput(seed), level)
                .toList()
                .stream()
                .filter(holder -> holder.value().matchesSoil(soil))
                .findFirst()
                .orElse(null);

        if (recipeHolder != null) {
            cachedGrowthRecipe = recipeHolder.value();
            isCrop = "crop".equals(cachedGrowthRecipe.renderer());
        } else {
            cachedGrowthRecipe = null;
            isCrop = false;
        }
    }

    @Nullable
    private GrowthRecipe findGrowthRecipe(ItemStack seed) {
        if (seed.isEmpty()) return null;
        refreshRecipeCacheIfNeeded(seed);
        return cachedGrowthRecipe;
    }

    // ============================================================
    // 土壤验证
    // ============================================================
    private Set<Item> getValidSoils() {
        if (soilCacheValid && cachedValidSoils != null) {
            return cachedValidSoils;
        }

        RecipeManager rm = getRecipeManager();
        if (rm == null) return Set.of();

        Set<Item> soils = new HashSet<>();
        for (RecipeHolder<?> holder : rm.getRecipes()) {
            if (holder.value().getType() == ResourcePlantPotRegister.GROWTH.get()) {
                GrowthRecipe recipe = (GrowthRecipe) holder.value();
                for (Ingredient ing : recipe.soils()) {
                    ing.getValues().stream().map(Holder::value).forEach(soils::add);
                }
            }
        }

        cachedValidSoils = Collections.unmodifiableSet(soils);
        soilCacheValid = true;
        return cachedValidSoils;
    }

    // ============================================================
    // 配方验证方法
    // ============================================================
    public boolean isValidPlant(ItemStack stack) {
        if (stack.isEmpty() || level == null) return false;
        return findGrowthRecipe(stack) != null;
    }

    public boolean isValidSoilForAnyRecipe(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return getValidSoils().contains(stack.getItem());
    }

    public boolean isValidPlantSoilCombination(ItemStack plant, ItemStack soil) {
        if (plant.isEmpty() || soil.isEmpty()) return false;
        GrowthRecipe recipe = findGrowthRecipe(plant);
        if (recipe == null) return false;
        return recipe.matchesSoil(soil);
    }

    // ============================================================
    // 生长逻辑
    // ============================================================
    public static void tick(Level level, BonsaiPotBlockEntity be) {
        if (level.isClientSide()) return;

        ItemStack plantStack = be.getStack(SEED_SLOT);
        ItemStack soilStack = be.getStack(SOIL_SLOT);

        // 检查是否有完整种植
        if (plantStack.isEmpty() || soilStack.isEmpty()) {
            be.resetGrowth();
            return;
        }

        // 检查种子-土壤组合是否有效
        if (!be.isValidPlantSoilCombination(plantStack, soilStack)) {
            be.resetGrowth();
            return;
        }

        // 已成熟则停止生长
        if (be.readyToHarvest) {
            return;
        }

        // 获取配方
        GrowthRecipe recipe = be.findGrowthRecipe(plantStack);
        if (recipe == null) {
            be.resetGrowth();
            return;
        }

        // 计算生长速度
        float totalMod = be.calculateGrowthModifier(soilStack);
        int adjustedTime = Math.max(1, Math.round((float) recipe.duration() / totalMod));

        be.growthTicks++;

        if (be.growthTicks >= adjustedTime) {
            // 成熟
            be.readyToHarvest = true;
            be.growthProgress = 100;
            be.lastGrowthStage = be.calculateGrowthStage();
            be.updateAndSync();
        } else {
            be.growthProgress = (int) ((float) be.growthTicks / adjustedTime * 100.0F);
            int stage = be.calculateGrowthStage();
            if (stage != be.lastGrowthStage) {
                be.lastGrowthStage = stage;
                be.updateAndSync();
            }
        }
    }

    private void updateAndSync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    protected float calculateGrowthModifier(ItemStack soilStack) {
        float soilMod = getSoilGrowthModifier(soilStack);
        float skyMod = getSkyDayModifier();
        return skyMod * soilMod;
    }

    public float getSkyDayModifier() {
        if (level == null) return 1;
        boolean canSeeSky = level.canSeeSky(getBlockPos().above());
        boolean isDay = level.isBrightOutside();
        return (canSeeSky && isDay) ? SKY_DAY_MODIFIER : 1;
    }

    public float getSoilGrowthModifier(ItemStack soilStack) {
        if (soilStack.isEmpty()) return 1.0F;
        var data = soilStack.typeHolder().getData(ResourcePlantPotRegister.SOIL_MODIFIERS);
        return data != null ? data.growthModifier() : 1.0F;
    }

    // ============================================================
    // 生长阶段
    // ============================================================
    public int getGrowthStage() {
        return calculateGrowthStage();
    }

    private int calculateGrowthStage() {
        if (!isCrop) {
            return growthProgress > 50 ? 1 : 0;
        }
        return Math.min(8, (int) (growthProgress / 12.5f));
    }

    // ============================================================
    // 收获
    // ============================================================
    protected void resetGrowth() {
        growthProgress = 0;
        growthTicks = 0;
        readyToHarvest = false;
        lastGrowthStage = -1;
        setChanged();
    }

    public void harvestPlant(Level level, BlockPos pos) {
        if (!readyToHarvest) return;

        ItemStack seedForDrops = getStack(SEED_SLOT);
        List<ItemStack> drops = getHarvestDrops(seedForDrops);

        for (ItemStack drop : drops) {
            double x = pos.getX() + 0.25 + level.getRandom().nextDouble() * 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.25 + level.getRandom().nextDouble() * 0.5;
            ItemEntity entity = new ItemEntity(level, x, y, z, drop);
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }

        resetGrowth();
    }

    protected List<ItemStack> getHarvestDrops(ItemStack plantStack) {
        List<ItemStack> drops = new ArrayList<>();
        if (plantStack.isEmpty()) return drops;

        GrowthRecipe recipe = findGrowthRecipe(plantStack);
        if (recipe == null) return drops;

        Random rng = new Random();
        for (OutputEntry entry : recipe.output()) {
            if (rng.nextFloat() <= entry.chance()) {
                int count = entry.max() > entry.min() ? entry.min() + rng.nextInt(entry.max() - entry.min() + 1) : entry.min();
                drops.add(new ItemStack(entry.item(), count));
            }
        }
        return drops;
    }

    // ============================================================
    // 施肥
    // ============================================================
    public boolean applyManualFertilizer(float speedMultiplier) {
        if (readyToHarvest) return false;

        ItemStack plantStack = getStack(SEED_SLOT);
        ItemStack soilStack = getStack(SOIL_SLOT);
        if (plantStack.isEmpty() || soilStack.isEmpty()) return false;

        GrowthRecipe recipe = findGrowthRecipe(plantStack);
        if (recipe == null || !recipe.fertilizable()) return false;

        float soilMod = getSoilGrowthModifier(soilStack);
        int adjustedTime = Math.max(1, Math.round((float) recipe.duration() / soilMod));
        int boost = Math.max(1, Math.round(BASE_FERTILIZER_BOOST * speedMultiplier));

        growthTicks = Math.min(adjustedTime, growthTicks + boost);
        growthProgress = (int) ((float) growthTicks / adjustedTime * 100.0F);

        if (growthTicks >= adjustedTime) {
            readyToHarvest = true;
            growthProgress = 100;
        }

        lastGrowthStage = calculateGrowthStage();
        setChanged();
        return true;
    }

    // ============================================================
    // 掉落
    // ============================================================
    @Override
    public void preRemoveSideEffects(@NonNull BlockPos pos, @NonNull BlockState state) {
        drops();
    }

    public void drops() {
        if (level == null) return;
        SimpleContainer inv = new SimpleContainer(inventory.size());
        for (int i = 0; i < inventory.size(); i++) {
            inv.setItem(i, getStack(i));
        }
        Containers.dropContents(level, worldPosition, inv);
    }
}
