package com.maple.resource_farm.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 1.21.1 中 {@code drops} 为 {@code ResourceKey<LootTable>}（非 Optional），需 @Mutable 才能写入。
 */
@Mixin(BlockBehaviour.class)
public interface BlockBehaviourAccessor {

    @Accessor("drops")
    @Mutable
    void setDrops(ResourceKey<LootTable> location);
}
