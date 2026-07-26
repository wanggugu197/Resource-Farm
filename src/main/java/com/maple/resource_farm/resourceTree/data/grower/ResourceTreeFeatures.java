package com.maple.resource_farm.resourceTree.data.grower;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceFarmMaps;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement.ResourceTreeMap;

/**
 * 资源树生长用的配置特征工厂。
 */
public final class ResourceTreeFeatures {

    private static final Identifier FALLBACK = Identifier.withDefaultNamespace("oak");

    /** 已成功解析的结构缓存：configured_feature id → 结构骨架 */
    private static final Object2ObjectOpenHashMap<Identifier, ResourceTreeConfiguration> CONFIG_CACHE = new Object2ObjectOpenHashMap<>();

    /** 确认不存在或解析失败的 id，避免重复读盘 */
    private static final ObjectOpenHashSet<Identifier> MISSING = new ObjectOpenHashSet<>();

    private ResourceTreeFeatures() {}

    /**
     * 是否可解析为树结构配置（会触发懒加载并缓存结果）。
     */
    public static boolean isAvailable(Identifier featureId) {
        return featureId != null && getOrLoad(featureId) != null;
    }

    /**
     * 按需加载并返回结构骨架；失败返回 null（并记入缺失缓存）。
     */
    @Nullable
    public static ResourceTreeConfiguration getOrLoad(Identifier featureId) {
        if (featureId == null) {
            return null;
        }
        ResourceTreeConfiguration cached = CONFIG_CACHE.get(featureId);
        if (cached != null) {
            return cached;
        }
        if (MISSING.contains(featureId)) {
            return null;
        }

        ResourceTreeConfiguration loaded = loadFromConfiguredFeatureJson(featureId);
        if (loaded != null) {
            CONFIG_CACHE.put(featureId, loaded);
            return loaded;
        }
        MISSING.add(featureId);
        return null;
    }

    /**
     * 为指定资源树构建可放置的 {@link ConfiguredFeature}（注入该树的原木/树叶）。
     */
    public static ConfiguredFeature<?, ?> getResourceTreeConfiguredFeature(String treeId, Identifier styleId) {
        ResourceTreeConfiguration structure = resolveStructure(styleId);

        var treeHolder = ResourceTreeMap.get(treeId);
        if (treeHolder == null) {
            throw new IllegalStateException("Resource tree not registered: " + treeId);
        }
        Block logBlock = treeHolder.getLog().get();
        Block leavesBlock = treeHolder.getLeaves().get();

        TreeConfiguration config = structure.build(
                BlockStateProvider.simple(logBlock.defaultBlockState()),
                BlockStateProvider.simple(leavesBlock.defaultBlockState()));
        return new ConfiguredFeature<>(Feature.TREE, config);
    }

    private static ResourceTreeConfiguration resolveStructure(Identifier styleId) {
        ResourceTreeConfiguration structure = getOrLoad(styleId);
        if (structure != null) {
            return structure;
        }
        ResourceFarm.LOGGER.warn(
                "[ResourceFarm] Unknown configured_feature tree style: {}, falling back to {}",
                styleId, FALLBACK);
        structure = getOrLoad(FALLBACK);
        if (structure == null) {
            throw new IllegalStateException(
                    "No tree structure available (missing " + styleId + " and fallback " + FALLBACK + ")");
        }
        return structure;
    }

    /**
     * 从 {@code data/<ns>/<path>.json} 解析树结构。
     * 仅接受 {@code type = minecraft:tree}（或其 config 直接是 TreeConfiguration）。
     */
    @Nullable
    private static ResourceTreeConfiguration loadFromConfiguredFeatureJson(Identifier id) {
        String relativePath = "data/" + id.getNamespace() + "/" + id.getPath() + ".json";
        try (InputStream in = ResourceFarmMaps.openRelative(relativePath)) {
            if (in == null) {
                ResourceFarm.LOGGER.debug("[ResourceFarm] configured_feature not found: {}", relativePath);
                return null;
            }
            TreeConfiguration treeConfig = parseTreeConfiguration(in, id);
            if (treeConfig == null) {
                return null;
            }
            return ResourceTreeConfiguration.fromVanilla(treeConfig);
        } catch (Exception e) {
            ResourceFarm.LOGGER.error(
                    "[ResourceFarm] Failed loading configured_feature {}: {}", id, e.toString());
            return null;
        }
    }

    @Nullable
    private static TreeConfiguration parseTreeConfiguration(InputStream in, Identifier id) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            JsonElement json = JsonParser.parseReader(reader);
            if (!json.isJsonObject()) {
                ResourceFarm.LOGGER.error("[ResourceFarm] configured_feature {} is not a JSON object", id);
                return null;
            }
            JsonObject obj = json.getAsJsonObject();
            JsonElement configElement;
            if (obj.has("config")) {
                if (obj.has("type")) {
                    Identifier typeId = Identifier.CODEC.parse(JsonOps.INSTANCE, obj.get("type"))
                            .result()
                            .orElse(null);
                    if (typeId != null && !typeId.equals(Identifier.withDefaultNamespace("tree"))) {
                        ResourceFarm.LOGGER.error(
                                "[ResourceFarm] configured_feature {} type is {}, expected minecraft:tree",
                                id, typeId);
                        return null;
                    }
                }
                configElement = obj.get("config");
            } else {
                configElement = obj;
            }

            RegistryAccess registryAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
            RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registryAccess);

            return TreeConfiguration.CODEC.parse(ops, configElement)
                    .resultOrPartial(err -> ResourceFarm.LOGGER.error(
                            "[ResourceFarm] Invalid configured_feature tree {}: {}", id, err))
                    .orElse(null);
        } catch (Exception e) {
            ResourceFarm.LOGGER.error("[ResourceFarm] Failed parsing configured_feature {}: {}", id, e.toString());
            return null;
        }
    }
}
