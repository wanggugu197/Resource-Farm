package com.maple.resource_farm.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerAdvancementManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ServerAdvancementManager.class)
public interface ServerAdvancementManagerAccessor {

    @Accessor("advancements")
    Map<Identifier, AdvancementHolder> resource_farm$getAdvancements();

    @Accessor("advancements")
    void resource_farm$setAdvancements(Map<Identifier, AdvancementHolder> advancements);

    @Accessor("tree")
    void resource_farm$setTree(AdvancementTree tree);
}
