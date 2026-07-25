package com.maple.resource_farm.ResourceTree.builder;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.ResourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.ResourceTree.data.ResourceTree;
import com.maple.resource_farm.ResourceTree.data.dataMaps.ResourceTreeBaseType;
import com.maple.resource_farm.ResourceTree.data.dataMaps.ResourceTreeConfig;
import com.maple.resource_farm.ResourceTree.data.dataMaps.ResourceTreeExtraType;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.FoliageColor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mapleutillib.api.pack.DynamicAssets;
import com.mapleutillib.api.pack.PackPaths;
import com.mojang.serialization.JsonOps;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Runtime tree multipart rendering:
 * <ul>
 * <li>Base: vanilla / style block models (may use face {@code tintindex} 0, e.g. leaves foliage)</li>
 * <li>Overlay: dynamically generated ore/overlay models with {@code tintindex} 1 (resource color)</li>
 * <li>Element-level {@code light_emission} (0–15), not face fields; unrelated to block lightLevel</li>
 * </ul>
 * Block tint layers are registered via {@code blockTintSource(layer0, layer1)} so base and overlay
 * can use different colors (items already use composite/plain + tinted models).
 */
public class TreeModelRenderer {

    private static final Identifier BLANK_TEXTURE = ResourceFarm.id("block/void");
    private static final int ROTATE_90 = 90;
    /** Face tint layer for resource overlay models (layer 0 is base / foliage). */
    private static final int OVERLAY_TINT_INDEX = 1;

    private static final boolean STRIPPED_LOG_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog;
    private static final boolean WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood;
    private static final boolean STRIPPED_WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood;
    private static final boolean PLANKS_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks;
    private static final boolean CLUMP_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateClump();

    private static boolean isTreeConfigValid(ResourceTreeConfig treeConfig) {
        return treeConfig != null && treeConfig.treeType() != null && treeConfig.oreType() != null;
    }

    @Getter
    private static class ResourceTreeBundle {

        private final String treeId;
        private final String treeName;
        private final ResourceTreeBaseType treeType;
        private final ResourceTreeExtraType oreType;
        private final int color;
        /** 模型 face light_emission（0–15） */
        private final int lightEmission;

        private final Identifier saplingId;
        private final Identifier leavesId;
        private final Identifier logId;
        private final Identifier strippedLogId;
        private final Identifier woodId;
        private final Identifier strippedWoodId;
        private final Identifier planksId;
        private final Identifier resinId;
        private final Identifier fruitId;
        private final Identifier clumpId;

        public ResourceTreeBundle(String treeId, ResourceTree resourceTree) {
            var config = resourceTree.getResourceTreeConfig();
            this.treeId = treeId;
            this.treeName = config.id();
            this.treeType = config.treeType();
            this.oreType = config.oreType();
            this.color = config.color();
            this.lightEmission = Mth.clamp(config.lightLevel(), 0, 15);

            this.saplingId = resourceTree.getSapling().identifier();
            this.leavesId = resourceTree.getLeaves().identifier();
            this.logId = resourceTree.getLog().identifier();
            this.strippedLogId = STRIPPED_LOG_ENABLED ? resourceTree.getStrippedLog().identifier() : null;
            this.woodId = WOOD_ENABLED ? resourceTree.getWood().identifier() : null;
            this.strippedWoodId = STRIPPED_WOOD_ENABLED ? resourceTree.getStrippedWood().identifier() : null;
            this.planksId = PLANKS_ENABLED ? resourceTree.getPlanks().identifier() : null;
            this.resinId = resourceTree.getResin().identifier();
            this.fruitId = resourceTree.getFruit().identifier();
            this.clumpId = CLUMP_ENABLED ? resourceTree.getClump().identifier() : null;
        }

        public boolean isValid() {
            return treeId != null && !treeId.isBlank() && treeName != null && !treeName.isBlank() && treeType != null && oreType != null && saplingId != null && leavesId != null && logId != null;
        }

