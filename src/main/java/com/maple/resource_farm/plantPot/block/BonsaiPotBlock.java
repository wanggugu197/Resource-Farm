package com.maple.resource_farm.plantPot.block;

import com.maple.resource_farm.plantPot.ResourcePlantPotRegister;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BonsaiPotBlock extends BaseEntityBlock {

    // ========== 常量 ==========
    private static final int SEED_SLOT = 0;
    private static final int SOIL_SLOT = 1;

    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 5, 1),
            Block.box(0, 0, 15, 16, 5, 16),
            Block.box(0, 0, 1, 1, 5, 15),
            Block.box(15, 0, 1, 16, 5, 15),
            Block.box(1, 1, 1, 15, 2, 15));

    public static final MapCodec<BonsaiPotBlock> CODEC = simpleCodec(BonsaiPotBlock::new);

    public BonsaiPotBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                           @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BonsaiPotBlockEntity(ResourcePlantPotRegister.BONSAI_POT_BLOCK_ENTITY.get(), pos, state);
    }

    // ============================================================
    // 右键交互
    // ============================================================
    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
                                                       @NotNull Level level, @NotNull BlockPos pos,
                                                       @NotNull Player player, @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof BonsaiPotBlockEntity planter)) {
            return ItemInteractionResult.FAIL;
        }

        // 优先级1: 成熟时收获（不消耗任何物品）
        if (planter.isReadyToHarvest()) {
            return handleHarvest(level, pos, planter);
        }

        ItemStack heldItem = player.getItemInHand(hand);

        // 优先级2: 潜行右键 → 提取物品
        if (player.isShiftKeyDown()) {
            return handleExtract(level, pos, player, planter);
        }

        // 优先级3: 正常交互
        return handleNormalInteraction(level, pos, player, planter, heldItem, hand, hitResult);
    }

    // ---------- 交互子方法 ----------
    private ItemInteractionResult handleHarvest(Level level, BlockPos pos, BonsaiPotBlockEntity planter) {
        if (!level.isClientSide()) {
            planter.harvestPlant(level, pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    private ItemInteractionResult handleExtract(Level level, BlockPos pos, Player player, BonsaiPotBlockEntity planter) {
        boolean hasExtractable = !planter.getStack(SEED_SLOT).isEmpty() || !planter.getStack(SOIL_SLOT).isEmpty();
        if (!hasExtractable) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide()) {
            return ItemInteractionResult.sidedSuccess(true);
        }

        // 尝试提取种子
        if (tryExtractSlot(level, pos, player, planter, SEED_SLOT)) {
            return ItemInteractionResult.sidedSuccess(false);
        }

        // 种子为空，尝试提取土壤
        if (tryExtractSlot(level, pos, player, planter, SOIL_SLOT)) {
            return ItemInteractionResult.sidedSuccess(false);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean tryExtractSlot(Level level, BlockPos pos, Player player,
                                   BonsaiPotBlockEntity planter, int slot) {
        ItemStack stack = planter.getStack(slot);
        if (stack.isEmpty()) return false;

        ItemStack extracted = planter.inventory.extractItem(slot, 1, false);
        if (!extracted.isEmpty()) {
            giveItemToPlayer(player, extracted);
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1.0F);
            return true;
        }
        return false;
    }

    private void giveItemToPlayer(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private ItemInteractionResult handleNormalInteraction(Level level, BlockPos pos, Player player,
                                                          BonsaiPotBlockEntity planter, ItemStack heldItem,
                                                          InteractionHand hand, BlockHitResult hitResult) {
        // 插入植物（种子/树苗）
        if (!planter.getStack(SOIL_SLOT).isEmpty() && planter.isValidPlant(heldItem)) {
            return handlePlantInsert(level, pos, player, planter, heldItem);
        }

        // 插入土壤
        if (planter.isValidSoilForAnyRecipe(heldItem)) {
            return handleSoilInsert(level, pos, player, planter, heldItem);
        }

        // 施肥
        if (isFertilizer(heldItem)) {
            return handleFertilizer(level, pos, player, planter, heldItem);
        }

        // 锄头耕地转换
        if (heldItem.getItem() instanceof HoeItem) {
            return handleHoeTill(level, pos, player, planter, heldItem, hand, hitResult);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private ItemInteractionResult handlePlantInsert(Level level, BlockPos pos, Player player,
                                                    BonsaiPotBlockEntity planter, ItemStack heldItem) {
        if (!planter.getStack(SEED_SLOT).isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide()) {
            return ItemInteractionResult.sidedSuccess(true);
        }

        ItemStack existingSoil = planter.getStack(SOIL_SLOT);
        if (!existingSoil.isEmpty() && !planter.isValidPlantSoilCombination(heldItem, existingSoil)) {
            player.displayClientMessage(
                    Component.translatable("message.resource_farm.invalid_seed_soil_combination")
                            .withStyle(ChatFormatting.GOLD),
                    true);
            return ItemInteractionResult.sidedSuccess(false);
        }

        insertItemIntoSlot(planter, SEED_SLOT, heldItem, level, pos, player,
                SoundEvents.CROP_PLANTED, 1.0F);
        return ItemInteractionResult.sidedSuccess(false);
    }

    private ItemInteractionResult handleSoilInsert(Level level, BlockPos pos, Player player,
                                                   BonsaiPotBlockEntity planter, ItemStack heldItem) {
        if (!planter.getStack(SOIL_SLOT).isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide()) {
            return ItemInteractionResult.sidedSuccess(true);
        }

        ItemStack existingPlant = planter.getStack(SEED_SLOT);
        if (!existingPlant.isEmpty() && !planter.isValidPlantSoilCombination(existingPlant, heldItem)) {
            player.displayClientMessage(
                    Component.translatable("message.resource_farm.invalid_seed_soil_combination")
                            .withStyle(ChatFormatting.GOLD),
                    true);
            return ItemInteractionResult.sidedSuccess(false);
        }

        insertItemIntoSlot(planter, SOIL_SLOT, heldItem, level, pos, player,
                SoundEvents.GRAVEL_PLACE, 0.8F);
        return ItemInteractionResult.sidedSuccess(false);
    }

    private void insertItemIntoSlot(BonsaiPotBlockEntity planter, int slot, ItemStack heldItem,
                                    Level level, BlockPos pos, Player player,
                                    SoundEvent sound, float pitch) {
        planter.inventory.insertItem(slot, heldItem.copyWithCount(1), false);

        if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
        }

        level.playSound(null, pos, sound, SoundSource.BLOCKS, (float) 1.0, pitch);
        level.sendBlockUpdated(pos, planter.getBlockState(), planter.getBlockState(), 2);
        planter.setChanged();
    }

    private ItemInteractionResult handleFertilizer(Level level, BlockPos pos, Player player,
                                                   BonsaiPotBlockEntity planter, ItemStack heldItem) {
        if (planter.getStack(SEED_SLOT).isEmpty() || planter.getStack(SOIL_SLOT).isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide()) {
            return ItemInteractionResult.sidedSuccess(true);
        }

        var data = heldItem.getItemHolder().getData(ResourcePlantPotRegister.FERTILIZERS);
        if (data == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!planter.applyManualFertilizer(data.speedMultiplier())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
        }

        spawnFertilizerParticles(level, pos);
        level.sendBlockUpdated(pos, planter.getBlockState(), planter.getBlockState(), 3);
        planter.setChanged();
        return ItemInteractionResult.sidedSuccess(false);
    }

    private void spawnFertilizerParticles(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                    6, 0.3, 0.2, 0.3, 0.0);
        }
    }

    private ItemInteractionResult handleHoeTill(Level level, BlockPos pos, Player player,
                                                BonsaiPotBlockEntity planter, ItemStack heldItem,
                                                InteractionHand hand, BlockHitResult hitResult) {
        ItemStack soilStack = planter.getStack(SOIL_SLOT);
        if (soilStack.isEmpty() || !(soilStack.getItem() instanceof BlockItem soilBlockItem)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockState soilState = soilBlockItem.getBlock().defaultBlockState();
        BlockState result = soilState.getToolModifiedState(
                new UseOnContext(level, player, hand, heldItem, hitResult),
                ItemAbilities.HOE_TILL, false);

        if (result == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide()) {
            return ItemInteractionResult.sidedSuccess(true);
        }

        planter.inventory.extractItem(SOIL_SLOT, 1, false);
        planter.inventory.insertItem(SOIL_SLOT, new ItemStack(result.getBlock()), false);

        level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (!player.getAbilities().instabuild) {
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            heldItem.hurtAndBreak(1, player, slot);
        }

        return ItemInteractionResult.sidedSuccess(false);
    }

    private static boolean isFertilizer(ItemStack stack) {
        return !stack.isEmpty() && stack.getItemHolder().getData(ResourcePlantPotRegister.FERTILIZERS) != null;
    }

    // ============================================================
    // Ticker
    // ============================================================
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return (lvl, ignoredState, ignoredType, be) -> {
            if (be instanceof BonsaiPotBlockEntity station) {
                BonsaiPotBlockEntity.tick(lvl, station);
            }
        };
    }
}
