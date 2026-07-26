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
import net.minecraft.world.InteractionResult;
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
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.NonNull;

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
    protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level,
                                           @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BonsaiPotBlockEntity(ResourcePlantPotRegister.BONSAI_POT_BLOCK_ENTITY.get(), pos, state);
    }

    // ============================================================
    // 右键交互
    // ============================================================
    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state,
                                                   @NonNull Level level, @NonNull BlockPos pos,
                                                   @NonNull Player player, @NonNull InteractionHand hand,
                                                   @NonNull BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof BonsaiPotBlockEntity planter)) {
            return InteractionResult.FAIL;
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
    private InteractionResult handleHarvest(Level level, BlockPos pos, BonsaiPotBlockEntity planter) {
        if (!level.isClientSide()) {
            planter.harvestPlant(level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleExtract(Level level, BlockPos pos, Player player, BonsaiPotBlockEntity planter) {
        boolean hasExtractable = !planter.getStack(SEED_SLOT).isEmpty() || !planter.getStack(SOIL_SLOT).isEmpty();
        if (!hasExtractable) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // 尝试提取种子
        if (tryExtractSlot(level, pos, player, planter, SEED_SLOT)) {
            return InteractionResult.SUCCESS;
        }

        // 种子为空，尝试提取土壤
        if (tryExtractSlot(level, pos, player, planter, SOIL_SLOT)) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private boolean tryExtractSlot(Level level, BlockPos pos, Player player,
                                   BonsaiPotBlockEntity planter, int slot) {
        ItemStack stack = planter.getStack(slot);
        if (stack.isEmpty()) return false;

        ItemResource resource = ItemResource.of(stack);
        try (Transaction tx = Transaction.openRoot()) {
            int extracted = planter.inventory.extract(slot, resource, 1, tx);
            if (extracted > 0) {
                tx.commit();
                giveItemToPlayer(player, resource.toStack(extracted));
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1.0F);
                return true;
            }
        }
        return false;
    }

    private void giveItemToPlayer(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private InteractionResult handleNormalInteraction(Level level, BlockPos pos, Player player,
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

        return InteractionResult.PASS;
    }

    private InteractionResult handlePlantInsert(Level level, BlockPos pos, Player player,
                                                BonsaiPotBlockEntity planter, ItemStack heldItem) {
        if (!planter.getStack(SEED_SLOT).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack existingSoil = planter.getStack(SOIL_SLOT);
        if (!existingSoil.isEmpty() && !planter.isValidPlantSoilCombination(heldItem, existingSoil)) {
            player.sendOverlayMessage(
                    Component.translatable("message.resource_farm.invalid_seed_soil_combination")
                            .withStyle(ChatFormatting.GOLD));
            return InteractionResult.SUCCESS;
        }

        insertItemIntoSlot(planter, SEED_SLOT, heldItem, level, pos, player,
                SoundEvents.CROP_PLANTED, 1.0F);
        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleSoilInsert(Level level, BlockPos pos, Player player,
                                               BonsaiPotBlockEntity planter, ItemStack heldItem) {
        if (!planter.getStack(SOIL_SLOT).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack existingPlant = planter.getStack(SEED_SLOT);
        if (!existingPlant.isEmpty() && !planter.isValidPlantSoilCombination(existingPlant, heldItem)) {
            player.sendOverlayMessage(
                    Component.translatable("message.resource_farm.invalid_seed_soil_combination")
                            .withStyle(ChatFormatting.GOLD));
            return InteractionResult.SUCCESS;
        }

        insertItemIntoSlot(planter, SOIL_SLOT, heldItem, level, pos, player,
                SoundEvents.GRAVEL_PLACE, 0.8F);
        return InteractionResult.SUCCESS;
    }

    private void insertItemIntoSlot(BonsaiPotBlockEntity planter, int slot, ItemStack heldItem,
                                    Level level, BlockPos pos, Player player,
                                    SoundEvent sound, float pitch) {
        try (Transaction tx = Transaction.openRoot()) {
            planter.inventory.insert(slot, ItemResource.of(heldItem), 1, tx);
            tx.commit();
        }

        if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
        }

        level.playSound(null, pos, sound, SoundSource.BLOCKS, (float) 1.0, pitch);
        level.sendBlockUpdated(pos, planter.getBlockState(), planter.getBlockState(), 2);
        planter.setChanged();
    }

    private InteractionResult handleFertilizer(Level level, BlockPos pos, Player player,
                                               BonsaiPotBlockEntity planter, ItemStack heldItem) {
        if (planter.getStack(SEED_SLOT).isEmpty() || planter.getStack(SOIL_SLOT).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        var data = heldItem.typeHolder().getData(ResourcePlantPotRegister.FERTILIZERS);
        if (data == null) {
            return InteractionResult.PASS;
        }

        if (!planter.applyManualFertilizer(data.speedMultiplier())) {
            return InteractionResult.PASS;
        }

        if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
        }

        spawnFertilizerParticles(level, pos);
        level.sendBlockUpdated(pos, planter.getBlockState(), planter.getBlockState(), 3);
        planter.setChanged();
        return InteractionResult.SUCCESS;
    }

    private void spawnFertilizerParticles(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                    6, 0.3, 0.2, 0.3, 0.0);
        }
    }

    private InteractionResult handleHoeTill(Level level, BlockPos pos, Player player,
                                            BonsaiPotBlockEntity planter, ItemStack heldItem,
                                            InteractionHand hand, BlockHitResult hitResult) {
        ItemStack soilStack = planter.getStack(SOIL_SLOT);
        if (soilStack.isEmpty() || !(soilStack.getItem() instanceof BlockItem soilBlockItem)) {
            return InteractionResult.PASS;
        }

        BlockState soilState = soilBlockItem.getBlock().defaultBlockState();
        BlockState result = soilState.getToolModifiedState(
                new UseOnContext(level, player, hand, heldItem, hitResult),
                ItemAbilities.HOE_TILL, false);

        if (result == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        try (Transaction tx = Transaction.openRoot()) {
            planter.inventory.extract(SOIL_SLOT, ItemResource.of(soilStack), 1, tx);
            planter.inventory.insert(SOIL_SLOT, ItemResource.of(new ItemStack(result.getBlock())), 1, tx);
            tx.commit();
        }

        level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (!player.getAbilities().instabuild) {
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            heldItem.hurtAndBreak(1, player, slot);
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    private static boolean isFertilizer(ItemStack stack) {
        return !stack.isEmpty() && stack.typeHolder().getData(ResourcePlantPotRegister.FERTILIZERS) != null;
    }

    // ============================================================
    // Ticker
    // ============================================================
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state,
                                                                  @NonNull BlockEntityType<T> type) {
        return (lvl, _, _, be) -> {
            if (be instanceof BonsaiPotBlockEntity station) {
                BonsaiPotBlockEntity.tick(lvl, station);
            }
        };
    }
}