        public boolean isStrippedLogEnabled() {
            return STRIPPED_LOG_ENABLED && strippedLogId != null;
        }

        public boolean isWoodEnabled() {
            return WOOD_ENABLED && woodId != null;
        }

        public boolean isStrippedWoodEnabled() {
            return STRIPPED_WOOD_ENABLED && strippedWoodId != null;
        }

        public boolean isPlanksEnabled() {
            return PLANKS_ENABLED && planksId != null;
        }

        public boolean isClumpEnabled() {
            return CLUMP_ENABLED && clumpId != null;
        }
    }

    private static class BatchRegistrar {

        private final ConcurrentHashMap<Identifier, JsonObject> blockModels = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<Identifier, JsonObject> blockStates = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<Identifier, DualTextureItem> dualTextureItems = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<Identifier, CompositeBlockItem> compositeBlockItems = new ConcurrentHashMap<>();
        private final AtomicBoolean isCommitted = new AtomicBoolean(false);

        private record DualTextureItem(Identifier layer0, Identifier layer1, ItemTintSource[] tints) {}

        /**
         * 方块物品联合渲染：base 为原版/基底模型，tint 为资源着色层。
         * {@code baseTint} 非空时对底层应用物品着色（如原版树叶物品的固定叶片色）。
         */
        private record CompositeBlockItem(Identifier baseModel, Identifier tintModel, int color,
                                          @Nullable ItemTintSource baseTint) {}

        void addBlockModel(Identifier modelKey, JsonObject json) {
            if (modelKey != null && json != null) blockModels.put(modelKey, json);
        }

        void addBlockState(Identifier blockId, JsonObject json) {
            if (blockId != null && json != null) blockStates.put(blockId, json);
        }

        /** 默认：第 1 层（overlay）着色，第 0 层不着色。 */
        void addDualTextureItem(Identifier itemId, Identifier layer0, Identifier layer1, int color) {
            addDualTextureItem(itemId, layer0, layer1, overlayOnlyTints(color));
        }

        void addDualTextureItem(Identifier itemId, Identifier layer0, Identifier layer1, ItemTintSource[] tints) {
            if (itemId != null) dualTextureItems.put(itemId, new DualTextureItem(layer0, layer1, tints));
        }

        void addCompositeBlockItem(Identifier itemId, Identifier baseModel, Identifier tintModel, int color) {
            addCompositeBlockItem(itemId, baseModel, tintModel, color, null);
        }

        void addCompositeBlockItem(Identifier itemId, Identifier baseModel, Identifier tintModel, int color,
                                   @Nullable ItemTintSource baseTint) {
            if (itemId != null && baseModel != null && tintModel != null) {
                compositeBlockItems.put(itemId, new CompositeBlockItem(baseModel, tintModel, color, baseTint));
            }
        }

        void commit(DynamicAssets assets) {
            if (isCommitted.getAndSet(true)) return;
            blockModels.forEach((modelKey, json) -> assets.put(PackPaths.blockModelFile(modelKey), json));
            blockStates.forEach((blockId, json) -> assets.put(PackPaths.blockStateFile(blockId), json));

            // 分层着色物品：逻辑在 resource_farm 内，不依赖 MapleUtilLib multiLayer* API
            dualTextureItems.forEach((itemId, entry) -> writeMultiLayerTintedFlatItem(
                    assets,
                    itemId,
                    entry.tints(),
                    nullSafe(entry.layer0()),
                    nullSafe(entry.layer1())));

            compositeBlockItems.forEach((itemId, entry) -> {
                ItemModel.Unbaked base = entry.baseTint() != null ? ItemModelUtils.tintedModel(entry.baseModel(), entry.baseTint()) : ItemModelUtils.plainModel(entry.baseModel());
                ItemModel.Unbaked overlay = ItemModelUtils.tintedModel(
                        entry.tintModel(),
                        ItemModelUtils.constantTint(0xFFFFFFFF),
                        ItemModelUtils.constantTint(entry.color()));
                writeItemDefinition(assets, itemId, ItemModelUtils.composite(base, overlay));
            });

            clearAll();
        }

