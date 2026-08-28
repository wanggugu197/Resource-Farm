package com.maple.resource_farm.resourceTree.data.dataMaps;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.Lazy;

import com.mapleutillib.utils.RegistriesUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Locale;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * 催熟配置。
 */
public record ResourceTreeFertilizeSettings(
                                            Lazy<Item> mainRipeningItem,
                                            double mainChance,
                                            Lazy<Item> secondaryRipeningItem,
                                            double secondaryChance) {

    public static final ResourceTreeFertilizeSettings DEFAULT = new ResourceTreeFertilizeSettings(Lazy.of(() -> Items.BONE_MEAL), 0.35, Lazy.of(() -> Items.BONE_MEAL), 0.35);
    public static final ResourceTreeFertilizeSettings NULL = new ResourceTreeFertilizeSettings(null, 0, null, 0);

    private static final Codec<ResourceTreeFertilizeSettings> FIELDS_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("main_item").forGetter(ignored -> Optional.empty()),
            Codec.DOUBLE.optionalFieldOf("main_chance", 0.0).forGetter(ResourceTreeFertilizeSettings::mainChance),
            Codec.STRING.optionalFieldOf("secondary_item").forGetter(ignored -> Optional.empty()),
            Codec.DOUBLE.optionalFieldOf("secondary_chance", 0.0).forGetter(ResourceTreeFertilizeSettings::secondaryChance)).apply(instance, (mainItem, mainChance, secondaryItem, secondaryChance) -> ofItems(mainItem.orElse(null), mainChance, secondaryItem.orElse(null), secondaryChance)));

    public static final Codec<ResourceTreeFertilizeSettings> CODEC = new Codec<>() {

        @Override
        public <T> DataResult<Pair<ResourceTreeFertilizeSettings, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<T> typeNode = ops.get(input, "type");
            if (typeNode.result().isPresent()) {
                DataResult<String> typeStr = ops.getStringValue(typeNode.result().get());
                if (typeStr.result().isPresent()) {
                    ResourceTreeFertilizeSettings preset = fromType(typeStr.result().get());
                    if (preset != null) {
                        return DataResult.success(Pair.of(preset, ops.empty()));
                    }
                }
            }
            return FIELDS_CODEC.decode(ops, input);
        }

        @Override
        public <T> DataResult<T> encode(ResourceTreeFertilizeSettings input, DynamicOps<T> ops, T prefix) {
            if (input == DEFAULT) {
                return ops.mergeToMap(prefix, ops.createString("type"), ops.createString("default"));
            }
            if (input == NULL) {
                return ops.mergeToMap(prefix, ops.createString("type"), ops.createString("null"));
            }
            return FIELDS_CODEC.encode(input, ops, prefix);
        }
    };

    public ResourceTreeFertilizeSettings {
        if (mainChance < 0.0 || mainChance > 1.0 || secondaryChance < 0.0 || secondaryChance > 1.0) {
            throw new IllegalArgumentException("fertilize chances must be in [0.0, 1.0]");
        }
    }

    @Nullable
    private static ResourceTreeFertilizeSettings fromType(@Nullable String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        return switch (type.trim().toLowerCase(Locale.ROOT)) {
            case "default" -> DEFAULT;
            case "null", "none" -> NULL;
            default -> null;
        };
    }

    private static ResourceTreeFertilizeSettings ofItems(
                                                         @Nullable String mainItem, double mainChance,
                                                         @Nullable String secondaryItem, double secondaryChance) {
        return new ResourceTreeFertilizeSettings(
                Lazy.of(() -> mainItem == null ? Items.BARRIER : RegistriesUtils.getItem(mainItem)),
                mainChance,
                Lazy.of(() -> secondaryItem == null ? Items.BARRIER : RegistriesUtils.getItem(secondaryItem)),
                secondaryChance);
    }
}
