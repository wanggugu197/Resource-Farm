package com.maple.resource_farm.plantPot.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SoilModifierData(float growthModifier) {

    public static final Codec<SoilModifierData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("growth_modifier", 1f).forGetter(SoilModifierData::growthModifier)).apply(instance, SoilModifierData::new));
}
