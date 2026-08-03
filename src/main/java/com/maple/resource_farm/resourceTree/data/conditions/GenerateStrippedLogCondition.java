package com.maple.resource_farm.resourceTree.data.conditions;

import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;

public record GenerateStrippedLogCondition() implements ICondition {

        public static final MapCodec<GenerateStrippedLogCondition> CODEC
                = MapCodec.unit(new GenerateStrippedLogCondition());

        @Override
        public boolean test(ICondition.IContext context) {
            return ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedLog;
        }

        @Override
        public MapCodec<? extends ICondition> codec() {
            return CODEC;
        }
}

