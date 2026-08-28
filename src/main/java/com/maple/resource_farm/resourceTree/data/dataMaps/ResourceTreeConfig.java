package com.maple.resource_farm.resourceTree.data.dataMaps;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
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

import java.util.*;

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
                                 ResourceLocation growerId,
                                 List<ExtraItemOutput> extraItemOutputs,
                                 Lazy<List<Ingredient>> saplingIngredients,    // Lazy 包装延迟解析
                                 @Nullable ContainerOutput containerOutput) {

    // ---------- 额外配方相关记录 ----------
    public record ExtraItemOutput(ResourceLocation item, int count) {

        public static final Codec<ExtraItemOutput> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                ResourceLocation.CODEC.fieldOf("item").forGetter(ExtraItemOutput::item),
                Codec.INT.fieldOf("count").forGetter(ExtraItemOutput::count)).apply(ins, ExtraItemOutput::new));
    }

    public record ContainerOutput(ResourceLocation container, int containerCount, ExtraItemOutput output) {

        public static final Codec<ContainerOutput> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                ResourceLocation.CODEC.fieldOf("container").forGetter(ContainerOutput::container),
                Codec.INT.fieldOf("container_count").forGetter(ContainerOutput::containerCount),
                ExtraItemOutput.CODEC.fieldOf("output").forGetter(ContainerOutput::output)).apply(ins, ContainerOutput::new));
    }

    /**
     * 额外配方数据（仅存储原始字符串，避免 Ingredient.CODEC 解析失败）
     */
    public record ExtraRecipes(
                               List<ExtraItemOutput> itemOutputs,
                               List<String> saplingIngredients,          // 存储字符串，后续转为 Ingredient
                               Optional<ContainerOutput> containerOutput) {

        public static final Codec<ExtraRecipes> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                ExtraItemOutput.CODEC.listOf().optionalFieldOf("item_outputs", List.of()).forGetter(ExtraRecipes::itemOutputs),
                Codec.STRING.listOf().optionalFieldOf("sapling_ingredients", List.of()).forGetter(ExtraRecipes::saplingIngredients),
                ContainerOutput.CODEC.optionalFieldOf("container_output").forGetter(ExtraRecipes::containerOutput)).apply(ins, ExtraRecipes::new));
    }
    // ----------------------------------------

    private static final Codec<ResourceLocation> STYLE_ID_CODEC = Codec.STRING.xmap(
            s -> RLUtils.parse(s.toLowerCase(Locale.ROOT)),
            ResourceLocation::toString);

    private static final Codec<Integer> COLOR_CODEC = Codec.withAlternative(
            Codec.INT,
            Codec.STRING.xmap(FormattingUtil::parseColorString, i -> String.format("0x%06X", i & 0xFFFFFF)));

    private record StyleJson(
                             ResourceLocation treeStyle,
                             ResourceLocation oreStyle,
                             ResourceLocation grower,
                             int lightLevel,
                             int color) {

        private static final Codec<StyleJson> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                STYLE_ID_CODEC.fieldOf("tree_style").forGetter(StyleJson::treeStyle),
                STYLE_ID_CODEC.fieldOf("ore_style").forGetter(StyleJson::oreStyle),
                STYLE_ID_CODEC.fieldOf("grower").forGetter(StyleJson::grower),
                Codec.INT.optionalFieldOf("light_level", 0).forGetter(StyleJson::lightLevel),
                COLOR_CODEC.optionalFieldOf("color", 0).forGetter(StyleJson::color)).apply(instance, StyleJson::new));
    }

    /**
     * 数据包字段形态（仅解码用）
     */
    private record DatapackJson(
                                Optional<String> group,
                                Optional<String> item,
                                Optional<String> translateKey,
                                boolean automaticBasicRecipe,
                                int productOutput,
                                StyleJson style,
                                ResourceTreeFertilizeSettings fertilize,
                                int growthFrequency,
                                Optional<String> customPlaceBlock,
                                Optional<String> customPlaceBlockTag,
                                Optional<ExtraRecipes> extraRecipes) {

        static final Codec<DatapackJson> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group").forGetter(DatapackJson::group),
                Codec.STRING.optionalFieldOf("item").forGetter(DatapackJson::item),
                Codec.STRING.optionalFieldOf("translate_key").forGetter(DatapackJson::translateKey),
                Codec.BOOL.optionalFieldOf("automatic_basic_recipe", true).forGetter(DatapackJson::automaticBasicRecipe),
                Codec.INT.optionalFieldOf("product_output", 1).forGetter(DatapackJson::productOutput),
                StyleJson.CODEC.fieldOf("style").forGetter(DatapackJson::style),
                ResourceTreeFertilizeSettings.CODEC.optionalFieldOf("fertilize", ResourceTreeFertilizeSettings.DEFAULT)
                        .forGetter(DatapackJson::fertilize),
                Codec.INT.optionalFieldOf("growth_frequency", 10).forGetter(DatapackJson::growthFrequency),
                Codec.STRING.optionalFieldOf("custom_place_block").forGetter(DatapackJson::customPlaceBlock),
                Codec.STRING.optionalFieldOf("custom_place_block_tag").forGetter(DatapackJson::customPlaceBlockTag),
                ExtraRecipes.CODEC.optionalFieldOf("extra_recipes").forGetter(DatapackJson::extraRecipes)).apply(instance, DatapackJson::new));

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

            List<ExtraItemOutput> extraOutputs = new ArrayList<>();
            List<String> saplingStrings;
            ContainerOutput container = null;
            if (extraRecipes.isPresent()) {
                ExtraRecipes er = extraRecipes.get();
                extraOutputs = er.itemOutputs();
                saplingStrings = er.saplingIngredients();
                container = er.containerOutput().orElse(null);
            } else {
                saplingStrings = new ArrayList<>();
            }

            // 使用 Lazy 包装 Ingredient 列表，延迟解析（仅在首次调用 get() 时转换）
            Lazy<List<Ingredient>> lazySapling = Lazy.of(() -> {
                List<Ingredient> saplings = new ArrayList<>();
                for (String sapling : saplingStrings) {
                    if (sapling.startsWith("#")) {
                        saplings.add(Ingredient.of(TagKey.create(Registries.ITEM, RLUtils.parse(sapling.substring(1)))));
                    } else {
                        saplings.add(Ingredient.of(BuiltInRegistries.ITEM.getOptional(RLUtils.parse(sapling)).orElse(Items.BARRIER)));
                    }
                }
                return saplings;
            });

            String id = generateId(item.orElse(null), translateKey.orElse(null));

            return new ResourceTreeConfig(
                    id,
                    item.orElse(null),
                    translateKey.orElse(null),
                    automaticBasicRecipe,
                    productOutput,
                    ResourceFarmMaps.getBaseType(style.treeStyle()),
                    ResourceFarmMaps.getExtraType(style.oreStyle()),
                    fertilize != null ? fertilize : ResourceTreeFertilizeSettings.DEFAULT,
                    growthFrequency,
                    Lazy.of(() -> customPlaceBlock.filter(s -> !s.isBlank())
                            .map(RegistriesUtils::getBlock)
                            .orElse(Blocks.BARRIER)),
                    placeTag,
                    style.lightLevel(),
                    style.color(),
                    style.grower(),
                    extraOutputs,
                    lazySapling,
                    container);
        }
    }

    // 紧凑构造器：校验和归一化
    public ResourceTreeConfig {
        Objects.requireNonNull(growerId, "growerId");
        lightLevel = Mth.clamp(lightLevel, 0, 15);
        color = 0xFF000000 | (color & 0x00FFFFFF);
        if (containerOutput != null && (containerOutput.containerCount() < 1 || containerOutput.containerCount() > 4)) {
            throw new IllegalArgumentException("container_count must be between 1 and 4");
        }
    }

    // 数据包解析入口
    public static Parsed fromDatapack(JsonElement json) {
        DatapackJson entry = DatapackJson.CODEC.parse(JsonOps.INSTANCE, json)
                .getOrThrow(err -> new IllegalArgumentException("Invalid resource_tree: " + err));
        return new Parsed(entry.groupOrNull(), entry.toConfig());
    }

    // 生成ID（public 供外部使用）
    public static String generateId(@Nullable String correspondingItem, @Nullable String translationKey) {
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

    // 工厂方法（适配新的 Lazy 字段）
    public static ResourceTreeConfig create(
                                            @Nullable String correspondingItem,
                                            @Nullable String translateKey,
                                            boolean automaticBasicRecipe,
                                            int productOutput,
                                            ResourceTreeBaseType treeStyle,
                                            ResourceTreeExtraType oreType,
                                            ResourceTreeFertilizeSettings fertilizeSetting,
                                            int growthFrequency,
                                            @Nullable String customPlaceBlock,
                                            @Nullable TagKey<Block> customPlaceBlockTag,
                                            int lightLevel,
                                            int colors,
                                            ResourceLocation growerId,
                                            List<ExtraItemOutput> extraItemOutputs,
                                            Lazy<List<Ingredient>> saplingIngredients,
                                            @Nullable ContainerOutput containerOutput) {
        String id = generateId(correspondingItem, translateKey);
        return new ResourceTreeConfig(
                id,
                correspondingItem,
                translateKey,
                automaticBasicRecipe,
                productOutput,
                treeStyle,
                oreType,
                fertilizeSetting,
                growthFrequency,
                Lazy.of(() -> customPlaceBlock == null ? Blocks.BARRIER : RegistriesUtils.getBlock(customPlaceBlock)),
                customPlaceBlockTag,
                lightLevel,
                colors,
                growerId,
                extraItemOutputs != null ? extraItemOutputs : List.of(),
                saplingIngredients != null ? saplingIngredients : Lazy.of(List::of),
                containerOutput);
    }

    public record Parsed(@Nullable String group, ResourceTreeConfig config) {}
}
