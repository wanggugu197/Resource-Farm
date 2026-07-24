package com.maple.resource_farm.data.tree.builder;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.api.ResourceOre.ResourceOreType;
import com.maple.resource_farm.api.ResourceTree.ResourceTreeType;
import com.maple.resource_farm.api.block.ColoringSettings;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.data.ResourceFarmBlocks;
import com.maple.resource_farm.data.misc.ResourceFarmModels;
import com.maple.resource_farm.data.misc.ResourceFarmModels.LayerFaceTextures;
import com.maple.resource_farm.data.tree.ResourceTree;
import com.maple.resource_farm.data.tree.ResourceTreeConfig;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.resources.Identifier;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mapleutillib.api.pack.DynamicAssets;
import com.mapleutillib.api.pack.PackPaths;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 运行时树木方块 / 物品模型生成，写入 MapleUtilLib {@link DynamicAssets}。
 * <p>
 * 由 {@code REGISTRY.packs().whenClient(TreeModelRenderer::reinitModels)} 在
 * ModelManager.reload 时调用。
 */
public class TreeModelRenderer {

    // ===================== 核心常量 =====================
    private static final Identifier BLANK_TEXTURE = ResourceFarm.id("block/void");
    private static final String VERTICAL_LOG_SUFFIX = "_vertical";
    private static final int ROTATE_90_DEGREE = 90;

    private static final boolean STRIPPED_LOG_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog;
    private static final boolean WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood;
    private static final boolean STRIPPED_WOOD_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood;
    private static final boolean PLANKS_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks;
    private static final boolean CLUMP_ENABLED = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateClump();

    private static final Identifier PARENT_ALL = ResourceFarmModels.STATIC_ALL_PARENT;
    private static final Identifier PARENT_HORIZONTAL_COLUMN = ResourceFarmModels.STATIC_HORIZONTAL_COLUMN_PARENT;
    private static final Identifier PARENT_BOTTOM_TOP = ResourceFarmModels.STATIC_BOTTOM_TOP_PARENT;
    private static final Identifier PARENT_CROSS = ResourceFarmModels.STATIC_CROSS_PARENT;
    private static final Identifier PARENT_LEAVES = ResourceFarmModels.STATIC_LEAVES_PARENT;
    private static final Identifier PARENT_ITEM = ResourceFarmModels.STATIC_ITEM_PARENT;

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
        private final ConcurrentHashMap<Identifier, ItemMapping> itemMappings = new ConcurrentHashMap<>();
        private final AtomicBoolean isCommitted = new AtomicBoolean(false);

        private record ItemMapping(Identifier modelRef, ColoringSettings coloring) {}

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

        public void addItemMapping(Identifier itemId, Identifier modelRef, ColoringSettings coloring) {
            if (itemId != null && modelRef != null) {
                itemMappings.put(itemId, new ItemMapping(modelRef, coloring));
            }
        }

