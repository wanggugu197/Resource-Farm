package com.maple.resource_farm.resourceTree.builder;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.resourceTree.data.ResourceTree;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeBaseType;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeConfig;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeExtraType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.FoliageColor;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mapleutillib.api.pack.DynamicAssets;
import com.mapleutillib.api.pack.PackPaths;
import com.mapleutillib.utils.RLUtils;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Runtime tree multipart rendering:
 * <ul>
 * <li>Base: vanilla / style block models (may use face {@code tintindex} 0, e.g. leaves foliage)</li>
 * <li>Overlay: dynamically generated ore/overlay models with {@code tintindex} 1 (resource color)</li>
 * <li>Face-level {@code neoforge_data} block/sky light (0–15), unrelated to block lightLevel</li>
 * </ul>
 * Block tint layers are registered via registrylib {@code blockTintSource(layer0, layer1)} so base
 * and overlay can use different colors (items use plain generated/parent JSON models + runtime
 * {@link ItemColor} registered through MapleUtilLib {@link ClientInit#registerItemTint}).
 */
public class TreeModelRenderer {

    private static final ResourceLocation BLANK_TEXTURE = ResourceFarm.id("block/void");
    private static final int ROTATE_90 = 90;

    private static final boolean STRIPPED_LOG_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedLog;
    private static final boolean WOOD_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateWood;
    private static final boolean STRIPPED_WOOD_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedWood;
    private static final boolean PLANKS_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generatePlanks;
    private static final boolean CLUMP_ENABLED = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateClump();

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

        private final ResourceLocation saplingId;
        private final ResourceLocation leavesId;
        private final ResourceLocation logId;
        private final ResourceLocation strippedLogId;
        private final ResourceLocation woodId;
        private final ResourceLocation strippedWoodId;
        private final ResourceLocation planksId;
        private final ResourceLocation resinId;
        private final ResourceLocation fruitId;
        private final ResourceLocation clumpId;

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

    /**
     * 方块物品联合渲染：model 为双层方块模型（base + overlay 两层几何，tintindex 0/1）。
     * {@code baseTint} 非空时对底层应用物品着色（如原版树叶物品的固定叶片色）。
     */
    private record CompositeBlockItem(ResourceLocation model, int color,
                                      @Nullable Integer baseTint) {}

    private static class BatchRegistrar {

        private final ConcurrentHashMap<ResourceLocation, JsonObject> blockModels = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<ResourceLocation, JsonObject> blockStates = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<ResourceLocation, DualTextureItem> dualTextureItems = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<ResourceLocation, CompositeBlockItem> compositeBlockItems = new ConcurrentHashMap<>();
        private final AtomicBoolean isCommitted = new AtomicBoolean(false);

        /** 各层颜色（ARGB）；0xFFFFFFFF = 不着色。1.21.1 无 ItemTintSource，着色由运行时 ItemColor 驱动。 */
        private record DualTextureItem(ResourceLocation layer0, ResourceLocation layer1, int[] tints) {}

        void addBlockModel(ResourceLocation modelKey, JsonObject json) {
            if (modelKey != null && json != null) blockModels.put(modelKey, json);
        }

        void addBlockState(ResourceLocation blockId, JsonObject json) {
            if (blockId != null && json != null) blockStates.put(blockId, json);
        }

        /** 默认：第 1 层（overlay）着色，第 0 层不着色。 */
        void addDualTextureItem(ResourceLocation itemId, ResourceLocation layer0, ResourceLocation layer1, int color) {
            addDualTextureItem(itemId, layer0, layer1, overlayOnlyTints(color));
        }

        void addDualTextureItem(ResourceLocation itemId, ResourceLocation layer0, ResourceLocation layer1, int[] tints) {
            if (itemId != null) dualTextureItems.put(itemId, new DualTextureItem(layer0, layer1, tints));
        }

        void addCompositeBlockItem(ResourceLocation itemId, ResourceLocation model, int color,
                                   @Nullable Integer baseTint) {
            if (itemId != null && model != null) {
                compositeBlockItems.put(itemId, new CompositeBlockItem(model, color, baseTint));
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

            compositeBlockItems.forEach((itemId, entry) -> writeCompositeBlockItem(assets, itemId, entry));

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

    // ===================== 模型 JSON 解析（读取原版/静态 base 模型链） =====================

    /** 模型 JSON 缓存（模型文件 → 解析后的 JsonObject）。 */
    private static final Map<ResourceLocation, JsonObject> MODEL_JSON_CACHE = new ConcurrentHashMap<>();

    /** 读取 {@code models/<path>.json}；路径缺 {@code block/} 前缀时自动补。失败返回 null。 */
    @Nullable
    private static JsonObject readModelJson(ResourceLocation model) {
        ResourceLocation file = model.getPath().startsWith("block/") || model.getPath().startsWith("item/") ? model : ResourceLocation.fromNamespaceAndPath(model.getNamespace(), "block/" + model.getPath());
        return MODEL_JSON_CACHE.computeIfAbsent(file, key -> {
            try {
                ResourceLocation path = ResourceLocation.fromNamespaceAndPath(
                        key.getNamespace(), "models/" + key.getPath() + ".json");
                Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(path);
                if (res.isEmpty()) {
                    return null;
                }
                try (InputStream in = res.get().open()) {
                    String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                    return JsonParser.parseString(text).getAsJsonObject();
                }
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * 沿 parent 链收集模型的最终纹理映射（子模型优先，解析 {@code #引用}）。
     * 返回具体纹理（如 {@code minecraft:block/oak_log}）或 null。
     */
    @Nullable
    private static String resolveModelTexture(ResourceLocation model, String key) {
        if (model == null) {
            return null;
        }
        JsonObject json = readModelJson(model);
        if (json == null) {
            return null;
        }
        Map<String, String> textures = new LinkedHashMap<>();
        collectTextures(json, textures, 0);
        return resolveTextureRef(textures, key, 0);
    }

    private static void collectTextures(JsonObject json, Map<String, String> out, int depth) {
        if (json == null || depth > 8) {
            return;
        }
        if (json.has("parent") && json.get("parent").isJsonPrimitive()) {
            JsonObject parent = readModelJson(ResourceLocation.parse(json.get("parent").getAsString()));
            if (parent != null) {
                collectTextures(parent, out, depth + 1);
            }
        }
        if (json.has("textures") && json.get("textures").isJsonObject()) {
            json.getAsJsonObject("textures").entrySet().forEach(e -> out.put(e.getKey(), e.getValue().getAsString()));
        }
    }

    @Nullable
    private static String resolveTextureRef(Map<String, String> textures, String key, int depth) {
        if (depth > 8) {
            return null;
        }
        String value = textures.get(key);
        if (value == null) {
            return null;
        }
        if (value.startsWith("#")) {
            return resolveTextureRef(textures, value.substring(1), depth + 1);
        }
        return value;
    }

    // ===================== JSON / light_emission =====================

    private static ResourceLocation nullSafe(ResourceLocation texture) {
        return texture == null ? BLANK_TEXTURE : texture;
    }

    /** 仅第 1 层（overlay）着色，第 0 层保持原色。用于树苗/树脂/果实等。 */
    private static int[] overlayOnlyTints(int color) {
        return new int[] { 0xFFFFFFFF, color };
    }

    /** 仅第 0 层着色，第 1 层（overlay）保持原色。用于 crossover 碎块。 */
    private static int[] baseOnlyTints(int color) {
        return new int[] { color, 0xFFFFFFFF };
    }

    /**
     * 原版树叶物品使用的固定叶片色（与 {@code assets/minecraft/items/*_leaves.json} 一致）。
     * cherry / pale_oak 纹理自带颜色，返回 null 表示底层不着色。
     */
    @Nullable
    private static Integer vanillaLeavesItemTint(ResourceTreeBaseType treeType) {
        if (treeType == null || treeType.type() == null) {
            return FoliageColor.getDefaultColor();
        }
        Integer color = switch (treeType.type()) {
            case "birch" -> FoliageColor.getBirchColor();
            case "spruce" -> FoliageColor.getEvergreenColor();
            case "mangrove" -> FoliageColor.getMangroveColor();
            case "cherry" -> null;
            default -> FoliageColor.getDefaultColor(); // oak / jungle / acacia / dark_oak
        };
        return color;
    }

    private static final ResourceLocation PARENT_ITEM_GENERATED = ResourceLocation.withDefaultNamespace("item/generated");

    /**
     * 本模组内实现的多层 flat 着色物品写入（不依赖 MapleUtilLib multiLayer*）。
     * 1.21.1：item/generated 第 N 层自动带 tintindex N，着色完全由运行时
     * {@link ItemColor} 提供（见 {@link #applyItemTints}，RegisterColorHandlersEvent.Item 时注册）。
     */
    private static void writeMultiLayerTintedFlatItem(DynamicAssets assets, ResourceLocation itemId,
                                                      int[] tints, ResourceLocation... layers) {
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
    }

    /**
     * 方块物品模型：parent 指向双层方块模型（base + overlay 两层几何，tintindex 0/1），
     * 颜色由运行时 {@link ItemColor} 按 tintindex 提供（见 {@link #applyItemTints}）。
     */
    private static void writeCompositeBlockItem(DynamicAssets assets, ResourceLocation itemId, CompositeBlockItem entry) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", entry.model().toString());
        assets.put(PackPaths.itemModelFile(itemId), model);
    }

    // ===================== 物品运行时着色（RegisterColorHandlersEvent.Item 时调用） =====================

    private static ItemColor overlayItemTint(int color) {
        return (stack, tintIndex) -> tintIndex == 1 ? color : 0xFFFFFFFF;
    }

    private static void registerItemTintEvent(RegisterColorHandlersEvent.Item event, ResourceLocation itemId, ItemColor color) {
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null || item == Items.AIR) {
            return;
        }
        event.register(color, item);
    }

    /**
     * 为所有资源树物品注册运行时着色。由客户端 {@link com.maple.resource_farm.client.ClientInit}
     * 在 {@link RegisterColorHandlersEvent.Item} 时调用（1.21.1 tint 必须在该事件内注册；
     * 26.1.2 的模型内嵌 ItemTintSource 机制已不存在）。
     * <p>
     * 着色规则与模型生成一致：flat 物品 layer0/layer1、方块物品 base(tintindex 0)/overlay(tintindex 1)。
     */
    public static void applyItemTints(RegisterColorHandlersEvent.Item event) {
        for (ResourceTree resourceTree : ResourceTreeAccessManagement.ResourceTreeMap.values()) {
            if (resourceTree == null) {
                continue;
            }
            var treeConfig = resourceTree.getResourceTreeConfig();
            if (!isTreeConfigValid(treeConfig)) {
                continue;
            }
            ResourceTreeBundle bundle = new ResourceTreeBundle(treeConfig.id(), resourceTree);
            if (!bundle.isValid()) {
                continue;
            }
            int color = bundle.getColor();
            ResourceTreeBaseType t = bundle.getTreeType();
            // 树苗（flat 双层）：layer0 原色，layer1 资源色
            registerItemTintEvent(event, bundle.getSaplingId(), overlayItemTint(color));
            // 树叶（双层方块模型）：layer0 叶片固定色（或原色），layer1 资源色
            Integer leavesBase = vanillaLeavesItemTint(t);
            registerItemTintEvent(event, bundle.getLeavesId(),
                    (stack, tintIndex) -> tintIndex == 1 ? color : (leavesBase != null ? leavesBase : 0xFFFFFFFF));
            // 原木/去皮原木/木材/去皮木材/木板：layer0 原色，layer1 资源色
            registerItemTintEvent(event, bundle.getLogId(), overlayItemTint(color));
            if (bundle.isStrippedLogEnabled()) {
                registerItemTintEvent(event, bundle.getStrippedLogId(), overlayItemTint(color));
            }
            if (bundle.isWoodEnabled()) {
                registerItemTintEvent(event, bundle.getWoodId(), overlayItemTint(color));
            }
            if (bundle.isStrippedWoodEnabled()) {
                registerItemTintEvent(event, bundle.getStrippedWoodId(), overlayItemTint(color));
            }
            if (bundle.isPlanksEnabled()) {
                registerItemTintEvent(event, bundle.getPlanksId(), overlayItemTint(color));
            }
            // 树脂/果实（flat 双层）：layer1 资源色
            registerItemTintEvent(event, bundle.getResinId(), overlayItemTint(color));
            registerItemTintEvent(event, bundle.getFruitId(), overlayItemTint(color));
            // 碎块（flat 双层，crossover）：layer0 资源色，layer1 原色
            if (bundle.isClumpEnabled()) {
                registerItemTintEvent(event, bundle.getClumpId(),
                        (stack, tintIndex) -> tintIndex == 0 ? color : 0xFFFFFFFF);
            }
        }
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
     * Build a face: texture + optional tint index + optional cullface.
     * <p>
     * 1.21.1：元素级 {@code light_emission} 已移除，发光改走 NeoForge 每面
     * {@code neoforge_data} 的 block/sky light（0–15）。
     *
     * @param textureRef e.g. {@code #all} / {@code #side}
     * @param cullface   nullable
     * @param light      face 发光亮度（0–15），0 表示不发光
     * @param tintIndex  着色层；-1 表示不着色
     */
    private static JsonObject face(String textureRef, String cullface, int light, int tintIndex) {
        JsonObject f = new JsonObject();
        f.add("uv", uvFull());
        f.addProperty("texture", textureRef);
        if (cullface != null) {
            f.addProperty("cullface", cullface);
        }
        if (tintIndex >= 0) {
            f.addProperty("tintindex", tintIndex);
        }
        applyFaceLightEmission(f, light);
        return f;
    }

    /** 将发光写到 face 的 {@code neoforge_data}（block_light/sky_light，0–15）；0 时省略。 */
    private static void applyFaceLightEmission(JsonObject face, int light) {
        if (light > 0) {
            JsonObject nfd = new JsonObject();
            int clamped = Mth.clamp(light, 0, 15);
            nfd.addProperty("block_light", clamped);
            nfd.addProperty("sky_light", clamped);
            face.add("neoforge_data", nfd);
        }
    }

    private static JsonArray vec3(double x, double y, double z) {
        JsonArray a = new JsonArray();
        a.add(x);
        a.add(y);
        a.add(z);
        return a;
    }

    /**
     * 单个立方体元素。
     *
     * @param texAll    面纹理引用（如 {@code #base} / {@code #overlay}）
     * @param light     发光
     * @param expanded  true 时外扩 -0.01~16.01（覆盖层防 z-fighting）
     * @param tintIndex 着色层（-1 不着色）
     * @param shade     是否受环境光遮蔽（base 层 true，overlay 层 false）
     */
    private static JsonObject cubeElement(String texAll, int light, boolean expanded, int tintIndex, boolean shade) {
        JsonObject el = new JsonObject();
        if (expanded) {
            el.add("from", vec3(-0.01, -0.01, -0.01));
            el.add("to", vec3(16.01, 16.01, 16.01));
        } else {
            el.add("from", vec3(0, 0, 0));
            el.add("to", vec3(16, 16, 16));
        }
        el.addProperty("shade", shade);
        JsonObject faces = new JsonObject();
        faces.add("down", face(texAll, "down", light, tintIndex));
        faces.add("up", face(texAll, "up", light, tintIndex));
        faces.add("north", face(texAll, "north", light, tintIndex));
        faces.add("south", face(texAll, "south", light, tintIndex));
        faces.add("west", face(texAll, "west", light, tintIndex));
        faces.add("east", face(texAll, "east", light, tintIndex));
        el.add("faces", faces);
        return el;
    }

    private static JsonObject columnElement(int light, int tintIndex) {
        JsonObject el = new JsonObject();
        el.add("from", vec3(-0.01, -0.01, -0.01));
        el.add("to", vec3(16.01, 16.01, 16.01));
        el.addProperty("shade", false);
        JsonObject faces = new JsonObject();
        faces.add("down", face("#end", "down", light, tintIndex));
        faces.add("up", face("#end", "up", light, tintIndex));
        faces.add("north", face("#side", "north", light, tintIndex));
        faces.add("south", face("#side", "south", light, tintIndex));
        faces.add("west", face("#side", "west", light, tintIndex));
        faces.add("east", face("#side", "east", light, tintIndex));
        el.add("faces", faces);
        return el;
    }

    private static JsonObject crossElement(double x0, double y0, double z0, double x1, double y1, double z1,
                                           String axis, int light, int tintIndex, boolean expanded, String textureRef,
                                           String... faceNames) {
        JsonObject el = new JsonObject();
        double pad = expanded ? 0.01 : 0;
        el.add("from", vec3(x0 - pad, y0 - pad, z0 - pad));
        el.add("to", vec3(x1 + pad, y1 + pad, z1 + pad));
        JsonObject rot = new JsonObject();
        rot.add("origin", vec3(8, 8, 8));
        rot.addProperty("axis", axis);
        rot.addProperty("angle", 45);
        rot.addProperty("rescale", true);
        el.add("rotation", rot);
        el.addProperty("shade", false);
        JsonObject faces = new JsonObject();
        for (String name : faceNames) {
            faces.add(name, face(textureRef, null, light, tintIndex));
        }
        el.add("faces", faces);
        return el;
    }

    /**
     * 双层立方体模型：base 层（tintindex 0）+ overlay 层（tintindex 1，与 base 同位置重叠，
     * 靠半透明 overlay 纹理混合——与 26.1.2 的 cube_multi_layer/all 模板一致）。用于木材/木板。
     */
    private static JsonObject dualLayerCubeJson(String baseTex, String overlayTex, int light) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/block");
        root.addProperty("render_type", "minecraft:translucent");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", baseTex);
        textures.addProperty("base", baseTex);
        textures.addProperty("overlay", overlayTex);
        root.add("textures", textures);
        JsonArray elements = new JsonArray();
        elements.add(cubeElement("#base", 0, false, 0, true));
        elements.add(cubeElement("#overlay", light, false, 1, false));
        root.add("elements", elements);
        return root;
    }

    /** 双层树叶模型：base（tintindex 0，叶片纹理）+ overlay（tintindex 1），cutout 渲染（与 26.1.2 leaves 模板一致）。 */
    private static JsonObject dualLayerLeavesJson(String baseTex, String overlayTex, int light) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/block");
        root.addProperty("render_type", "minecraft:cutout");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", baseTex);
        textures.addProperty("base", baseTex);
        textures.addProperty("overlay", overlayTex);
        root.add("textures", textures);
        JsonArray elements = new JsonArray();
        elements.add(cubeElement("#base", 0, false, 0, true));
        elements.add(cubeElement("#overlay", light, false, 1, false));
        root.add("elements", elements);
        return root;
    }

    /** 双层 cross 模型（树苗等）：base（tintindex 0）+ overlay（tintindex 1，同位置重叠）。 */
    private static JsonObject dualLayerCrossJson(String baseTex, String overlayTex, int light) {
        JsonObject root = new JsonObject();
        root.addProperty("ambientocclusion", false);
        root.addProperty("render_type", "minecraft:cutout");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", baseTex);
        textures.addProperty("cross", baseTex);
        textures.addProperty("overlay", overlayTex);
        root.add("textures", textures);
        JsonArray elements = new JsonArray();
        elements.add(crossElement(0.8, 0, 8, 15.2, 16, 8, "y", 0, 0, false, "#cross", "north", "south"));
        elements.add(crossElement(8, 0, 0.8, 8, 16, 15.2, "y", 0, 0, false, "#cross", "west", "east"));
        elements.add(crossElement(0.8, 0, 8, 15.2, 16, 8, "y", light, 1, false, "#overlay", "north", "south"));
        elements.add(crossElement(8, 0, 0.8, 8, 16, 15.2, "y", light, 1, false, "#overlay", "west", "east"));
        root.add("elements", elements);
        return root;
    }

    /** 双层柱体模型（原木等）：base（tintindex 0）+ overlay（tintindex 1，同位置重叠）。 */
    private static JsonObject dualLayerColumnJson(String baseSide, String baseEnd, String overlaySide, String overlayEnd, int light) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/block");
        root.addProperty("render_type", "minecraft:translucent");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", baseSide);
        textures.addProperty("side", baseSide);
        textures.addProperty("end", baseEnd);
        textures.addProperty("overlay_side", overlaySide);
        textures.addProperty("overlay_end", overlayEnd);
        root.add("textures", textures);
        JsonArray elements = new JsonArray();
        // base 柱体
        JsonObject base = new JsonObject();
        base.add("from", vec3(0, 0, 0));
        base.add("to", vec3(16, 16, 16));
        base.addProperty("shade", false);
        JsonObject baseFaces = new JsonObject();
        baseFaces.add("down", face("#end", "down", 0, 0));
        baseFaces.add("up", face("#end", "up", 0, 0));
        baseFaces.add("north", face("#side", "north", 0, 0));
        baseFaces.add("south", face("#side", "south", 0, 0));
        baseFaces.add("west", face("#side", "west", 0, 0));
        baseFaces.add("east", face("#side", "east", 0, 0));
        base.add("faces", baseFaces);
        elements.add(base);
        // overlay 柱体（同位置）
        JsonObject overlay = new JsonObject();
        overlay.add("from", vec3(0, 0, 0));
        overlay.add("to", vec3(16, 16, 16));
        overlay.addProperty("shade", false);
        JsonObject overlayFaces = new JsonObject();
        overlayFaces.add("down", face("#overlay_end", "down", light, 1));
        overlayFaces.add("up", face("#overlay_end", "up", light, 1));
        overlayFaces.add("north", face("#overlay_side", "north", light, 1));
        overlayFaces.add("south", face("#overlay_side", "south", light, 1));
        overlayFaces.add("west", face("#overlay_side", "west", light, 1));
        overlayFaces.add("east", face("#overlay_side", "east", light, 1));
        overlay.add("faces", overlayFaces);
        elements.add(overlay);
        root.add("elements", elements);
        return root;
    }

    // ===================== multipart helpers =====================

    private static ResourceLocation modelRef(ResourceLocation modelKey) {
        return PackPaths.blockModelRef(modelKey);
    }

    private static JsonObject applyModel(ResourceLocation model) {
        JsonObject apply = new JsonObject();
        apply.addProperty("model", model.toString());
        return apply;
    }

    private static JsonObject applyModel(ResourceLocation model, int x, Integer y) {
        JsonObject apply = applyModel(model);
        apply.addProperty("x", x);
        if (y != null) apply.addProperty("y", y);
        return apply;
    }

    private static void putSimpleVariant(ResourceLocation blockId, ResourceLocation model) {
        JsonObject variants = new JsonObject();
        JsonObject apply = applyModel(model);
        JsonObject entry = new JsonObject();
        entry.addProperty("model", apply.get("model").getAsString());
        if (apply.has("x")) {
            entry.addProperty("x", apply.get("x").getAsInt());
        }
        if (apply.has("y")) {
            entry.addProperty("y", apply.get("y").getAsInt());
        }
        variants.add("", entry);
        JsonObject root = new JsonObject();
        root.add("variants", variants);
        BATCH.addBlockState(blockId, root);
    }

    private static void putLogVariants(ResourceLocation blockId, ResourceLocation columnModel) {
        JsonObject variants = new JsonObject();
        variants.add("axis=y", applyAsVariant(columnModel, 0, null));
        variants.add("axis=z", applyAsVariant(columnModel, ROTATE_90, null));
        variants.add("axis=x", applyAsVariant(columnModel, ROTATE_90, ROTATE_90));
        JsonObject root = new JsonObject();
        root.add("variants", variants);
        BATCH.addBlockState(blockId, root);
    }

    private static JsonObject applyAsVariant(ResourceLocation model, int x, Integer y) {
        JsonObject apply = applyModel(model);
        JsonObject entry = new JsonObject();
        entry.addProperty("model", apply.get("model").getAsString());
        if (x != 0) {
            entry.addProperty("x", x);
        }
        if (y != null) {
            entry.addProperty("y", y);
        }
        return entry;
    }

    // ===================== dual-layer model builders =====================

    private static ResourceLocation tintModelKey(ResourceLocation blockId, String suffix) {
        return RLUtils.get(blockId.getNamespace(), blockId.getPath() + suffix);
    }

    /**
     * 从 base 模型链解析指定纹理；不同模型纹理键不同（cube_all 用 {@code all}、
     * cube_column 用 {@code side}/{@code end}、cross 用 {@code cross}），失败时依次兜底。
     */
    private static String baseTexture(ResourceLocation baseModel, String key) {
        String tex = resolveModelTexture(baseModel, key);
        if (tex == null) {
            tex = resolveModelTexture(baseModel, "all");
        }
        if (tex == null) {
            tex = resolveModelTexture(baseModel, "side");
        }
        if (tex == null) {
            tex = resolveModelTexture(baseModel, "end");
        }
        if (tex == null) {
            tex = resolveModelTexture(baseModel, "cross");
        }
        if (tex == null) {
            tex = resolveModelTexture(baseModel, "particle");
        }
        return tex != null ? tex : BLANK_TEXTURE.toString();
    }

    private static ResourceLocation createDualLayerCube(ResourceLocation blockId, ResourceLocation baseModel,
                                                        ResourceLocation overlayTex, int light) {
        ResourceLocation key = tintModelKey(blockId, "_model");
        BATCH.addBlockModel(key, dualLayerCubeJson(baseTexture(baseModel, "all"), nullSafe(overlayTex).toString(), light));
        return modelRef(key);
    }

    private static ResourceLocation createDualLayerLeaves(ResourceLocation blockId, ResourceLocation baseModel,
                                                          ResourceLocation overlayTex, int light) {
        ResourceLocation key = tintModelKey(blockId, "_model");
        BATCH.addBlockModel(key, dualLayerLeavesJson(baseTexture(baseModel, "all"), nullSafe(overlayTex).toString(), light));
        return modelRef(key);
    }

    private static ResourceLocation createDualLayerCross(ResourceLocation blockId, ResourceLocation baseModel,
                                                         ResourceLocation overlayTex, int light) {
        ResourceLocation key = tintModelKey(blockId, "_model");
        BATCH.addBlockModel(key, dualLayerCrossJson(baseTexture(baseModel, "cross"), nullSafe(overlayTex).toString(), light));
        return modelRef(key);
    }

    private static ResourceLocation createDualLayerColumn(ResourceLocation blockId, ResourceLocation baseModel,
                                                          ResourceLocation overlaySide, ResourceLocation overlayEnd,
                                                          int light) {
        ResourceLocation key = tintModelKey(blockId, "_model");
        BATCH.addBlockModel(key, dualLayerColumnJson(
                baseTexture(baseModel, "side"),
                baseTexture(baseModel, "end"),
                nullSafe(overlaySide).toString(),
                nullSafe(overlayEnd).toString(),
                light));
        return modelRef(key);
    }

    // ===================== register =====================

    private static void registerCubeLike(ResourceLocation blockId, ResourceLocation baseModel, ResourceLocation tintTexture, int color, int light) {
        ResourceLocation modelRef = createDualLayerCube(blockId, baseModel, tintTexture, light);
        putSimpleVariant(blockId, modelRef);
        BATCH.addCompositeBlockItem(blockId, modelRef, color, null);
    }

    private static void registerLeaves(ResourceLocation leavesId, ResourceLocation baseModel, ResourceLocation overlay,
                                       int color, int light, ResourceTreeBaseType treeType) {
        ResourceLocation modelRef = createDualLayerLeaves(leavesId, baseModel, overlay, light);
        putSimpleVariant(leavesId, modelRef);
        // 底层原版树叶需与对应 *_leaves 物品相同的 constant 叶片色，否则库存中灰白
        BATCH.addCompositeBlockItem(leavesId, modelRef, color, vanillaLeavesItemTint(treeType));
    }

    private static void registerLog(ResourceLocation logId,
                                    ResourceLocation baseVertical,
                                    ResourceLocation baseHorizontal,
                                    ResourceLocation oreSide,
                                    ResourceLocation oreEnd,
                                    int color,
                                    int light) {
        ResourceLocation modelRef = createDualLayerColumn(logId, baseVertical, oreSide, oreEnd, light);
        putLogVariants(logId, modelRef);
        BATCH.addCompositeBlockItem(logId, modelRef, color, null);
    }

    private static void registerSapling(ResourceLocation saplingId, ResourceTreeBaseType treeType, int color, int light) {
        ResourceLocation modelRef = createDualLayerCross(saplingId, treeType.saplingBase(), treeType.saplingOverlay(), light);
        putSimpleVariant(saplingId, modelRef);
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
