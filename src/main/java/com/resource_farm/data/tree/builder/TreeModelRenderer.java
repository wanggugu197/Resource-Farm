package com.resource_farm.data.tree.builder;

import com.resource_farm.ResourceFarm;
import com.resource_farm.api.ResourceOre.ResourceOreType;
import com.resource_farm.api.ResourceTree.ResourceTreeType;
import com.resource_farm.common.pack.ResourceFarmDynamicResourcePack;
import com.resource_farm.config.ResourceFarmConfigHolder;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.data.misc.ResourceFarmModels;
import com.resource_farm.data.misc.ResourceFarmModels.LayerFaceTextures;
import com.resource_farm.data.tree.ResourceTree;
import com.resource_farm.data.tree.ResourceTreeConfig;
import com.resource_farm.utils.RLUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TreeModelRenderer {

    // ===================== 核心常量 =====================
    private static final ResourceLocation BLANK_TEXTURE = ResourceFarm.id("block/void");
    private static final String VERTICAL_LOG_SUFFIX = "_vertical";
    private static final String BLOCK_PATH_PREFIX = "block/";
    private static final int ROTATE_90_DEGREE = 90;

    private static final boolean STRIPPED_LOG_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog;
    private static final boolean WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood;
    private static final boolean STRIPPED_WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood;
    private static final boolean PLANKS_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks;

    // 父模型
    private static final ResourceLocation PARENT_ALL = ResourceFarmModels.STATIC_ALL_PARENT;
    private static final ResourceLocation PARENT_HORIZONTAL_COLUMN = ResourceFarmModels.STATIC_HORIZONTAL_COLUMN_PARENT;
    private static final ResourceLocation PARENT_BOTTOM_TOP = ResourceFarmModels.STATIC_BOTTOM_TOP_PARENT;
    private static final ResourceLocation PARENT_CROSS = ResourceFarmModels.STATIC_CROSS_PARENT;
    private static final ResourceLocation PARENT_LEAVES = ResourceFarmModels.STATIC_LEAVES_PARENT;
    private static final ResourceLocation PARENT_ITEM = ResourceFarmModels.STATIC_ITEM_PARENT;

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

        private final ResourceLocation saplingId;
        private final ResourceLocation leavesId;
        private final ResourceLocation logId;

        private final ResourceLocation strippedLogId;
        private final ResourceLocation woodId;
        private final ResourceLocation strippedWoodId;

        private final ResourceLocation planksId;

        private final ResourceLocation resinId;
        private final ResourceLocation fruitId;

        public ResourceTreeBundle(String treeId, ResourceTree resourceTree) {
            this.treeId = treeId;
            this.treeName = resourceTree.getResourceTreeConfig().id();
            this.treeType = resourceTree.getResourceTreeConfig().treeType();
            this.oreType = resourceTree.getResourceTreeConfig().oreType();

            this.saplingId = resourceTree.getSapling().getId();
            this.leavesId = resourceTree.getLeaves().getId();
            this.logId = resourceTree.getLog().getId();

            this.strippedLogId = STRIPPED_LOG_ENABLED ? resourceTree.getStrippedLog().getId() : null;
            this.woodId = WOOD_ENABLED ? resourceTree.getWood().getId() : null;
            this.strippedWoodId = STRIPPED_WOOD_ENABLED ? resourceTree.getStrippedWood().getId() : null;

            this.planksId = PLANKS_ENABLED ? resourceTree.getPlanks().getId() : null;

            this.resinId = resourceTree.getResin().getId();
            this.fruitId = resourceTree.getFruit().getId();
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
    }

    // ===================== 批量注册器 =====================
    private static class BatchRegistrar {

        private final ConcurrentHashMap<ResourceLocation, JsonObject> blockModels = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<ResourceLocation, JsonObject> blockStates = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<ResourceLocation, JsonObject> itemModels = new ConcurrentHashMap<>();
        private final AtomicBoolean isCommitted = new AtomicBoolean(false);

        public void addBlockModel(ResourceLocation blockId, JsonObject modelJson) {
            if (blockId != null && modelJson != null) {
                blockModels.put(blockId, modelJson);
            }
        }

        public void addBlockState(ResourceLocation blockId, JsonObject stateJson) {
            if (blockId != null && stateJson != null) {
                blockStates.put(blockId, stateJson);
            }
        }

        public void addItemModel(ResourceLocation itemId, JsonObject modelJson) {
            if (itemId != null && modelJson != null) {
                itemModels.put(itemId, modelJson);
            }
        }

        public void commit() {
            if (isCommitted.getAndSet(true)) {
                return;
            }
            blockModels.forEach(ResourceFarmDynamicResourcePack::addBlockModel);
            blockStates.forEach(ResourceFarmDynamicResourcePack::addBlockState);
            itemModels.forEach(ResourceFarmDynamicResourcePack::addItemModel);
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
        }
    }

    // ===================== 缓存与注册器实例 =====================
    private static final Map<ResourceLocation, ResourceLocation> BLOCK_MODEL_LOCATION_CACHE = new ConcurrentHashMap<>();
    private static final BatchRegistrar BATCH_REGISTRAR = new BatchRegistrar();

    // ===================== 基础工具方法 =====================
    private static JsonObject createBaseModelJson(ResourceLocation parentModel) {
        var modelJson = new JsonObject();
        var parentValue = parentModel == null ? "" : parentModel.toString();
        modelJson.add("parent", new JsonPrimitive(parentValue));
        return modelJson;
    }

    private static void bindTextureToModel(JsonObject modelJson, String textureAlias,
                                           ResourceLocation textureLocation) {
        if (modelJson == null || textureAlias == null || textureAlias.isBlank()) {
            ResourceFarm.LOGGER.warn("Invalid model JSON or texture alias, skip texture binding");
            return;
        }
        var textures = GsonHelper.getAsJsonObject(modelJson, "textures", new JsonObject());
        textures.add(textureAlias, new JsonPrimitive(nullSafeTexture(textureLocation).toString()));
        modelJson.add("textures", textures);
    }

    private static ResourceLocation nullSafeTexture(ResourceLocation texture) {
        return texture == null ? BLANK_TEXTURE : texture;
    }

    private static ResourceLocation buildBlockModelLocation(ResourceLocation blockId) {
        if (blockId == null) {
            return null;
        }
        return BLOCK_MODEL_LOCATION_CACHE.computeIfAbsent(blockId,
                id -> RLUtils.get(id.getNamespace(), BLOCK_PATH_PREFIX + id.getPath()));
    }

    private static String buildBlockModelRef(ResourceLocation blockId) {
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
    private static void createAndRegisterDualLayerItemModel(
                                                            ResourceLocation ItemId,
                                                            ResourceLocation layer0Texture,
                                                            ResourceLocation layer1Texture) {
        var layer0 = nullSafeTexture(layer0Texture);
        var layer1 = nullSafeTexture(layer1Texture);
        var modelJson = createBaseModelJson(PARENT_ITEM);
        bindTextureToModel(modelJson, "layer0", layer0);
        bindTextureToModel(modelJson, "layer1", layer1);
        BATCH_REGISTRAR.addItemModel(ItemId, modelJson);
    }

    private static void createAndRegisterDualLayerBlockModel(
                                                             ResourceLocation blockId,
                                                             ResourceLocation layer0Texture,
                                                             ResourceLocation layer1Texture) {
        var layer0 = nullSafeTexture(layer0Texture);
        var layer1 = nullSafeTexture(layer1Texture);
        var modelJson = createBaseModelJson(PARENT_ALL);
        bindTextureToModel(modelJson, "layer0", layer0);
        bindTextureToModel(modelJson, "layer1", layer1);
        BATCH_REGISTRAR.addBlockModel(blockId, modelJson);
    }

    private static void createAndRegisterDualLayerHorizontalColumnModel(
                                                                        ResourceLocation baseLogId,
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
                                                             ResourceLocation saplingId,
                                                             ResourceLocation layer0Texture,
                                                             ResourceLocation layer1Texture) {
        var layer0 = nullSafeTexture(layer0Texture);
        var layer1 = nullSafeTexture(layer1Texture);
        var modelJson = createBaseModelJson(PARENT_CROSS);
        bindTextureToModel(modelJson, "layer0", layer0);
        bindTextureToModel(modelJson, "layer1", layer1);
        BATCH_REGISTRAR.addBlockModel(saplingId, modelJson);
    }

    private static void createAndRegisterSaplingBlockState(ResourceLocation saplingId) {
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
                                                       ResourceLocation logId,
                                                       ResourceLocation horizontalModelId,
                                                       ResourceLocation verticalModelId) {
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

    private static void createAndRegisterSimpleBlockState(ResourceLocation location) {
        var blockStateJson = new JsonObject();
        var variants = new JsonObject();
        var modelRef = buildBlockModelRef(location);
        variants.add("", buildDefaultVariant(modelRef));
        blockStateJson.add("variants", variants);
        BATCH_REGISTRAR.addBlockState(location, blockStateJson);
    }

    private static void createAndRegisterDualLayerLeavesModel(
                                                              ResourceLocation leavesId,
                                                              ResourceLocation layer0Texture,
                                                              ResourceLocation layer1Texture) {
        var layer0 = nullSafeTexture(layer0Texture);
        var layer1 = nullSafeTexture(layer1Texture);
        var modelJson = createBaseModelJson(PARENT_LEAVES);
        bindTextureToModel(modelJson, "layer0", layer0);
        bindTextureToModel(modelJson, "layer1", layer1);
        BATCH_REGISTRAR.addBlockModel(leavesId, modelJson);
    }

    private static void createAndRegisterItemModelFromBlock(
                                                            ResourceLocation itemId,
                                                            ResourceLocation blockId) {
        var blockModelParent = buildBlockModelLocation(blockId);
        var modelJson = createBaseModelJson(blockModelParent);
        BATCH_REGISTRAR.addItemModel(itemId, modelJson);
    }

    // ===================== 快捷注册 =====================
    private static void registerSapling(ResourceTreeBundle bundle) {
        var saplingId = bundle.getSaplingId();
        var treeType = bundle.getTreeType();
        createAndRegisterDualLayerCrossModel(saplingId, treeType.sapling(), treeType.saplingOverlay());
        createAndRegisterDualLayerItemModel(saplingId, treeType.sapling(), treeType.saplingOverlay());
        createAndRegisterSaplingBlockState(saplingId);
    }

    private static void registerLeaves(ResourceTreeBundle bundle) {
        var leavesId = bundle.getLeavesId();
        var treeType = bundle.getTreeType();
        createAndRegisterDualLayerLeavesModel(leavesId, treeType.leaves(), treeType.leavesOverlay());
        createAndRegisterItemModelFromBlock(leavesId, leavesId);
        createAndRegisterSimpleBlockState(leavesId);
    }

    private static void registerLog(ResourceTreeBundle bundle) {
        var logId = bundle.getLogId();
        var treeType = bundle.getTreeType();
        var oreType = bundle.getOreType();
        var logLayer0 = LayerFaceTextures.bottomTopSame(treeType.logTop(), treeType.log());
        var logLayer1 = LayerFaceTextures.bottomTopSame(oreType.center(), oreType.base());
        createAndRegisterDualLayerHorizontalColumnModel(logId, logLayer0, logLayer1);
        createAndRegisterItemModelFromBlock(logId, logId);
    }

    private static void registerStrippedLog(ResourceTreeBundle bundle) {
        if (!bundle.isStrippedLogEnabled()) return;
        var strippedLogId = bundle.getStrippedLogId();
        var treeType = bundle.getTreeType();
        var oreType = bundle.getOreType();
        var logLayer0 = LayerFaceTextures.bottomTopSame(treeType.strippedLogTop(), treeType.strippedLog());
        var logLayer1 = LayerFaceTextures.bottomTopSame(oreType.center(), oreType.base());
        createAndRegisterDualLayerHorizontalColumnModel(strippedLogId, logLayer0, logLayer1);
        createAndRegisterItemModelFromBlock(strippedLogId, strippedLogId);

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
        createAndRegisterItemModelFromBlock(woodId, woodId);
        createAndRegisterSimpleBlockState(woodId);
    }

    private static void registerStrippedWood(ResourceTreeBundle bundle) {
        if (!bundle.isStrippedWoodEnabled()) return;
        var strippedWoodId = bundle.getStrippedWoodId();
        var treeType = bundle.getTreeType();
        var oreType = bundle.getOreType();
        createAndRegisterDualLayerBlockModel(strippedWoodId, treeType.strippedLog(), oreType.base());
        createAndRegisterItemModelFromBlock(strippedWoodId, strippedWoodId);
        createAndRegisterSimpleBlockState(strippedWoodId);
    }

    private static void registerPlanks(ResourceTreeBundle bundle) {
        if (!bundle.isPlanksEnabled()) return;
        var planksId = bundle.getPlanksId();
        var treeType = bundle.getTreeType();
        var oreType = bundle.getOreType();
        createAndRegisterDualLayerBlockModel(planksId, treeType.planks(), oreType.base());
        createAndRegisterItemModelFromBlock(planksId, planksId);
        createAndRegisterSimpleBlockState(planksId);
    }

    private static void registerResin(ResourceTreeBundle bundle) {
        var resinId = bundle.getResinId();
        var treeType = bundle.getTreeType();
        createAndRegisterDualLayerItemModel(resinId, treeType.resin(), treeType.resinOverlay());
    }

    private static void registerFruit(ResourceTreeBundle bundle) {
        var fruitId = bundle.getFruitId();
        var treeType = bundle.getTreeType();
        createAndRegisterDualLayerItemModel(fruitId, treeType.fruit(), treeType.fruitOverlay());
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
            } catch (Exception ignored) {}
        });

        BATCH_REGISTRAR.commit();
    }
}
