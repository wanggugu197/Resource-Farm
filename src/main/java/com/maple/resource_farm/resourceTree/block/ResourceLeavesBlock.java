package com.maple.resource_farm.resourceTree.block;

import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeBaseType;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.util.Lazy;

import org.jetbrains.annotations.NotNull;

public class ResourceLeavesBlock extends UntintedParticleLeavesBlock {

    private final Lazy<String> translateKey;
    private final ResourceTreeBaseType treeType;

    public ResourceLeavesBlock(String treeId, BlockBehaviour.Properties properties) {
        super(0, ParticleTypes.CHERRY_LEAVES, properties);
        this.translateKey = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
    }

    public static ResourceLeavesBlock create(String treeId, Properties properties) {
        return new ResourceLeavesBlock(treeId, properties);
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.leavesTranslateKey(), Component.translatable(translateKey.get()));
    }
}
