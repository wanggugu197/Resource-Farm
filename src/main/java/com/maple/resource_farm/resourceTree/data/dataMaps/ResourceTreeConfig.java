package com.maple.resource_farm.resourceTree.data.dataMaps;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.Lazy;

import com.google.gson.JsonElement;
import com.mapleutillib.utils.FormattingUtil;
import com.mapleutillib.utils.RLUtils;
import com.mapleutillib.utils.RegistriesUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * 运行时资源树配置。
 */
public record ResourceTreeConfig(
                                 String id,
                                 @Nullable String correspondingItem,
                                 @Nullable String translateKey,
                                 boolean automaticBasicRecipe,
                                 int productOutput,
                                 ResourceTreeBaseType treeType,
                                 ResourceTreeExtraType oreType,
                                 ResourceTreeFertilizeSettings fertilizeSetting,
                                 int growthFrequency,
                                 Lazy<Block> customPlaceBlock,
                                 @Nullable TagKey<Block> customPlaceBlockTag,
                                 int lightLevel,
                                 int color,
                                 Identifier growerId) {

    private static final Codec<Identifier> STYLE_ID_CODEC = Codec.STRING.xmap(
            s -> RLUtils.parse(s.toLowerCase(Locale.ROOT)),
            Identifier::toString);

    private static final Codec<Integer> COLOR_CODEC = Codec.withAlternative(
            Codec.INT,
            Codec.STRING.xmap(FormattingUtil::parseColorString, i -> String.format("0x%06X", i & 0xFFFFFF)));

    /**
     * 数据包字段形态（仅解码用）。解析后立刻转为 {@link ResourceTreeConfig}，不缓存。
     */
    private record DatapackJson(
                                Optional<String> group,
                                Optional<String> item,
                                Optional<String> translateKey,
                                boolean automaticBasicRecipe,
                                int productOutput,
                                Identifier treeStyle,
                                Identifier oreStyle,
                                ResourceTreeFertilizeSettings fertilize,
                                int growthFrequency,
                                Optional<String> customPlaceBlock,
                                Optional<String> customPlaceBlockTag,
                                int lightLevel,
                                int color,
                                Identifier grower) {

        static final Codec<DatapackJson> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group").forGetter(DatapackJson::group),
                Codec.STRING.optionalFieldOf("item").forGetter(DatapackJson::item),
                Codec.STRING.optionalFieldOf("translate_key").forGetter(DatapackJson::translateKey),
                Codec.BOOL.optionalFieldOf("automatic_basic_recipe", true).forGetter(DatapackJson::automaticBasicRecipe),
                Codec.INT.optionalFieldOf("product_output", 1).forGetter(DatapackJson::productOutput),
                STYLE_ID_CODEC.optionalFieldOf("tree_style", RLUtils.parse("oak")).forGetter(DatapackJson::treeStyle),
                STYLE_ID_CODEC.optionalFieldOf("ore_style", RLUtils.parse("iron")).forGetter(DatapackJson::oreStyle),
                ResourceTreeFertilizeSettings.CODEC.optionalFieldOf("fertilize", ResourceTreeFertilizeSettings.DEFAULT)
                        .forGetter(DatapackJson::fertilize),
                Codec.INT.optionalFieldOf("growth_frequency", 10).forGetter(DatapackJson::growthFrequency),
                Codec.STRING.optionalFieldOf("custom_place_block").forGetter(DatapackJson::customPlaceBlock),
                Codec.STRING.optionalFieldOf("custom_place_block_tag").forGetter(DatapackJson::customPlaceBlockTag),
                Codec.INT.optionalFieldOf("light_level", 0).forGetter(DatapackJson::lightLevel),
                COLOR_CODEC.optionalFieldOf("color", 0).forGetter(DatapackJson::color),
                STYLE_ID_CODEC.optionalFieldOf("grower", RLUtils.parse("oak")).forGetter(DatapackJson::grower))
                .apply(instance, DatapackJson::new));

        @Nullable
        String groupOrNull() {
            return group.filter(s -> !s.isBlank()).orElse(null);
        }

        ResourceTreeConfig toConfig() {
            if (item.isEmpty() && translateKey.isEmpty()) {
                throw new IllegalArgumentException("item and translate_key cannot both be absent");
            }
            TagKey<Block> placeTag = customPlaceBlockTag
                    .filter(s -> !s.isBlank())
                    .map(s -> TagKey.create(Registries.BLOCK, RLUtils.parse(s)))
                    .orElse(null);
            return ResourceTreeConfig.create(
                    item.orElse(null),
                    translateKey.orElse(null),
                    automaticBasicRecipe,
                    productOutput,
                    ResourceFarmMaps.getBaseType(treeStyle),
                    ResourceFarmMaps.getExtraType(oreStyle),
                    fertilize != null ? fertilize : ResourceTreeFertilizeSettings.DEFAULT,
                    growthFrequency,
                    customPlaceBlock.filter(s -> !s.isBlank()).orElse(null),
                    placeTag,
                    lightLevel,
                    color,
                    grower);
        }
    }

    /**
     * 数据包解析结果：preset 分组键 + 运行时配置。
     */
    public record Parsed(@Nullable String group, ResourceTreeConfig config) {}

    public ResourceTreeConfig {
        Objects.requireNonNull(growerId, "growerId");
        lightLevel = Mth.clamp(lightLevel, 0, 15);
        color = 0xFF000000 | (color & 0x00FFFFFF);
        id = generateId(correspondingItem, translateKey);
    }

    /**
     * 将 {@code resource_tree} JSON 直接解析为 {@link ResourceTreeConfig}（及可选 group）。
     */
    public static Parsed fromDatapack(JsonElement json) {
        DatapackJson entry = DatapackJson.CODEC.parse(JsonOps.INSTANCE, json)
                .getOrThrow(err -> new IllegalArgumentException("Invalid resource_tree: " + err));
        return new Parsed(entry.groupOrNull(), entry.toConfig());
    }

    private static String generateId(@Nullable String correspondingItem, @Nullable String translationKey) {
        if (correspondingItem != null) {
            int colonIndex = correspondingItem.indexOf(':');
            if (colonIndex != -1 && "minecraft".equals(correspondingItem.substring(0, colonIndex))) {
                return correspondingItem.substring(colonIndex + 1).replace(':', '_');
            }
            return correspondingItem.replace(':', '_');
        }
        if (translationKey != null) {
            int lastDot = translationKey.lastIndexOf('.');
            return lastDot == -1 ? translationKey : translationKey.substring(lastDot + 1);
        }
        throw new IllegalArgumentException("correspondingItem and translateKey cannot both be null");
    }

    public static ResourceTreeConfig create(
                                            @Nullable String correspondingItem,
                                            @Nullable String translateKey,
                                            boolean automaticBasicRecipe,
                                            int productOutput,
                                            ResourceTreeBaseType treeStyle,
                                            ResourceTreeExtraType oreStyle,
                                            ResourceTreeFertilizeSettings fertilizeSetting,
                                            int growthFrequency,
                                            @Nullable String customPlaceBlock,
                                            @Nullable TagKey<Block> customPlaceBlockTag,
                                            int lightLevel,
                                            int colors,
                                            Identifier growerId) {
        return new ResourceTreeConfig(
                null,
                correspondingItem,
                translateKey,
                automaticBasicRecipe,
                productOutput,
                treeStyle,
                oreStyle,
                fertilizeSetting,
                growthFrequency,
                Lazy.of(() -> customPlaceBlock == null ? Blocks.BARRIER : RegistriesUtils.getBlock(customPlaceBlock)),
                customPlaceBlockTag,
                lightLevel,
                colors,
                growerId);
    }
}
