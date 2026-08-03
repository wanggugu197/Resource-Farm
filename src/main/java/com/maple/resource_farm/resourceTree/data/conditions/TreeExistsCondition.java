package com.maple.resource_farm.resourceTree.data.conditions;

import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;

public record TreeExistsCondition(String treeId) implements ICondition {

    public static final MapCodec<TreeExistsCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("tree_id").forGetter(TreeExistsCondition::treeId)
    ).apply(inst, TreeExistsCondition::new));

    @Override
    public boolean test(ICondition.IContext context) {
        String targetId = treeId.endsWith("_tree") ? treeId : treeId + "_tree";
        return ResourceTreeAccessManagement.ResourceTreeMap.containsKey(targetId);
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
