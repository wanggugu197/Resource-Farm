package com.maple.resource_farm.plantPot.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FertilizerData(float speedMultiplier, float yieldMultiplier) {

    public static final Codec<FertilizerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("speed_multiplier", 1f).forGetter(FertilizerData::speedMultiplier),
            Codec.FLOAT.optionalFieldOf("yield_multiplier", 1f).forGetter(FertilizerData::yieldMultiplier)).apply(instance, FertilizerData::new));
}
