package com.resource_farm.common.block.ResourceTree;

import com.resource_farm.api.ResourceTree.ResourceTreeType;
import com.resource_farm.api.block.ColoringSettings;
import com.resource_farm.common.block.ColoringBlock;
import com.resource_farm.config.ResourceFarmConfigHolder;
import com.resource_farm.data.ResourceFarmBlocks;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import com.tterrag.registrate.util.entry.BlockEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourceWoodBlock extends ColoringBlock {

    private final String treeId;
    private final String translateKey;
    private final ResourceTreeType treeType;

    public ResourceWoodBlock(String treeId,
                             Properties properties,
                             int lightLevel,
                             ColoringSettings coloringSettings) {
        super(properties, lightLevel, coloringSettings);
        this.treeId = treeId;
        this.translateKey = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
    }

    public static ResourceWoodBlock create(String treeId,
                                           Properties properties,
                                           int lightLevel,
                                           ColoringSettings coloringSettings) {
        return new ResourceWoodBlock(treeId, properties, lightLevel, coloringSettings);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(@NotNull BlockState state, @NotNull UseOnContext context, @NotNull ItemAbility ability, boolean simulate) {
        if (ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.blockGeneration.generateStrippedWood) {
            if (ability == ItemAbilities.AXE_STRIP) {
                BlockEntry<?> strippedWood = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getStrippedWood();
                if (strippedWood != null) {
                    return strippedWood.getDefaultState();
                }
            }
        }
        return super.getToolModifiedState(state, context, ability, simulate);
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.woodTranslateKey(), Component.translatable(translateKey));
    }
}