        void reset() {
            isCommitted.set(false);
            clearAll();
        }

        private void clearAll() {
            blockModels.clear();
            blockStates.clear();
            dualTextureItems.clear();
            compositeBlockItems.clear();
        }
    }

    private static final BatchRegistrar BATCH = new BatchRegistrar();

    // ===================== JSON / light_emission =====================

    private static Identifier nullSafe(Identifier texture) {
        return texture == null ? BLANK_TEXTURE : texture;
    }

    /** 仅第 1 层（overlay）着色，第 0 层保持原色。用于树苗/树脂/果实等。 */
    private static ItemTintSource[] overlayOnlyTints(int color) {
        return new ItemTintSource[] {
                ItemModelUtils.constantTint(0xFFFFFFFF),
                ItemModelUtils.constantTint(color)
        };
    }

    /** 仅第 0 层着色，第 1 层（overlay）保持原色。用于 crossover 碎块。 */
    private static ItemTintSource[] baseOnlyTints(int color) {
        return new ItemTintSource[] {
                ItemModelUtils.constantTint(color),
                ItemModelUtils.constantTint(0xFFFFFFFF)
        };
    }

    /**
     * 原版树叶物品使用的固定叶片色（与 {@code assets/minecraft/items/*_leaves.json} 一致）。
     * cherry / pale_oak 纹理自带颜色，返回 null 表示底层不着色。
     */
    @Nullable
    private static ItemTintSource vanillaLeavesItemTint(ResourceTreeBaseType treeType) {
        if (treeType == null || treeType.type() == null) {
            return ItemModelUtils.constantTint(FoliageColor.FOLIAGE_DEFAULT);
        }
        Integer color = switch (treeType.type()) {
            case "birch" -> FoliageColor.FOLIAGE_BIRCH;
            case "spruce" -> FoliageColor.FOLIAGE_EVERGREEN;
            case "mangrove" -> FoliageColor.FOLIAGE_MANGROVE;
            case "cherry" -> null;
            default -> FoliageColor.FOLIAGE_DEFAULT; // oak / jungle / acacia / dark_oak
        };
        return color == null ? null : ItemModelUtils.constantTint(color);
    }

    private static final Identifier PARENT_ITEM_GENERATED = Identifier.withDefaultNamespace("item/generated");

    /**
     * 本模组内实现的多层 flat 着色物品写入（不依赖 MapleUtilLib multiLayer*）。
     */
    private static void writeMultiLayerTintedFlatItem(DynamicAssets assets, Identifier itemId,
                                                      ItemTintSource[] tints, Identifier... layers) {
        Identifier modelRef = PackPaths.itemModelRef(itemId);
        JsonObject root = new JsonObject();
        root.addProperty("parent", PARENT_ITEM_GENERATED.toString());
        JsonObject textures = new JsonObject();
        if (layers != null) {
            for (int i = 0; i < layers.length; i++) {
                if (layers[i] != null) {
                    textures.addProperty("layer" + i, layers[i].toString());
                }
            }
        }
        root.add("textures", textures);
        assets.put(PackPaths.itemModelFile(itemId), root);

        ItemTintSource[] safe = tints == null ? new ItemTintSource[0] : tints;
        writeItemDefinition(assets, itemId, ItemModelUtils.tintedModel(modelRef, safe));
    }

    /** 写入 items 定义（支持跨命名空间）。 */
    private static void writeItemDefinition(DynamicAssets assets, Identifier itemId, ItemModel.Unbaked unbaked) {
        ClientItem clientItem = new ClientItem(unbaked, ClientItem.Properties.DEFAULT);
        ClientItem.CODEC
                .encodeStart(JsonOps.INSTANCE, clientItem)
                .resultOrPartial(err -> ResourceFarm.LOGGER.error(
                        "[ResourceFarm] Failed encoding ClientItem itemId={} err={}", itemId, err))
                .ifPresent(json -> assets.put(PackPaths.itemDefinitionFile(itemId), json));
    }

