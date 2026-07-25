package com.maple.resource_farm.common.block.ResourceTree;

import com.maple.resource_farm.api.ResourceTree.ResourceTreeType;
import com.maple.resource_farm.data.ResourceFarmBlocks;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.util.Lazy;

import org.jetbrains.annotations.NotNull;

public class ResourceLeavesBlock extends UntintedParticleLeavesBlock {

    private final Lazy<String> translateKey;
    private final ResourceTreeType treeType;

    public ResourceLeavesBlock(String treeId, BlockBehaviour.Properties properties) {
        super(0.01F, ParticleTypes.CHERRY_LEAVES, properties);
        this.translateKey = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getTranslateKey();
        this.treeType = ResourceFarmBlocks.ResourceTreeMap.get(treeId).getResourceTreeConfig().treeType();
    }

    public static ResourceLeavesBlock create(String treeId, Properties properties) {
        return new ResourceLeavesBlock(treeId, properties);
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(treeType.leavesTranslateKey(), Component.translatable(translateKey.get()));
    }
}
