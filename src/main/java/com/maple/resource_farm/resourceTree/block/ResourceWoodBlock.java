package com.maple.resource_farm.resourceTree.block;

import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeBaseType;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.Lazy;

import com.gto.registrylib.util.entry.BlockEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourceWoodBlock extends Block {

    private final String treeId;
    private final Lazy<String> translateKey;
    private final ResourceTreeBaseType treeType;

    public ResourceWoodBlock(String treeId, Properties properties) {
        super(properties);
        this.treeId = treeId;
        this.translateKey = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
    }

    public static ResourceWoodBlock create(String treeId, Properties properties) {
        return new ResourceWoodBlock(treeId, properties);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(@NotNull BlockState state, @NotNull UseOnContext context,
                                                     @NotNull ItemAbility ability, boolean simulate) {
        if (ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedWood) {
            if (ability == ItemAbilities.AXE_STRIP) {
                BlockEntry<?> strippedWood = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getStrippedWood();
                if (strippedWood != null) {
                    return strippedWood.getDefaultState();
                }
            }
        }
        return super.getToolModifiedState(state, context, ability, simulate);
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.woodTranslateKey(), Component.translatable(translateKey.get()));
    }
}
