package com.maple.resource_farm.data.tree.builder;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.api.ResourceOre.ResourceOreType;
import com.maple.resource_farm.api.ResourceTree.ResourceTreeType;
import com.maple.resource_farm.api.block.ColoringSettings;
import com.maple.resource_farm.common.pack.ResourceFarmDynamicResourcePack;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.data.ResourceFarmBlocks;
import com.maple.resource_farm.data.misc.ResourceFarmModels;
import com.maple.resource_farm.data.misc.ResourceFarmModels.LayerFaceTextures;
import com.maple.resource_farm.data.tree.ResourceTree;
import com.maple.resource_farm.data.tree.ResourceTreeConfig;
import com.maple.resource_farm.utils.RLUtils;

import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TreeModelRenderer {

    // ===================== 核心常量 =====================
    private static final Identifier BLANK_TEXTURE = ResourceFarm.id("block/void");
    private static final String VERTICAL_LOG_SUFFIX = "_vertical";
    private static final String BLOCK_PATH_PREFIX = "block/";
    private static final int ROTATE_90_DEGREE = 90;

    private static final boolean STRIPPED_LOG_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog;
    private static final boolean WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood;
    private static final boolean STRIPPED_WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood;
    private static final boolean PLANKS_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks;
    private static final boolean CLUMP_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateClump();

    // 父模型
    private static final Identifier PARENT_ALL = ResourceFarmModels.STATIC_ALL_PARENT;
    private static final Identifier PARENT_HORIZONTAL_COLUMN = ResourceFarmModels.STATIC_HORIZONTAL_COLUMN_PARENT;
    private static final Identifier PARENT_BOTTOM_TOP = ResourceFarmModels.STATIC_BOTTOM_TOP_PARENT;
    private static final Identifier PARENT_CROSS = ResourceFarmModels.STATIC_CROSS_PARENT;
    private static final Identifier PARENT_LEAVES = ResourceFarmModels.STATIC_LEAVES_PARENT;
    private static final Identifier PARENT_ITEM = ResourceFarmModels.STATIC_ITEM_PARENT;

    // ===================== 核心配置校验 =====================
    private static boolean isTreeConfigValid(ResourceTreeConfig treeConfig) {
        if (treeConfig == null) return false;
        return treeConfig.treeType() != null && treeConfig.oreType() != null;
    }

    // ===================== 核心实体 =====================
    @Getter
    private static class ResourceTreeBundle {

        private final String treeId;
        private final String treeName;
        private final ResourceTreeType treeType;
        private final ResourceOreType oreType;
        /** 与 TreeBuilder 注册时一致的着色配置 */
        private final ColoringSettings coloring;
        private final ColoringSettings leavesColoring;
        private final ColoringSettings clumpColoring;

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
            this.treeId = treeId;
            this.treeName = resourceTree.getResourceTreeConfig().id();
            this.treeType = resourceTree.getResourceTreeConfig().treeType();
            this.oreType = resourceTree.getResourceTreeConfig().oreType();
            ColoringSettings base = resourceTree.getResourceTreeConfig().coloringSettings();
            this.coloring = base;
            this.leavesColoring = ColoringSettings.getLeave(base);
            this.clumpColoring = ColoringSettings.reversed(base);

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
            var isTreeInfoValid = treeId != null && !treeId.isBlank() && treeName != null && !treeName.isBlank();
            var isTypeValid = treeType != null && oreType != null;
            var isResourceValid = saplingId != null && leavesId != null && logId != null;
            return isTreeInfoValid && isTypeValid && isResourceValid;
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

    // ===================== 批量注册器 =====================
    private static class BatchRegistrar {

        private final ConcurrentHashMap<Identifier, JsonObject> blockModels = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<Identifier, JsonObject> blockStates = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<Identifier, JsonObject> itemModels = new ConcurrentHashMap<>();
        /** MC 26.1+ items/*.json 物品→模型映射 */
        private final ConcurrentHashMap<Identifier, JsonObject> itemDefinitions = new ConcurrentHashMap<>();
        private final AtomicBoolean isCommitted = new AtomicBoolean(false);

        public void addBlockModel(Identifier blockId, JsonObject modelJson) {
            if (blockId != null && modelJson != null) {
                blockModels.put(blockId, modelJson);
            }
        }

        public void addBlockState(Identifier blockId, JsonObject stateJson) {
            if (blockId != null && stateJson != null) {
                blockStates.put(blockId, stateJson);
            }
        }

        public void addItemModel(Identifier itemId, JsonObject modelJson) {
            if (itemId != null && modelJson != null) {
                itemModels.put(itemId, modelJson);
            }
        }

        public void addItemDefinition(Identifier itemId, JsonObject definitionJson) {
            if (itemId != null && definitionJson != null) {
                itemDefinitions.put(itemId, definitionJson);
            }
        }

        public void commit() {
            if (isCommitted.getAndSet(true)) {
                return;
            }
            blockModels.forEach(ResourceFarmDynamicResourcePack::addBlockModel);
            blockStates.forEach(ResourceFarmDynamicResourcePack::addBlockState);
            itemModels.forEach(ResourceFarmDynamicResourcePack::addItemModel);
            itemDefinitions.forEach(ResourceFarmDynamicResourcePack::addItemDefinition);
            clearAll();
        }

        public void reset() {
            isCommitted.set(false);
            clearAll();
        }

        private void clearAll() {
            blockModels.clear();
            blockStates.clear();
            itemModels.clear();
            itemDefinitions.clear();
        }
    }

    // ===================== 缓存与注册器实例 =====================
    private static final Map<Identifier, Identifier> BLOCK_MODEL_LOCATION_CACHE = new ConcurrentHashMap<>();
    private static final BatchRegistrar BATCH_REGISTRAR = new BatchRegistrar();

    // ===================== 基础工具方法 =====================
    private static JsonObject createBaseModelJson(Identifier parentModel) {
        var modelJson = new JsonObject();
        var parentValue = parentModel == null ? "" : parentModel.toString();
        modelJson.add("parent", new JsonPrimitive(parentValue));
        return modelJson;
    }

    private static void bindTextureToModel(JsonObject modelJson, String textureAlias,
                                           Identifier textureLocation) {
        if (modelJson == null || textureAlias == null || textureAlias.isBlank()) {
            ResourceFarm.LOGGER.warn("Invalid model JSON or texture alias, skip texture binding");
            return;
        }
        var textures = GsonHelper.getAsJsonObject(modelJson, "textures", new JsonObject());
        textures.add(textureAlias, new JsonPrimitive(nullSafeTexture(textureLocation).toString()));
        modelJson.add("textures", textures);
    }

    private static Identifier nullSafeTexture(Identifier texture) {
        return texture == null ? BLANK_TEXTURE : texture;
    }

    private static Identifier buildBlockModelLocation(Identifier blockId) {
        if (blockId == null) {
            return null;
        }
        return BLOCK_MODEL_LOCATION_CACHE.computeIfAbsent(blockId,
                id -> RLUtils.get(id.getNamespace(), BLOCK_PATH_PREFIX + id.getPath()));
    }

    private static String buildBlockModelRef(Identifier blockId) {
        var blockModelLocation = buildBlockModelLocation(blockId);
        return blockModelLocation == null ? "" : blockModelLocation.toString();
    }

    private static JsonObject buildDefaultVariant(String modelRef) {
        var variant = new JsonObject();
        var modelValue = (modelRef == null || modelRef.isBlank()) ? "" : modelRef;
        variant.add("model", new JsonPrimitive(modelValue));
        return variant;
    }

    private static JsonObject buildRotatedVariant(String modelRef, int x, Integer y) {
        var variant = new JsonObject();
        var modelValue = (modelRef == null || modelRef.isBlank()) ? "" : modelRef;
        variant.add("model", new JsonPrimitive(modelValue));
        variant.add("x", new JsonPrimitive(x));
        if (y != null) {
            variant.add("y", new JsonPrimitive(y));
        }
        return variant;
    }

    // ===================== 模型构建 =====================

    /**
     * MC 26.1+ 客户端物品定义（对齐 quantum generated/items）：
     * 
     * <pre>
     * {
     *   "model": {
     *     "type": "minecraft:model",
     *     "model": "namespace:item/xxx 或 namespace:block/xxx",
     *     "tints": [
     *       { "type": "minecraft:constant", "value": -1 },
     *       { "type": "minecraft:constant", "value": -13089548 }
     *     ]
     *   }
     * }
     * </pre>
     * <p>
     * 禁止在 items JSON 里使用自定义 tint type（如 {@code resource_farm:tintable_item}）：
     * ClientItem 解析时可能尚未注册该 id，会报 Unknown element id 并整表解析失败 → 紫黑块。
     * 颜色在生成阶段烘焙为 {@code minecraft:constant}。
     */
    private static JsonObject createClientItemDefinition(Identifier modelRef, ColoringSettings coloring) {
        var root = new JsonObject();
        var model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", modelRef == null ? "" : modelRef.toString());

        // 双层物品/方块：始终输出 2 个 tint 槽，与 models 中 tintindex 0/1 对齐
        boolean[] layers = coloring != null ? coloring.tintLayers() : new boolean[0];
        int[] colors = coloring != null ? coloring.colors() : new int[0];
        var tints = new JsonArray();
        for (int i = 0; i < 2; i++) {
            var tint = new JsonObject();
            tint.addProperty("type", "minecraft:constant");
            // 未启用着色的层用 -1（与 quantum 一致，表示不染色/白乘）
            int value = -1;
            if (i < layers.length && i < colors.length && layers[i]) {
                value = colors[i];
            }
            tint.addProperty("value", value);
            tints.add(tint);
        }
        model.add("tints", tints);
        root.add("model", model);
        return root;
    }

    /** models/item 的逻辑 ID：namespace:item/path */
    private static Identifier itemModelRef(Identifier itemId) {
        return RLUtils.get(itemId.getNamespace(), "item/" + itemId.getPath());
    }

    private static void createAndRegisterDualLayerItemModel(
                                                            Identifier itemId,
                                                            Identifier layer0Texture,
                                                            Identifier layer1Texture,
                                                            ColoringSettings coloring) {
        var layer0 = nullSafeTexture(layer0Texture);
        var layer1 = nullSafeTexture(layer1Texture);
        var modelJson = createBaseModelJson(PARENT_ITEM);
        bindTextureToModel(modelJson, "layer0", layer0);
        bindTextureToModel(modelJson, "layer1", layer1);
        // 几何：models/item/...
        BATCH_REGISTRAR.addItemModel(itemId, modelJson);
        // 映射：items/... → models/item（constant tints）
        BATCH_REGISTRAR.addItemDefinition(itemId, createClientItemDefinition(itemModelRef(itemId), coloring));
    }

    private static void createAndRegisterDualLayerBlockModel(
                                                             Identifier blockId,
                                                             Identifier layer0Texture,
                                                             Identifier layer1Texture) {
        var layer0 = nullSafeTexture(layer0Texture);
        var layer1 = nullSafeTexture(layer1Texture);
        var modelJson = createBaseModelJson(PARENT_ALL);
        bindTextureToModel(modelJson, "layer0", layer0);
        bindTextureToModel(modelJson, "layer1", layer1);
        BATCH_REGISTRAR.addBlockModel(blockId, modelJson);
    }

    private static void createAndRegisterDualLayerHorizontalColumnModel(
                                                                        Identifier baseLogId,
                                                                        LayerFaceTextures layer0Faces,
                                                                        LayerFaceTextures layer1Faces) {
        var horizontalLogModelJson = createBaseModelJson(PARENT_HORIZONTAL_COLUMN);
        bindTextureToModel(horizontalLogModelJson, "layer0_side", layer0Faces.side());
        bindTextureToModel(horizontalLogModelJson, "layer0_end", layer0Faces.top());
        bindTextureToModel(horizontalLogModelJson, "layer1_side", layer1Faces.side());
        bindTextureToModel(horizontalLogModelJson, "layer1_end", layer1Faces.top());

        var verticalLogModelJson = createBaseModelJson(PARENT_BOTTOM_TOP);
        bindTextureToModel(verticalLogModelJson, "layer0_bottom", layer0Faces.bottom());
        bindTextureToModel(verticalLogModelJson, "layer0_side", layer0Faces.side());
        bindTextureToModel(verticalLogModelJson, "layer0_top", layer0Faces.top());
        bindTextureToModel(verticalLogModelJson, "layer1_bottom", layer1Faces.bottom());
        bindTextureToModel(verticalLogModelJson, "layer1_side", layer1Faces.side());
        bindTextureToModel(verticalLogModelJson, "layer1_top", layer1Faces.top());

        var logName = baseLogId.getPath();
        var horizontalLogId = ResourceFarm.id(logName);
        var verticalLogId = ResourceFarm.id(logName + VERTICAL_LOG_SUFFIX);
        BATCH_REGISTRAR.addBlockModel(horizontalLogId, horizontalLogModelJson);
        BATCH_REGISTRAR.addBlockModel(verticalLogId, verticalLogModelJson);

        createAndRegisterLogBlockState(baseLogId, horizontalLogId, verticalLogId);
    }

    private static void createAndRegisterDualLayerCrossModel(
                                                             Identifier saplingId,
                                                             Identifier layer0Texture,
                                                             Identifier layer1Texture) {
        var layer0 = nullSafeTexture(layer0Texture);
        var layer1 = nullSafeTexture(layer1Texture);
        var modelJson = createBaseModelJson(PARENT_CROSS);
        bindTextureToModel(modelJson, "layer0", layer0);
        bindTextureToModel(modelJson, "layer1", layer1);
        BATCH_REGISTRAR.addBlockModel(saplingId, modelJson);
    }

    private static void createAndRegisterSaplingBlockState(Identifier saplingId) {
        var blockStateJson = new JsonObject();
        var variants = new JsonObject();
        var saplingModelRef = buildBlockModelRef(saplingId);
        var stageVariant = buildDefaultVariant(saplingModelRef);
        variants.add("stage=0", stageVariant);
        variants.add("stage=1", stageVariant);
        blockStateJson.add("variants", variants);
        BATCH_REGISTRAR.addBlockState(saplingId, blockStateJson);
    }

    private static void createAndRegisterLogBlockState(
                                                       Identifier logId,
                                                       Identifier horizontalModelId,
                                                       Identifier verticalModelId) {
        var blockStateJson = new JsonObject();
        var variants = new JsonObject();
        var horizontalModelRef = buildBlockModelRef(horizontalModelId);
        var verticalModelRef = buildBlockModelRef(verticalModelId);

        variants.add("axis=x", buildRotatedVariant(horizontalModelRef, ROTATE_90_DEGREE, ROTATE_90_DEGREE));
        variants.add("axis=y", buildDefaultVariant(verticalModelRef));
        variants.add("axis=z", buildRotatedVariant(horizontalModelRef, ROTATE_90_DEGREE, null));

        blockStateJson.add("variants", variants);
        BATCH_REGISTRAR.addBlockState(logId, blockStateJson);
    }

    private static void createAndRegisterSimpleBlockState(Identifier location) {
        var blockStateJson = new JsonObject();
        var variants = new JsonObject();
        var modelRef = buildBlockModelRef(location);
        variants.add("", buildDefaultVariant(modelRef));
        blockStateJson.add("variants", variants);
        BATCH_REGISTRAR.addBlockState(location, blockStateJson);
    }

    private static void createAndRegisterDualLayerLeavesModel(
                                                              Identifier leavesId,
                                                              Identifier layer0Texture,
                                                              Identifier layer1Texture) {
        var layer0 = nullSafeTexture(layer0Texture);
        var layer1 = nullSafeTexture(layer1Texture);
        var modelJson = createBaseModelJson(PARENT_LEAVES);
        bindTextureToModel(modelJson, "layer0", layer0);
        bindTextureToModel(modelJson, "layer1", layer1);
        BATCH_REGISTRAR.addBlockModel(leavesId, modelJson);
    }

    /**
     * 方块物品：只写 items/*.json，直接指向 block 模型（与 quantum 一致）。
     */
    private static void createAndRegisterItemModelFromBlock(
                                                            Identifier itemId,
                                                            Identifier blockId,
                                                            ColoringSettings coloring) {
        var blockModelRef = buildBlockModelLocation(blockId);
        BATCH_REGISTRAR.addItemDefinition(itemId, createClientItemDefinition(blockModelRef, coloring));
    }

    // ===================== 快捷注册 =====================
    private static void registerSapling(ResourceTreeBundle bundle) {
        var saplingId = bundle.getSaplingId();
        var treeType = bundle.getTreeType();
        createAndRegisterDualLayerCrossModel(saplingId, treeType.sapling(), treeType.saplingOverlay());
        createAndRegisterDualLayerItemModel(saplingId, treeType.sapling(), treeType.saplingOverlay(), bundle.getColoring());
        createAndRegisterSaplingBlockState(saplingId);
    }

    private static void registerLeaves(ResourceTreeBundle bundle) {
        var leavesId = bundle.getLeavesId();
        var treeType = bundle.getTreeType();
        createAndRegisterDualLayerLeavesModel(leavesId, treeType.leaves(), treeType.leavesOverlay());
        createAndRegisterItemModelFromBlock(leavesId, leavesId, bundle.getLeavesColoring());
        createAndRegisterSimpleBlockState(leavesId);
    }

    private static void registerLog(ResourceTreeBundle bundle) {
        var logId = bundle.getLogId();
        var treeType = bundle.getTreeType();
        var oreType = bundle.getOreType();
        var logLayer0 = LayerFaceTextures.bottomTopSame(treeType.logTop(), treeType.log());
        var logLayer1 = LayerFaceTextures.bottomTopSame(oreType.center(), oreType.base());
        createAndRegisterDualLayerHorizontalColumnModel(logId, logLayer0, logLayer1);
        createAndRegisterItemModelFromBlock(logId, logId, bundle.getColoring());
    }

    private static void registerStrippedLog(ResourceTreeBundle bundle) {
        if (!bundle.isStrippedLogEnabled()) return;
        var strippedLogId = bundle.getStrippedLogId();
        var treeType = bundle.getTreeType();
        var oreType = bundle.getOreType();
        var logLayer0 = LayerFaceTextures.bottomTopSame(treeType.strippedLogTop(), treeType.strippedLog());
        var logLayer1 = LayerFaceTextures.bottomTopSame(oreType.center(), oreType.base());
        createAndRegisterDualLayerHorizontalColumnModel(strippedLogId, logLayer0, logLayer1);
        createAndRegisterItemModelFromBlock(strippedLogId, strippedLogId, bundle.getColoring());

        var logName = strippedLogId.getPath();
        var horizontalLogId = ResourceFarm.id(logName);
        var verticalLogId = ResourceFarm.id(logName + VERTICAL_LOG_SUFFIX);
        createAndRegisterLogBlockState(strippedLogId, horizontalLogId, verticalLogId);
    }

    private static void registerWood(ResourceTreeBundle bundle) {
        if (!bundle.isWoodEnabled()) return;
        var woodId = bundle.getWoodId();
        var treeType = bundle.getTreeType();
        var oreType = bundle.getOreType();
        createAndRegisterDualLayerBlockModel(woodId, treeType.log(), oreType.base());
        createAndRegisterItemModelFromBlock(woodId, woodId, bundle.getColoring());
        createAndRegisterSimpleBlockState(woodId);
    }

    private static void registerStrippedWood(ResourceTreeBundle bundle) {
        if (!bundle.isStrippedWoodEnabled()) return;
        var strippedWoodId = bundle.getStrippedWoodId();
        var treeType = bundle.getTreeType();
        var oreType = bundle.getOreType();
        createAndRegisterDualLayerBlockModel(strippedWoodId, treeType.strippedLog(), oreType.base());
        createAndRegisterItemModelFromBlock(strippedWoodId, strippedWoodId, bundle.getColoring());
        createAndRegisterSimpleBlockState(strippedWoodId);
    }

    private static void registerPlanks(ResourceTreeBundle bundle) {
        if (!bundle.isPlanksEnabled()) return;
        var planksId = bundle.getPlanksId();
        var treeType = bundle.getTreeType();
        var oreType = bundle.getOreType();
        createAndRegisterDualLayerBlockModel(planksId, treeType.planks(), oreType.base());
        createAndRegisterItemModelFromBlock(planksId, planksId, bundle.getColoring());
        createAndRegisterSimpleBlockState(planksId);
    }

    private static void registerResin(ResourceTreeBundle bundle) {
        var resinId = bundle.getResinId();
        var treeType = bundle.getTreeType();
        createAndRegisterDualLayerItemModel(resinId, treeType.resin(), treeType.resinOverlay(), bundle.getColoring());
    }

    private static void registerFruit(ResourceTreeBundle bundle) {
        var fruitId = bundle.getFruitId();
        var treeType = bundle.getTreeType();
        createAndRegisterDualLayerItemModel(fruitId, treeType.fruit(), treeType.fruitOverlay(), bundle.getColoring());
    }

    private static void registerClump(ResourceTreeBundle bundle) {
        if (!bundle.isClumpEnabled()) return;
        var clump = bundle.getClumpId();
        var treeType = bundle.getTreeType();
        createAndRegisterDualLayerItemModel(clump, treeType.clump(), treeType.clumpOverlay(), bundle.getClumpColoring());
    }

    private static void buildAndRegisterTreeBlockBundle(ResourceTreeBundle bundle) {
        if (bundle == null || !bundle.isValid()) {
            return;
        }
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
        BLOCK_MODEL_LOCATION_CACHE.clear();
        BATCH_REGISTRAR.reset();
    }

    public static void reinitModels() {
        clearCache();

        ResourceFarmBlocks.ResourceTreeMap.values().parallelStream().forEach(resourceTree -> {
            if (resourceTree == null) return;

            var treeConfig = resourceTree.getResourceTreeConfig();
            if (!isTreeConfigValid(treeConfig)) return;

            try {
                var blockBundle = new ResourceTreeBundle(treeConfig.id(), resourceTree);
                if (blockBundle.isValid()) {
                    buildAndRegisterTreeBlockBundle(blockBundle);
                }
            } catch (Exception e) {
                var treeId = treeConfig.id();
                ResourceFarm.LOGGER.error("构建树模型失败，treeId={}，resourceTree={}，开始跳过该树的后续构建", treeId, resourceTree, e);
            }
        });

        BATCH_REGISTRAR.commit();
    }
}
