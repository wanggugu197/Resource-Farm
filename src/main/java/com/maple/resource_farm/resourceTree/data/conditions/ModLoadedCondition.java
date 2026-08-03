package com.maple.resource_farm.resourceTree.data.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.ICondition;

public record ModLoadedCondition(String modId) implements ICondition {
    public static final MapCodec<ModLoadedCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("modid").forGetter(ModLoadedCondition::modId)
    ).apply(inst, ModLoadedCondition::new));

    @Override
    public boolean test(ICondition.IContext context) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}