package com.maple.resource_farm.plantPot.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record OutputEntry(Item item, int min, int max, float chance) {

    public static OutputEntry of(Item item, int min, int max, float chance) {
        return new OutputEntry(item, min, max, chance);
    }

    public static OutputEntry of(Item item, int min, int max) {
        return new OutputEntry(item, min, max, 1);
    }

    public static OutputEntry of(Item item, float chance) {
        return new OutputEntry(item, 1, 1, chance);
    }

    public static OutputEntry of(Item item) {
        return new OutputEntry(item, 1, 1, 1);
    }

    public static final Codec<OutputEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(OutputEntry::item),
            Codec.INT.optionalFieldOf("min", 1).forGetter(OutputEntry::min),
            Codec.INT.optionalFieldOf("max", 1).forGetter(OutputEntry::max),
            Codec.FLOAT.optionalFieldOf("chance", 1f).forGetter(OutputEntry::chance)).apply(instance, OutputEntry::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, OutputEntry> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ITEM), OutputEntry::item,
            ByteBufCodecs.INT, OutputEntry::min,
            ByteBufCodecs.INT, OutputEntry::max,
            ByteBufCodecs.FLOAT, OutputEntry::chance,
            OutputEntry::new);
}
