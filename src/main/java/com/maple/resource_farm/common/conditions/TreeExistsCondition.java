package com.maple.resource_farm.common.conditions;

import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;

import net.neoforged.neoforge.common.conditions.ICondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public record TreeExistsCondition(@Nullable String treeId) implements ICondition {

    public static final MapCodec<TreeExistsCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.optionalFieldOf("tree_id", null).forGetter(TreeExistsCondition::treeId)).apply(inst, TreeExistsCondition::new));

    @Override
    public boolean test(@NotNull IContext context) {
        if (treeId == null || treeId.isBlank()) return false;
        String id = (treeId.endsWith("_tree") ? treeId : treeId + "_tree").toLowerCase().replace(":", "_");
        return ResourceTreeAccessManagement.ResourceTreeMap.containsKey(id);
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
