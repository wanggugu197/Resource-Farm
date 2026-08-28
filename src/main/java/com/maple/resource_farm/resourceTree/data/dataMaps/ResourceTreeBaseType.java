package com.maple.resource_farm.resourceTree.data.dataMaps;

import com.maple.resource_farm.ResourceFarm;

import net.minecraft.resources.ResourceLocation;

import com.mapleutillib.utils.RLUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 树木样式定义。
 */
public record ResourceTreeBaseType(
                                   String type,
                                   // ---- 基底模型（models/block/...）----
                                   ResourceLocation saplingBase,
                                   ResourceLocation leavesBase,
                                   ResourceLocation logBase,
                                   ResourceLocation logHorizontalBase,
                                   ResourceLocation strippedLogBase,
                                   ResourceLocation strippedLogHorizontalBase,
                                   ResourceLocation woodBase,
                                   ResourceLocation strippedWoodBase,
                                   ResourceLocation planksBase,
                                   // ---- 着色 / 物品叠加纹理 ----
                                   ResourceLocation saplingOverlay,
                                   ResourceLocation leavesOverlay,
                                   ResourceLocation resin,
                                   ResourceLocation resinOverlay,
                                   ResourceLocation fruit,
                                   ResourceLocation fruitOverlay,
                                   ResourceLocation clump,
                                   ResourceLocation clumpOverlay,
                                   // ---- 翻译键 ----
                                   String saplingTranslateKey,
                                   String leavesTranslateKey,
                                   String logTranslateKey,
                                   String strippedLogTranslateKey,
                                   String woodTranslateKey,
                                   String strippedWoodTranslateKey,
                                   String planksTranslateKey,
                                   String resinTranslateKey,
                                   String fruitTranslateKey,
                                   String clumpTranslateKey) {

    /** JSON 嵌套：基底模型 */
    public record Models(
                         ResourceLocation saplingBase,
                         ResourceLocation leavesBase,
                         ResourceLocation logBase,
                         ResourceLocation logHorizontalBase,
                         ResourceLocation strippedLogBase,
                         ResourceLocation strippedLogHorizontalBase,
                         ResourceLocation woodBase,
                         ResourceLocation strippedWoodBase,
                         ResourceLocation planksBase) {

        public static final Codec<Models> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("sapling_base", RLUtils.mc("block/oak_sapling"))
                        .forGetter(Models::saplingBase),
                ResourceLocation.CODEC.optionalFieldOf("leaves_base", RLUtils.mc("block/oak_leaves"))
                        .forGetter(Models::leavesBase),
                ResourceLocation.CODEC.optionalFieldOf("log_base", RLUtils.mc("block/oak_log")).forGetter(Models::logBase),
                ResourceLocation.CODEC.optionalFieldOf("log_horizontal_base", RLUtils.mc("block/oak_log_horizontal"))
                        .forGetter(Models::logHorizontalBase),
                ResourceLocation.CODEC.optionalFieldOf("stripped_log_base", RLUtils.mc("block/stripped_oak_log"))
                        .forGetter(Models::strippedLogBase),
                ResourceLocation.CODEC.optionalFieldOf("stripped_log_horizontal_base", RLUtils.mc("block/stripped_oak_log_horizontal"))
                        .forGetter(Models::strippedLogHorizontalBase),
                ResourceLocation.CODEC.optionalFieldOf("wood_base", RLUtils.mc("block/oak_wood")).forGetter(Models::woodBase),
                ResourceLocation.CODEC.optionalFieldOf("stripped_wood_base", RLUtils.mc("block/stripped_oak_wood"))
                        .forGetter(Models::strippedWoodBase),
                ResourceLocation.CODEC.optionalFieldOf("planks_base", RLUtils.mc("block/oak_planks"))
                        .forGetter(Models::planksBase))
                .apply(instance, Models::new));
    }

    /** JSON 嵌套：叠加 / 物品纹理 */
    public record Overlays(
                           ResourceLocation saplingOverlay,
                           ResourceLocation leavesOverlay,
                           ResourceLocation resin,
                           ResourceLocation resinOverlay,
                           ResourceLocation fruit,
                           ResourceLocation fruitOverlay,
                           ResourceLocation clump,
                           ResourceLocation clumpOverlay) {

        public static final Codec<Overlays> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("sapling_overlay", ResourceFarm.id("block/tree/sapling/oak_sapling_overlay"))
                        .forGetter(Overlays::saplingOverlay),
                ResourceLocation.CODEC.optionalFieldOf("leaves_overlay", ResourceFarm.id("block/tree/leaves/oak_leaves_overlay"))
                        .forGetter(Overlays::leavesOverlay),
                ResourceLocation.CODEC.optionalFieldOf("resin", ResourceFarm.id("item/resin/base_resin"))
                        .forGetter(Overlays::resin),
                ResourceLocation.CODEC.optionalFieldOf("resin_overlay", ResourceFarm.id("item/resin/base_resin"))
                        .forGetter(Overlays::resinOverlay),
                ResourceLocation.CODEC.optionalFieldOf("fruit", ResourceFarm.id("item/fruit/base_fruit"))
                        .forGetter(Overlays::fruit),
                ResourceLocation.CODEC.optionalFieldOf("fruit_overlay", ResourceFarm.id("item/fruit/base_fruit_overlay"))
                        .forGetter(Overlays::fruitOverlay),
                ResourceLocation.CODEC.optionalFieldOf("clump", ResourceFarm.id("item/crossover/clump"))
                        .forGetter(Overlays::clump),
                ResourceLocation.CODEC.optionalFieldOf("clump_overlay", ResourceFarm.id("item/crossover/clump_overlay"))
                        .forGetter(Overlays::clumpOverlay))
                .apply(instance, Overlays::new));
    }

    /** JSON 嵌套：翻译键 */
    public record TranslateKeys(
                                String sapling,
                                String leaves,
                                String log,
                                String strippedLog,
                                String wood,
                                String strippedWood,
                                String planks,
                                String resin,
                                String fruit,
                                String clump) {

        public static final Codec<TranslateKeys> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("sapling", "block.resource_farm.tree.sapling")
                        .forGetter(TranslateKeys::sapling),
                Codec.STRING.optionalFieldOf("leaves", "block.resource_farm.tree.leaves").forGetter(TranslateKeys::leaves),
                Codec.STRING.optionalFieldOf("log", "block.resource_farm.tree.log").forGetter(TranslateKeys::log),
                Codec.STRING.optionalFieldOf("stripped_log", "block.resource_farm.tree.stripped_log")
                        .forGetter(TranslateKeys::strippedLog),
                Codec.STRING.optionalFieldOf("wood", "block.resource_farm.tree.wood").forGetter(TranslateKeys::wood),
                Codec.STRING.optionalFieldOf("stripped_wood", "block.resource_farm.tree.stripped_wood")
                        .forGetter(TranslateKeys::strippedWood),
                Codec.STRING.optionalFieldOf("planks", "block.resource_farm.tree.planks").forGetter(TranslateKeys::planks),
                Codec.STRING.optionalFieldOf("resin", "item.resource_farm.tree.resin").forGetter(TranslateKeys::resin),
                Codec.STRING.optionalFieldOf("fruit", "item.resource_farm.tree.fruit").forGetter(TranslateKeys::fruit),
                Codec.STRING.optionalFieldOf("clump", "item.resource_farm.tree.clump")
                        .forGetter(TranslateKeys::clump))
                .apply(instance, TranslateKeys::new));
    }

    public static final Codec<ResourceTreeBaseType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("type", "oak").forGetter(ResourceTreeBaseType::type),
            Models.CODEC.optionalFieldOf("models", defaultModels()).forGetter(t -> new Models(
                    t.saplingBase(), t.leavesBase(), t.logBase(), t.logHorizontalBase(),
                    t.strippedLogBase(), t.strippedLogHorizontalBase(),
                    t.woodBase(), t.strippedWoodBase(), t.planksBase())),
            Overlays.CODEC.optionalFieldOf("overlays", defaultOverlays()).forGetter(t -> new Overlays(
                    t.saplingOverlay(), t.leavesOverlay(),
                    t.resin(), t.resinOverlay(),
                    t.fruit(), t.fruitOverlay(),
                    t.clump(), t.clumpOverlay())),
            TranslateKeys.CODEC.optionalFieldOf("translate_keys", defaultTranslateKeys())
                    .forGetter(t -> new TranslateKeys(
                            t.saplingTranslateKey(), t.leavesTranslateKey(), t.logTranslateKey(),
                            t.strippedLogTranslateKey(), t.woodTranslateKey(), t.strippedWoodTranslateKey(),
                            t.planksTranslateKey(), t.resinTranslateKey(), t.fruitTranslateKey(), t.clumpTranslateKey())))
            .apply(instance, (type, models, overlays, keys) -> new ResourceTreeBaseType(
                    type,
                    models.saplingBase(), models.leavesBase(),
                    models.logBase(), models.logHorizontalBase(),
                    models.strippedLogBase(), models.strippedLogHorizontalBase(),
                    models.woodBase(), models.strippedWoodBase(), models.planksBase(),
                    overlays.saplingOverlay(), overlays.leavesOverlay(),
                    overlays.resin(), overlays.resinOverlay(),
                    overlays.fruit(), overlays.fruitOverlay(),
                    overlays.clump(), overlays.clumpOverlay(),
                    keys.sapling(), keys.leaves(), keys.log(), keys.strippedLog(),
                    keys.wood(), keys.strippedWood(), keys.planks(),
                    keys.resin(), keys.fruit(), keys.clump())));

    private static Models defaultModels() {
        return new Models(
                RLUtils.mc("block/oak_sapling"),
                RLUtils.mc("block/oak_leaves"),
                RLUtils.mc("block/oak_log"),
                RLUtils.mc("block/oak_log_horizontal"),
                RLUtils.mc("block/stripped_oak_log"),
                RLUtils.mc("block/stripped_oak_log_horizontal"),
                RLUtils.mc("block/oak_wood"),
                RLUtils.mc("block/stripped_oak_wood"),
                RLUtils.mc("block/oak_planks"));
    }

    private static Overlays defaultOverlays() {
        return new Overlays(
                ResourceFarm.id("block/tree/sapling/oak_sapling_overlay"),
                ResourceFarm.id("block/tree/leaves/oak_leaves_overlay"),
                ResourceFarm.id("item/resin/base_resin"),
                ResourceFarm.id("item/resin/base_resin"),
                ResourceFarm.id("item/fruit/base_fruit"),
                ResourceFarm.id("item/fruit/base_fruit_overlay"),
                ResourceFarm.id("item/crossover/clump"),
                ResourceFarm.id("item/crossover/clump_overlay"));
    }

    private static TranslateKeys defaultTranslateKeys() {
        return new TranslateKeys(
                "block.resource_farm.tree.sapling",
                "block.resource_farm.tree.leaves",
                "block.resource_farm.tree.log",
                "block.resource_farm.tree.stripped_log",
                "block.resource_farm.tree.wood",
                "block.resource_farm.tree.stripped_wood",
                "block.resource_farm.tree.planks",
                "item.resource_farm.tree.resin",
                "item.resource_farm.tree.fruit",
                "item.resource_farm.tree.clump");
    }

    public static final ResourceTreeBaseType DEFAULT = new ResourceTreeBaseType(
            "oak",
            RLUtils.mc("block/oak_sapling"),
            RLUtils.mc("block/oak_leaves"),
            RLUtils.mc("block/oak_log"),
            RLUtils.mc("block/oak_log_horizontal"),
            RLUtils.mc("block/stripped_oak_log"),
            RLUtils.mc("block/stripped_oak_log_horizontal"),
            RLUtils.mc("block/oak_wood"),
            RLUtils.mc("block/stripped_oak_wood"),
            RLUtils.mc("block/oak_planks"),
            ResourceFarm.id("block/tree/sapling/oak_sapling_overlay"),
            ResourceFarm.id("block/tree/leaves/oak_leaves_overlay"),
            ResourceFarm.id("item/resin/base_resin"),
            ResourceFarm.id("item/resin/base_resin"),
            ResourceFarm.id("item/fruit/base_fruit"),
            ResourceFarm.id("item/fruit/base_fruit_overlay"),
            ResourceFarm.id("item/crossover/clump"),
            ResourceFarm.id("item/crossover/clump_overlay"),
            "block.resource_farm.tree.sapling",
            "block.resource_farm.tree.leaves",
            "block.resource_farm.tree.log",
            "block.resource_farm.tree.stripped_log",
            "block.resource_farm.tree.wood",
            "block.resource_farm.tree.stripped_wood",
            "block.resource_farm.tree.planks",
            "item.resource_farm.tree.resin",
            "item.resource_farm.tree.fruit",
            "item.resource_farm.tree.clump");

    /** 树苗物品底层纹理：与基底模型同路径约定。 */
    public ResourceLocation saplingItemTexture() {
        return saplingBase;
    }
}
