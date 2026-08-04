package com.maple.resource_farm.resourceTree.data.conditions;

import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;

import javax.annotation.Nullable;

public record TreeExistsCondition(@Nullable String group, @Nullable String treeId) implements ICondition {
    public static final MapCodec<TreeExistsCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.optionalFieldOf("group", null).forGetter(TreeExistsCondition::group),
            Codec.STRING.optionalFieldOf("tree_id", null).forGetter(TreeExistsCondition::treeId)
    ).apply(inst, TreeExistsCondition::new));

    @Override
    public boolean test(IContext context) {
        if (treeId != null && !treeId.isBlank()) {
            String id = treeId.endsWith("_tree") ? treeId : treeId + "_tree";
            return ResourceTreeAccessManagement.ResourceTreeMap.containsKey(id);
        }
        if (group == null || group.isBlank()) return true;
        String targetId = group.endsWith("_tree") ? group : group + "_tree";
        return ResourceTreeAccessManagement.ResourceTreeMap.containsKey(targetId);
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