    private static JsonArray uvFull() {
        JsonArray uv = new JsonArray();
        uv.add(0);
        uv.add(0);
        uv.add(16);
        uv.add(16);
        return uv;
    }

    /**
     * Build a face for overlay models: texture + {@link #OVERLAY_TINT_INDEX}.
     * <p>
     * MC 26.1 {@code light_emission} belongs on the <strong>element</strong>, not the face.
     *
     * @param textureRef e.g. {@code #all} / {@code #side}
     * @param cullface   nullable
     */
    private static JsonObject face(String textureRef, String cullface) {
        JsonObject f = new JsonObject();
        f.add("uv", uvFull());
        f.addProperty("texture", textureRef);
        if (cullface != null) {
            f.addProperty("cullface", cullface);
        }
        // Layer 1 = resource color; layer 0 reserved for base (e.g. vanilla leaves foliage)
        f.addProperty("tintindex", OVERLAY_TINT_INDEX);
        return f;
    }

    private static JsonArray vec3(double x, double y, double z) {
        JsonArray a = new JsonArray();
        a.add(x);
        a.add(y);
        a.add(z);
        return a;
    }

    /** 将 light_emission 写到 element（0–15）；0 时省略。 */
    private static void applyElementLightEmission(JsonObject el, int light) {
        if (light > 0) {
            el.addProperty("light_emission", Mth.clamp(light, 0, 15));
        }
    }

    private static JsonObject cubeElement(String texAll, int light, boolean expanded) {
        JsonObject el = new JsonObject();
        if (expanded) {
            el.add("from", vec3(-0.01, -0.01, -0.01));
            el.add("to", vec3(16.01, 16.01, 16.01));
        } else {
            el.add("from", vec3(0, 0, 0));
            el.add("to", vec3(16, 16, 16));
        }
        el.addProperty("shade", false);
        applyElementLightEmission(el, light);
        JsonObject faces = new JsonObject();
        faces.add("down", face(texAll, "down"));
        faces.add("up", face(texAll, "up"));
        faces.add("north", face(texAll, "north"));
        faces.add("south", face(texAll, "south"));
        faces.add("west", face(texAll, "west"));
        faces.add("east", face(texAll, "east"));
        el.add("faces", faces);
        return el;
    }

    private static JsonObject columnElement(int light) {
        JsonObject el = new JsonObject();
        el.add("from", vec3(-0.01, -0.01, -0.01));
        el.add("to", vec3(16.01, 16.01, 16.01));
        el.addProperty("shade", false);
        applyElementLightEmission(el, light);
        JsonObject faces = new JsonObject();
        faces.add("down", face("#end", "down"));
        faces.add("up", face("#end", "up"));
        faces.add("north", face("#side", "north"));
        faces.add("south", face("#side", "south"));
        faces.add("west", face("#side", "west"));
        faces.add("east", face("#side", "east"));
        el.add("faces", faces);
        return el;
    }

    private static JsonObject crossElement(double x0, double y0, double z0, double x1, double y1, double z1,
                                           String axis, int light, String... faceNames) {
        JsonObject el = new JsonObject();
        el.add("from", vec3(x0, y0, z0));
        el.add("to", vec3(x1, y1, z1));
        JsonObject rot = new JsonObject();
        rot.add("origin", vec3(8, 8, 8));
        rot.addProperty("axis", axis);
        rot.addProperty("angle", 45);
        rot.addProperty("rescale", true);
        el.add("rotation", rot);
        el.addProperty("shade", false);
        applyElementLightEmission(el, light);
        JsonObject faces = new JsonObject();
        for (String name : faceNames) {
            faces.add(name, face("#cross", null));
        }
        el.add("faces", faces);
        return el;
    }

