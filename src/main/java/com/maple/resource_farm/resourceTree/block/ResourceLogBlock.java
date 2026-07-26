package com.maple.resource_farm.resourceTree.block;

import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeBaseType;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.Lazy;

import com.gto.registrylib.util.entry.BlockEntry;
import com.gto.registrylib.util.entry.ItemEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResourceLogBlock extends RotatedPillarBlock {

    private final String treeId;
    private final Lazy<String> translateKey;
    private final ResourceTreeBaseType treeType;
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;

    public ResourceLogBlock(String treeId, Properties properties) {
        super(properties);
        this.treeId = treeId;
        this.translateKey = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
        this.registerDefaultState(this.defaultBlockState().setValue(PERSISTENT, Boolean.FALSE));
    }

    public static ResourceLogBlock create(String treeId, Properties properties) {
        return new ResourceLogBlock(treeId, properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PERSISTENT);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(PERSISTENT, Boolean.TRUE);
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder params) {
        fallingResin(state, params.getLevel(), BlockPos.containing(params.getParameter(LootContextParams.ORIGIN)));
        return super.getDrops(state, params);
    }

    public void fallingResin(BlockState state, Level level, BlockPos pos) {
        boolean isPlayerPlaced = state.getValue(PERSISTENT);
        if (!isPlayerPlaced) {
            ItemEntry<?> saplingEntry = ResourceTreeAccessManagement.ResourceTreeMap.get(this.treeId).getResin();
            if (saplingEntry != null) {
                int resinCount = level.getRandom().nextFloat() < 0.75f ? 1 : 2;
                popResource(level, pos, new ItemStack(saplingEntry.get(), resinCount));
            }
        }
    }

    @Override
    public @Nullable BlockState getToolModifiedState(@NotNull BlockState state, @NotNull UseOnContext context,
                                                     @NotNull ItemAbility ability, boolean simulate) {
        if (ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedLog) {
            if (ability == ItemAbilities.AXE_STRIP) {
                BlockEntry<?> strippedLog = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getStrippedLog();
                if (strippedLog != null) {
                    fallingResin(state, context.getLevel(), context.getClickedPos());
                    return strippedLog.getDefaultState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
                }
            }
        }
        return super.getToolModifiedState(state, context, ability, simulate);
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.logTranslateKey(), Component.translatable(translateKey.get()));
    }
}
