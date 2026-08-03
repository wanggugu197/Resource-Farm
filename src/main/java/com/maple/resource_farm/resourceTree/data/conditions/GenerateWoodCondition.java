package com.maple.resource_farm.resourceTree.data.conditions;

import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;

public record GenerateWoodCondition() implements ICondition {

    public static final MapCodec<GenerateWoodCondition> CODEC
            = MapCodec.unit(new GenerateWoodCondition());

    @Override
    public boolean test(ICondition.IContext context) {
        return ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateWood;
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
