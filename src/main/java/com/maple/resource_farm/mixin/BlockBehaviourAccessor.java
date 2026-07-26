package com.maple.resource_farm.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

/**
 * 26.1.2 中 {@code output} 为 {@code final Optional&lt;ResourceKey&lt;LootTable&gt;&gt;}，需 @Mutable 才能写入。
 */
@Mixin(BlockBehaviour.class)
public interface BlockBehaviourAccessor {

    @Accessor("drops")
    @Mutable
    void setDrops(Optional<ResourceKey<LootTable>> location);
}