        public void commit(DynamicAssets assets) {
            if (isCommitted.getAndSet(true)) {
                return;
            }
            blockModels.forEach((id, json) -> assets.put(PackPaths.blockModelFile(id), json));
            itemModels.forEach((id, json) -> assets.put(PackPaths.itemModelFile(id), json));
            blockStates.forEach((id, json) -> assets.put(PackPaths.blockStateFile(id), json));
            itemMappings.forEach((itemId, mapping) -> {
                ItemTintSource[] tints = dualLayerConstantTints(
                        mapping.coloring() != null ? mapping.coloring().tintLayers() : null,
                        mapping.coloring() != null ? mapping.coloring().colors() : null);
                writeItemMapping(assets, itemId, mapping.modelRef(), tints);
            });
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
            itemMappings.clear();
        }
    }

    private static final Map<Identifier, Identifier> BLOCK_MODEL_LOCATION_CACHE = new ConcurrentHashMap<>();
    private static final BatchRegistrar BATCH_REGISTRAR = new BatchRegistrar();

    // ===================== JSON / 路径工具 =====================

    private static JsonObject parentedModel(Identifier parent, Map<String, Identifier> textures) {
        JsonObject root = new JsonObject();
        if (parent != null) {
            root.addProperty("parent", parent.toString());
        }
        if (textures != null && !textures.isEmpty()) {
            JsonObject tex = new JsonObject();
            textures.forEach((slot, id) -> {
                if (slot != null && id != null) {
                    tex.addProperty(slot, id.toString());
                }
            });
            if (!tex.entrySet().isEmpty()) {
                root.add("textures", tex);
            }
        }
        return root;
    }

    private static JsonObject parentedModel(Identifier parent, Object... textureSlotPairs) {
        if (textureSlotPairs == null || textureSlotPairs.length == 0) {
            return parentedModel(parent, Map.of());
        }
        if ((textureSlotPairs.length & 1) != 0) {
            throw new IllegalArgumentException("textureSlotPairs must be even: slot, id, slot, id, ...");
        }
        Map<String, Identifier> textures = new LinkedHashMap<>();
        for (int i = 0; i < textureSlotPairs.length; i += 2) {
            Object slot = textureSlotPairs[i];
            Object id = textureSlotPairs[i + 1];
            if (!(slot instanceof String s) || !(id instanceof Identifier rl)) {
                throw new IllegalArgumentException(
                        "texture pairs must be (String slot, Identifier id)");
            }
            textures.put(s, rl);
        }
        return parentedModel(parent, textures);
    }

    private static JsonObject multiLayerItemModel(Identifier parent, Identifier... layers) {
        Map<String, Identifier> textures = new LinkedHashMap<>();
        if (layers != null) {
            for (int i = 0; i < layers.length; i++) {
                if (layers[i] != null) {
                    textures.put("layer" + i, layers[i]);
                }
            }
        }
        return parentedModel(parent, textures);
    }

    private static Identifier blockModelRef(Identifier blockId) {
        return PackPaths.blockModelRef(blockId);
    }

    private static Identifier itemModelRef(Identifier itemId) {
        return PackPaths.itemModelRef(itemId);
    }

    /**
     * 双层 tint：按着色开关与颜色数组生成 2 槽 constant tint。
     */
    private static ItemTintSource[] dualLayerConstantTints(boolean[] layers, int[] colors) {
        ItemTintSource[] tints = new ItemTintSource[2];
        for (int i = 0; i < 2; i++) {
            int value = -1;
            if (layers != null && colors != null && i < layers.length && i < colors.length && layers[i]) {
                value = colors[i];
            }
            tints[i] = ItemModelUtils.constantTint(value);
        }
        return tints;
    }

    private static void writeItemMapping(DynamicAssets assets, Identifier itemId, Identifier modelRef,
                                         ItemTintSource[] tints) {
        if (itemId.getNamespace().equals(assets.modId())) {
            assets.mapItemTinted(itemId.getPath(), modelRef, tints);
            return;
        }
        // 跨命名空间（addon）：直接写 items 文件路径
        var unbaked = ItemModelUtils.tintedModel(modelRef, tints);
        var clientItem = new net.minecraft.client.renderer.item.ClientItem(
                unbaked, net.minecraft.client.renderer.item.ClientItem.Properties.DEFAULT);
        net.minecraft.client.renderer.item.ClientItem.CODEC
                .encodeStart(com.mojang.serialization.JsonOps.INSTANCE, clientItem)
                .result()
                .ifPresent(json -> assets.put(PackPaths.itemDefinitionFile(itemId), json));
    }

    // ===================== 基础工具 =====================

    private static Identifier nullSafeTexture(Identifier texture) {
        return texture == null ? BLANK_TEXTURE : texture;
    }

    private static Identifier buildBlockModelLocation(Identifier blockId) {
        if (blockId == null) {
            return null;
        }
        return BLOCK_MODEL_LOCATION_CACHE.computeIfAbsent(blockId, TreeModelRenderer::blockModelRef);
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

    private static void createAndRegisterDualLayerItemModel(
                                                            Identifier itemId,
                                                            Identifier layer0Texture,
                                                            Identifier layer1Texture,
                                                            ColoringSettings coloring) {
        var layer0 = nullSafeTexture(layer0Texture);
        var layer1 = nullSafeTexture(layer1Texture);
        BATCH_REGISTRAR.addItemModel(itemId, multiLayerItemModel(PARENT_ITEM, layer0, layer1));
        BATCH_REGISTRAR.addItemMapping(itemId, itemModelRef(itemId), coloring);
    }

    private static void createAndRegisterDualLayerBlockModel(
                                                             Identifier blockId,
                                                             Identifier layer0Texture,
                                                             Identifier layer1Texture) {
        var layer0 = nullSafeTexture(layer0Texture);
        var layer1 = nullSafeTexture(layer1Texture);
        BATCH_REGISTRAR.addBlockModel(blockId,
                parentedModel(PARENT_ALL, "layer0", layer0, "layer1", layer1));
    }

    private static void createAndRegisterDualLayerHorizontalColumnModel(
                                                                        Identifier baseLogId,
                                                                        LayerFaceTextures layer0Faces,
                                                                        LayerFaceTextures layer1Faces) {
        var horizontalLogModelJson = parentedModel(PARENT_HORIZONTAL_COLUMN,
                "layer0_side", layer0Faces.side(),
                "layer0_end", layer0Faces.top(),
                "layer1_side", layer1Faces.side(),
                "layer1_end", layer1Faces.top());

        var verticalLogModelJson = parentedModel(PARENT_BOTTOM_TOP,
                "layer0_bottom", layer0Faces.bottom(),
                "layer0_side", layer0Faces.side(),
                "layer0_top", layer0Faces.top(),
                "layer1_bottom", layer1Faces.bottom(),
                "layer1_side", layer1Faces.side(),
                "layer1_top", layer1Faces.top());

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
        BATCH_REGISTRAR.addBlockModel(saplingId,
                parentedModel(PARENT_CROSS, "layer0", layer0, "layer1", layer1));
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
        BATCH_REGISTRAR.addBlockModel(leavesId,
                parentedModel(PARENT_LEAVES, "layer0", layer0, "layer1", layer1));
    }

    private static void createAndRegisterItemModelFromBlock(
                                                            Identifier itemId,
                                                            Identifier blockId,
                                                            ColoringSettings coloring) {
        BATCH_REGISTRAR.addItemMapping(itemId, buildBlockModelLocation(blockId), coloring);
    }

    // ===================== 快捷注册 =====================
    private static void registerSapling(ResourceTreeBundle bundle) {
        var saplingId = bundle.getSaplingId();
        var treeType = bundle.getTreeType();
        createAndRegisterDualLayerCrossModel(saplingId, treeType.sapling(), treeType.saplingOverlay());
        createAndRegisterDualLayerItemModel(saplingId, treeType.sapling(), treeType.saplingOverlay(),
                bundle.getColoring());
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
        createAndRegisterDualLayerItemModel(resinId, treeType.resin(), treeType.resinOverlay(),
                bundle.getColoring());
    }

    private static void registerFruit(ResourceTreeBundle bundle) {
        var fruitId = bundle.getFruitId();
        var treeType = bundle.getTreeType();
        createAndRegisterDualLayerItemModel(fruitId, treeType.fruit(), treeType.fruitOverlay(),
                bundle.getColoring());
    }

    private static void registerClump(ResourceTreeBundle bundle) {
        if (!bundle.isClumpEnabled()) return;
        var clump = bundle.getClumpId();
        var treeType = bundle.getTreeType();
        createAndRegisterDualLayerItemModel(clump, treeType.clump(), treeType.clumpOverlay(),
                bundle.getClumpColoring());
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

    /**
     * MapleUtilLib {@code whenClient} 入口：清空后重建全部树木模型并写入动态资源包。
     */
    public static void reinitModels(DynamicAssets assets) {
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
                ResourceFarm.LOGGER.error(
                        "构建树模型失败，treeId={}，resourceTree={}，开始跳过该树的后续构建",
                        treeId, resourceTree, e);
            }
        });

        BATCH_REGISTRAR.commit(assets);
    }
}