    private static JsonObject tintedCubeJson(Identifier texture, int light) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/block");
        root.addProperty("render_type", "minecraft:cutout_mipped");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", nullSafe(texture).toString());
        textures.addProperty("all", nullSafe(texture).toString());
        root.add("textures", textures);
        JsonArray elements = new JsonArray();
        elements.add(cubeElement("#all", light, true));
        root.add("elements", elements);
        return root;
    }

    private static JsonObject tintedLeavesJson(Identifier texture, int light) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/block");
        root.addProperty("render_type", "minecraft:cutout_mipped");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", nullSafe(texture).toString());
        textures.addProperty("all", nullSafe(texture).toString());
        root.add("textures", textures);
        JsonArray elements = new JsonArray();
        elements.add(cubeElement("#all", light, true));
        root.add("elements", elements);
        return root;
    }

    private static JsonObject tintedColumnJson(Identifier side, Identifier end, int light) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/block");
        root.addProperty("render_type", "minecraft:cutout_mipped");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", nullSafe(side).toString());
        textures.addProperty("side", nullSafe(side).toString());
        textures.addProperty("end", nullSafe(end).toString());
        root.add("textures", textures);
        JsonArray elements = new JsonArray();
        elements.add(columnElement(light));
        root.add("elements", elements);
        return root;
    }

    private static JsonObject tintedCrossJson(Identifier texture, int light) {
        JsonObject root = new JsonObject();
        root.addProperty("ambientocclusion", false);
        root.addProperty("render_type", "minecraft:cutout");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", nullSafe(texture).toString());
        textures.addProperty("cross", nullSafe(texture).toString());
        root.add("textures", textures);
        JsonArray elements = new JsonArray();
        elements.add(crossElement(0.8, 0, 8, 15.2, 16, 8, "y", light, "north", "south"));
        elements.add(crossElement(8, 0, 0.8, 8, 16, 15.2, "y", light, "west", "east"));
        root.add("elements", elements);
        return root;
    }

    // ===================== multipart helpers =====================

    private static Identifier modelRef(Identifier modelKey) {
        return PackPaths.blockModelRef(modelKey);
    }

    private static JsonObject applyModel(Identifier model) {
        JsonObject apply = new JsonObject();
        apply.addProperty("model", model.toString());
        return apply;
    }

    private static JsonObject applyModel(Identifier model, int x, Integer y) {
        JsonObject apply = applyModel(model);
        apply.addProperty("x", x);
        if (y != null) apply.addProperty("y", y);
        return apply;
    }

    private static JsonObject multipartPart(JsonObject apply) {
        JsonObject part = new JsonObject();
        part.add("apply", apply);
        return part;
    }

    private static JsonObject multipartPartWhen(String property, String value, JsonObject apply) {
        JsonObject when = new JsonObject();
        when.addProperty(property, value);
        JsonObject part = new JsonObject();
        part.add("when", when);
        part.add("apply", apply);
        return part;
    }

    private static JsonObject multipartState(JsonArray parts) {
        JsonObject root = new JsonObject();
        root.add("multipart", parts);
        return root;
    }

    private static void putSimpleMultipart(Identifier blockId, Identifier baseModel, Identifier tintModelRef) {
        JsonArray parts = new JsonArray();
        parts.add(multipartPart(applyModel(baseModel)));
        parts.add(multipartPart(applyModel(tintModelRef)));
        BATCH.addBlockState(blockId, multipartState(parts));
    }

    private static void putLogMultipart(Identifier blockId,
                                        Identifier baseVertical,
                                        Identifier baseHorizontal,
                                        Identifier tintColumnRef) {
        JsonArray parts = new JsonArray();
        parts.add(multipartPartWhen("axis", "y", applyModel(baseVertical)));
        parts.add(multipartPartWhen("axis", "y", applyModel(tintColumnRef)));
        parts.add(multipartPartWhen("axis", "z", applyModel(baseHorizontal, ROTATE_90, null)));
        parts.add(multipartPartWhen("axis", "z", applyModel(tintColumnRef, ROTATE_90, null)));
        parts.add(multipartPartWhen("axis", "x", applyModel(baseHorizontal, ROTATE_90, ROTATE_90)));
        parts.add(multipartPartWhen("axis", "x", applyModel(tintColumnRef, ROTATE_90, ROTATE_90)));
        BATCH.addBlockState(blockId, multipartState(parts));
    }

    // ===================== tint model builders =====================

    private static Identifier tintModelKey(Identifier blockId, String suffix) {
        return Identifier.fromNamespaceAndPath(blockId.getNamespace(), blockId.getPath() + suffix);
    }

    private static Identifier createTintedCube(Identifier blockId, Identifier texture, int light) {
        Identifier key = tintModelKey(blockId, "_tint");
        BATCH.addBlockModel(key, tintedCubeJson(texture, light));
        return modelRef(key);
    }

    private static Identifier createTintedLeaves(Identifier blockId, Identifier texture, int light) {
        Identifier key = tintModelKey(blockId, "_tint");
        BATCH.addBlockModel(key, tintedLeavesJson(texture, light));
        return modelRef(key);
    }

    private static Identifier createTintedCross(Identifier blockId, Identifier texture, int light) {
        Identifier key = tintModelKey(blockId, "_tint");
        BATCH.addBlockModel(key, tintedCrossJson(texture, light));
        return modelRef(key);
    }

    private static Identifier createTintedColumn(Identifier blockId, Identifier side, Identifier end, int light) {
        Identifier key = tintModelKey(blockId, "_tint");
        BATCH.addBlockModel(key, tintedColumnJson(side, end, light));
        return modelRef(key);
    }

    // ===================== register =====================

    private static void registerCubeLike(Identifier blockId, Identifier baseModel, Identifier tintTexture, int color, int light) {
        Identifier tintRef = createTintedCube(blockId, tintTexture, light);
        putSimpleMultipart(blockId, baseModel, tintRef);
        BATCH.addCompositeBlockItem(blockId, baseModel, tintRef, color);
    }

    private static void registerLeaves(Identifier leavesId, Identifier baseModel, Identifier overlay,
                                       int color, int light, ResourceTreeBaseType treeType) {
        Identifier tintRef = createTintedLeaves(leavesId, overlay, light);
        putSimpleMultipart(leavesId, baseModel, tintRef);
        // 底层原版树叶需与对应 *_leaves 物品相同的 constant 叶片色，否则库存中灰白
        BATCH.addCompositeBlockItem(leavesId, baseModel, tintRef, color, vanillaLeavesItemTint(treeType));
    }

    private static void registerLog(Identifier logId,
                                    Identifier baseVertical,
                                    Identifier baseHorizontal,
                                    Identifier oreSide,
                                    Identifier oreEnd,
                                    int color,
                                    int light) {
        Identifier tintRef = createTintedColumn(logId, oreSide, oreEnd, light);
        putLogMultipart(logId, baseVertical, baseHorizontal, tintRef);
        BATCH.addCompositeBlockItem(logId, baseVertical, tintRef, color);
    }

    private static void registerSapling(Identifier saplingId, ResourceTreeBaseType treeType, int color, int light) {
        Identifier tintRef = createTintedCross(saplingId, treeType.saplingOverlay(), light);
        putSimpleMultipart(saplingId, treeType.saplingBase(), tintRef);
        BATCH.addDualTextureItem(saplingId, treeType.saplingItemTexture(), treeType.saplingOverlay(), color);
    }

    private static void registerSapling(ResourceTreeBundle bundle) {
        registerSapling(bundle.getSaplingId(), bundle.getTreeType(), bundle.getColor(), bundle.getLightEmission());
    }

    private static void registerLeaves(ResourceTreeBundle bundle) {
        var t = bundle.getTreeType();
        registerLeaves(bundle.getLeavesId(), t.leavesBase(), t.leavesOverlay(),
                bundle.getColor(), bundle.getLightEmission(), t);
    }

    private static void registerLog(ResourceTreeBundle bundle) {
        var t = bundle.getTreeType();
        var o = bundle.getOreType();
        registerLog(bundle.getLogId(), t.logBase(), t.logHorizontalBase(),
                o.base(), o.center(), bundle.getColor(), bundle.getLightEmission());
    }

    private static void registerStrippedLog(ResourceTreeBundle bundle) {
        if (!bundle.isStrippedLogEnabled()) return;
        var t = bundle.getTreeType();
        var o = bundle.getOreType();
        registerLog(bundle.getStrippedLogId(), t.strippedLogBase(), t.strippedLogHorizontalBase(),
                o.base(), o.center(), bundle.getColor(), bundle.getLightEmission());
    }

    private static void registerWood(ResourceTreeBundle bundle) {
        if (!bundle.isWoodEnabled()) return;
        registerCubeLike(bundle.getWoodId(), bundle.getTreeType().woodBase(),
                bundle.getOreType().base(), bundle.getColor(), bundle.getLightEmission());
    }

    private static void registerStrippedWood(ResourceTreeBundle bundle) {
        if (!bundle.isStrippedWoodEnabled()) return;
        registerCubeLike(bundle.getStrippedWoodId(), bundle.getTreeType().strippedWoodBase(),
                bundle.getOreType().base(), bundle.getColor(), bundle.getLightEmission());
    }

    private static void registerPlanks(ResourceTreeBundle bundle) {
        if (!bundle.isPlanksEnabled()) return;
        registerCubeLike(bundle.getPlanksId(), bundle.getTreeType().planksBase(),
                bundle.getOreType().base(), bundle.getColor(), bundle.getLightEmission());
    }

    private static void registerResin(ResourceTreeBundle bundle) {
        var t = bundle.getTreeType();
        BATCH.addDualTextureItem(bundle.getResinId(), t.resin(), t.resinOverlay(), bundle.getColor());
    }

    private static void registerFruit(ResourceTreeBundle bundle) {
        var t = bundle.getTreeType();
        BATCH.addDualTextureItem(bundle.getFruitId(), t.fruit(), t.fruitOverlay(), bundle.getColor());
    }

    private static void registerClump(ResourceTreeBundle bundle) {
        if (!bundle.isClumpEnabled()) return;
        var t = bundle.getTreeType();
        // crossover 碎块：layer0=clump 需着色，layer1=clump_overlay 不着色
        BATCH.addDualTextureItem(bundle.getClumpId(), t.clump(), t.clumpOverlay(),
                baseOnlyTints(bundle.getColor()));
    }

    private static void buildAndRegisterTreeBlockBundle(ResourceTreeBundle bundle) {
        if (bundle == null || !bundle.isValid()) return;
        registerSapling(bundle);
        registerLeaves(bundle);
        registerLog(bundle);
        registerStrippedLog(bundle);
        registerWood(bundle);
        registerStrippedWood(bundle);
        registerPlanks(bundle);
        registerResin(bundle);
        registerFruit(bundle);
        registerClump(bundle);
    }

    public static void clearCache() {
        BATCH.reset();
    }

    public static void reinitModels(DynamicAssets assets) {
        clearCache();

        for (ResourceTree resourceTree : ResourceTreeAccessManagement.ResourceTreeMap.values()) {
            if (resourceTree == null) continue;
            var treeConfig = resourceTree.getResourceTreeConfig();
            if (!isTreeConfigValid(treeConfig)) continue;
            try {
                var blockBundle = new ResourceTreeBundle(treeConfig.id(), resourceTree);
                if (blockBundle.isValid()) {
                    buildAndRegisterTreeBlockBundle(blockBundle);
                }
            } catch (Exception e) {
                ResourceFarm.LOGGER.error(
                        "[ResourceFarm] Failed building tree model treeId={} resourceTree={}",
                        treeConfig.id(), resourceTree, e);
            }
        }

        BATCH.commit(assets);
    }
}
