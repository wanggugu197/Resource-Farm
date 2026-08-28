package com.maple.resource_farm.plantPot.block;

import com.maple.resource_farm.plantPot.ResourcePlantPotRegister;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class HoppingBonsaiPotBlock extends BonsaiPotBlock {

    // ========== 形状 ==========
    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 2, 11, 2),
            Block.box(14, 0, 0, 16, 11, 2),
            Block.box(0, 0, 14, 2, 11, 16),
            Block.box(14, 0, 14, 16, 11, 16),
            Block.box(1, 2, 1, 15, 10, 2),
            Block.box(1, 2, 14, 15, 10, 15),
            Block.box(1, 2, 2, 2, 10, 14),
            Block.box(14, 2, 2, 15, 10, 15),
            Block.box(2, 2, 2, 14, 3, 14));

    public static final MapCodec<HoppingBonsaiPotBlock> CODEC = simpleCodec(HoppingBonsaiPotBlock::new);

    public HoppingBonsaiPotBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                           @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull MapCodec<? extends BonsaiPotBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new HoppingBonsaiPotBlockEntity(
                ResourcePlantPotRegister.HOPPING_BONSAI_POT_BLOCK_ENTITY.get(), pos, state);
    }

    // ============================================================
    // 右键交互
    // ============================================================
    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
                                                       @NotNull Level level, @NotNull BlockPos pos,
                                                       @NotNull Player player, @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof HoppingBonsaiPotBlockEntity be)) {
            return ItemInteractionResult.FAIL;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        // 潜行右键空手且玻璃罩存在 → 摘下玻璃罩
        if (player.isShiftKeyDown() && heldItem.isEmpty() && be.getClochedTier() != 0) {
            return handleClocheRemoval(level, pos, be);
        }

        // 手持玻璃罩且当前无罩 → 放置
        if (ClocheHelper.getClocheTire(heldItem.getItem()) != 0 && be.getClochedTier() == 0) {
            return handleClochePlace(level, pos, player, heldItem, be);
        }

        // 无玻璃罩时，走父类交互（种植、施肥等）
        if (be.getClochedTier() == 0) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        // 有玻璃罩且未触发上述操作 → 拒绝交互
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    // ---------- 辅助方法 ----------
    private ItemInteractionResult handleClocheRemoval(Level level, BlockPos pos, HoppingBonsaiPotBlockEntity be) {
        if (level.isClientSide()) {
            return ItemInteractionResult.sidedSuccess(true);
        }

        ItemStack cloche = ClocheHelper.getClocheItem(be.getClochedTier());
        if (cloche.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        be.setClochedTier(0);
        level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, cloche));
        level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.5F, 1.2F);
        return ItemInteractionResult.sidedSuccess(false);
    }

    private ItemInteractionResult handleClochePlace(Level level, BlockPos pos, Player player,
                                                    ItemStack heldItem, HoppingBonsaiPotBlockEntity be) {
        if (level.isClientSide()) {
            return ItemInteractionResult.sidedSuccess(true);
        }

        int tier = ClocheHelper.getClocheTire(heldItem.getItem());
        if (tier == 0) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        be.setClochedTier(tier);
        if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
        }
        level.playSound(null, pos, SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        return ItemInteractionResult.sidedSuccess(false);
    }

    // ============================================================
    // Ticker
    // ============================================================
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return (lvl, pos, ignoredState, be) -> {
            if (be instanceof HoppingBonsaiPotBlockEntity station) {
                HoppingBonsaiPotBlockEntity.tick(lvl, pos, station);
            }
        };
    }
}
