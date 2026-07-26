package com.maple.resource_farm.resourceTree.data.dataMaps;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.resourceTree.PresetResourceTreeConfigHolder;
import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.resourceTree.data.ResourceTree;

import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModList;
import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.locating.IModFile;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mapleutillib.utils.RLUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * 资源树数据包统一入口：表存储 + 扫描加载 + jar 资源访问。
 * <p>
 * 路径根：{@code data/&lt;ns&gt;/resource_farm_maps/}
 * <ul>
 * <li>{@code tree_base_type} → {@link #BASE_TYPES}</li>
 * <li>{@code tree_extra_type} → {@link #EXTRA_TYPES}</li>
 * <li>{@code resource_tree_grower} → {@link #GROWERS}</li>
 * <li>{@code resource_tree} → {@link ResourceTreeAccessManagement#ResourceTreeMap}</li>
 * </ul>
 */
public final class ResourceFarmMaps {

    private static final String REGISTRY_NS = ResourceFarm.MOD_ID + "_maps";
    private static final String MAPS_ROOT = "/" + REGISTRY_NS + "/";
    private static final String BASE_MARKER = MAPS_ROOT + "tree_base_type/";
    private static final String EXTRA_MARKER = MAPS_ROOT + "tree_extra_type/";
    private static final String GROWER_MARKER = MAPS_ROOT + "resource_tree_grower/";
    private static final String TREE_MARKER = MAPS_ROOT + "resource_tree/";
    private static final String REMOVE_MARKER = MAPS_ROOT + "resource_tree_remove/";

    /** 树样式表 */
    public static final Object2ObjectOpenHashMap<Identifier, ResourceTreeBaseType> BASE_TYPES = new Object2ObjectOpenHashMap<>();
    /** 矿/叠加样式表 */
    public static final Object2ObjectOpenHashMap<Identifier, ResourceTreeExtraType> EXTRA_TYPES = new Object2ObjectOpenHashMap<>();
    /** 共享生长器表（键 = grower JSON id，无 treeId 绑定） */
    public static final Object2ObjectOpenHashMap<Identifier, ResourceTreeGrower> GROWERS = new Object2ObjectOpenHashMap<>();

    private static boolean loaded;

    private ResourceFarmMaps() {}

    // -------------------------------------------------------------------------
    // 查询
    // -------------------------------------------------------------------------

    public static ResourceTreeBaseType getBaseType(Identifier id) {
        ResourceTreeBaseType type = BASE_TYPES.get(id);
        return type != null ? type : ResourceTreeBaseType.DEFAULT;
    }

    public static ResourceTreeExtraType getExtraType(Identifier id) {
        ResourceTreeExtraType type = EXTRA_TYPES.get(id);
        return type != null ? type : ResourceTreeExtraType.DEFAULT;
    }

    @Nullable
    public static ResourceTreeGrower getGrower(Identifier id) {
        return id == null ? null : GROWERS.get(id);
    }

    // -------------------------------------------------------------------------
    // 加载
    // -------------------------------------------------------------------------

    /** 幂等：扫描数据包并写入上表与 {@link ResourceTreeAccessManagement#ResourceTreeMap}。 */
    public static void load() {
        if (loaded) {
            return;
        }
        loaded = true;

        ObjectArrayList<PendingTree> pendingTrees = new ObjectArrayList<>();
        ObjectArrayList<String> removals = new ObjectArrayList<>();
        int base, extra, grower, treeJson, remove;

        visitDataJsonContaining(MAPS_ROOT, (path, opener) -> {
            // remove 必须先于 tree 判断（路径含 resource_tree 前缀）
            if (path.contains(REMOVE_MARKER)) {
                Identifier id = parseEntryId(path, REMOVE_MARKER);
                try (InputStream in = opener.open()) {
                    String rid = parseRemoval(in, id);
                    if (rid != null && !rid.isBlank()) {
                        removals.add(rid);
                    }
                } catch (Exception e) {
                    ResourceFarm.LOGGER.error(
                            "[ResourceFarm] Failed reading resource_tree_remove {}: {}", id, e.toString());
                }
                return;
            }
            if (path.contains(BASE_MARKER)) {
                Identifier id = parseEntryId(path, BASE_MARKER);
                if (id == null) return;
                try (InputStream in = opener.open()) {
                    ResourceTreeBaseType type = parseCodec(in, ResourceTreeBaseType.CODEC, id, "tree_base_type");
                    if (type != null) {
                        BASE_TYPES.put(id, type);
                    }
                } catch (Exception e) {
                    ResourceFarm.LOGGER.error("[ResourceFarm] Failed reading tree_base_type {}: {}", id, e.toString());
                }
            } else if (path.contains(EXTRA_MARKER)) {
                Identifier id = parseEntryId(path, EXTRA_MARKER);
                if (id == null) return;
                try (InputStream in = opener.open()) {
                    ResourceTreeExtraType type = parseCodec(in, ResourceTreeExtraType.CODEC, id, "tree_extra_type");
                    if (type != null) {
                        EXTRA_TYPES.put(id, type);
                    }
                } catch (Exception e) {
                    ResourceFarm.LOGGER.error("[ResourceFarm] Failed reading tree_extra_type {}: {}", id, e.toString());
                }
            } else if (path.contains(GROWER_MARKER)) {
                Identifier id = parseEntryId(path, GROWER_MARKER);
                if (id == null) return;
                try (InputStream in = opener.open()) {
                    ResourceTreeGrower g = parseCodec(in, ResourceTreeGrower.CODEC, id, "resource_tree_grower");
                    if (g != null) {
                        GROWERS.put(id, g);
                    }
                } catch (Exception e) {
                    ResourceFarm.LOGGER.error("[ResourceFarm] Failed reading grower {}: {}", id, e.toString());
                }
            } else if (path.contains(TREE_MARKER)) {
                Identifier id = parseEntryId(path, TREE_MARKER);
                if (id == null) return;
                try (InputStream in = opener.open()) {
                    JsonElement json = JsonParser.parseReader(
                            new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)));
                    pendingTrees.add(new PendingTree(id, json));
                } catch (Exception e) {
                    ResourceFarm.LOGGER.error("[ResourceFarm] Failed reading resource_tree {}: {}", id, e.toString());
                }
            }
        });

        base = BASE_TYPES.size();
        extra = EXTRA_TYPES.size();
        grower = GROWERS.size();
        treeJson = pendingTrees.size();
        remove = removals.size();

        if (BASE_TYPES.isEmpty()) {
            ResourceFarm.LOGGER.warn("[ResourceFarm] No tree_base_type JSON; using DEFAULT oak");
            BASE_TYPES.put(Identifier.withDefaultNamespace("oak"), ResourceTreeBaseType.DEFAULT);
        }
        if (EXTRA_TYPES.isEmpty()) {
            ResourceFarm.LOGGER.warn("[ResourceFarm] No tree_extra_type JSON; using DEFAULT iron");
            EXTRA_TYPES.put(Identifier.withDefaultNamespace("iron"), ResourceTreeExtraType.DEFAULT);
        }

        ResourceFarm.LOGGER.info(
                "[ResourceFarm] resource_farm_maps: {} base, {} extra, {} grower, {} tree json, {} remove",
                base, extra, grower, treeJson, remove);

        applyTrees(pendingTrees, removals);
    }

    private static void applyTrees(ObjectArrayList<PendingTree> pendingTrees, ObjectArrayList<String> removals) {
        int registered = 0, skipped = 0;
        for (PendingTree pending : pendingTrees) {
            try {
                ResourceTreeConfig.Parsed parsed = ResourceTreeConfig.fromDatapack(pending.json());
                if (!shouldLoadGroup(parsed.group())) {
                    skipped++;
                    continue;
                }
                ResourceTreeConfig config = parsed.config();
                String treeId = config.id() + "_tree";
                if (getGrower(config.growerId()) == null) {
                    throw new IllegalStateException(
                            "Grower key " + config.growerId() + " not in GROWERS for tree " + treeId);
                }
                ResourceTreeAccessManagement.ResourceTreeMap.put(treeId, ResourceTree.create(config));
                registered++;
            } catch (Exception e) {
                ResourceFarm.LOGGER.error(
                        "[ResourceFarm] Failed applying resource tree {}: {}", pending.fileId(), e.toString());
            }
        }

        int removed = 0;
        for (String raw : removals) {
            String treeId = normalizeTreeId(raw);
            if (ResourceTreeAccessManagement.ResourceTreeMap.remove(treeId) != null) {
                removed++;
            }
        }

        ResourceFarm.LOGGER.info(
                "[ResourceFarm] Resource tree registration: applied {} tree(s) (skipped {} by preset), removed {}.",
                registered, skipped, removed);
    }

    private static boolean shouldLoadGroup(@Nullable String group) {
        if (group == null) {
            return true;
        }
        PresetResourceTreeConfigHolder holder = ResourceFarmConfigHolder.treeConfigHolder;
        if (holder == null) {
            return true;
        }
        String g = group.toLowerCase(Locale.ROOT);
        if (!holder.enablePresetTreeGroups) {
            return switch (g) {
                case "base", "mineral", "biology", "agriculture" -> false;
                default -> true;
            };
        }
        PresetResourceTreeConfigHolder.PresetTreeGenerationConfigs c = holder.presetTreeGeneration;
        return switch (g) {
            case "base" -> c.minecraftBase;
            case "mineral" -> c.minecraftMineral;
            case "biology" -> c.minecraftBiology;
            case "agriculture" -> c.minecraftAgriculture;
            default -> true;
        };
    }

    private static String normalizeTreeId(String raw) {
        String id = raw.trim();
        return id.endsWith("_tree") ? id : id + "_tree";
    }

    // -------------------------------------------------------------------------
    // jar / 数据包 IO（供本类与 ResourceTreeFeatures 使用）
    // -------------------------------------------------------------------------

    /**
     * 按 jar 内相对路径打开资源（如 {@code data/minecraft/worldgen/configured_feature/oak.json}）。
     * 多 mod 同路径时返回第一个命中。
     */
    @Nullable
    public static InputStream openRelative(String relativePath) {
        String normalized = relativePath.replace('\\', '/');
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        for (IModFileInfo info : ModList.get().getModFiles()) {
            try {
                IModFile file = info.getFile();
                JarContents contents = file.getContents();
                if (contents == null) {
                    continue;
                }
                var resource = contents.get(normalized);
                if (resource != null) {
                    return resource.open();
                }
            } catch (Exception e) {
                ResourceFarm.LOGGER.error(
                        "[ResourceFarm] Error opening {} from {}: {}",
                        normalized, info.getFile().getFileName(), e.toString());
            }
        }
        return null;
    }

    private static void visitDataJsonContaining(String pathContains, BiConsumer<String, InputStreamOpener> consumer) {
        for (IModFileInfo info : ModList.get().getModFiles()) {
            try {
                IModFile file = info.getFile();
                JarContents contents = file.getContents();
                if (contents == null) {
                    continue;
                }
                contents.visitContent("data", (relativePath, resource) -> {
                    String path = relativePath.replace('\\', '/');
                    if (path.endsWith(".json") && path.contains(pathContains)) {
                        consumer.accept(path, resource::open);
                    }
                });
            } catch (Exception e) {
                ResourceFarm.LOGGER.error(
                        "[ResourceFarm] Error scanning mod file {}: {}",
                        info.getFile().getFileName(), e.toString());
            }
        }
    }

    @Nullable
    private static Identifier parseEntryId(String relativePath, String marker) {
        String rest = relativePath.replace('\\', '/');
        if (rest.startsWith("data/")) {
            rest = rest.substring(5);
        }
        int markerIdx = rest.indexOf(marker);
        if (markerIdx < 0) {
            return null;
        }
        String namespace = rest.substring(0, markerIdx);
        String entry = rest.substring(markerIdx + marker.length());
        if (entry.endsWith(".json")) {
            entry = entry.substring(0, entry.length() - 5);
        }
        if (namespace.isEmpty() || entry.isEmpty()) {
            return null;
        }
        return RLUtils.get(namespace, entry);
    }

    @Nullable
    private static <T> T parseCodec(InputStream in, Codec<T> codec, Identifier id, String kind) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            JsonElement json = JsonParser.parseReader(reader);
            return codec.parse(JsonOps.INSTANCE, json)
                    .resultOrPartial(err -> ResourceFarm.LOGGER.error(
                            "[ResourceFarm] Invalid {} {}: {}", kind, id, err))
                    .orElse(null);
        } catch (Exception e) {
            ResourceFarm.LOGGER.error("[ResourceFarm] Failed parsing {} {}: {}", kind, id, e.toString());
            return null;
        }
    }

    @Nullable
    private static String parseRemoval(InputStream in, @Nullable Identifier fileId) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            JsonElement json = JsonParser.parseReader(reader);
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
                return json.getAsString();
            }
            if (json.isJsonObject()) {
                var obj = json.getAsJsonObject();
                if (obj.has("id")) return obj.get("id").getAsString();
                if (obj.has("tree_id")) return obj.get("tree_id").getAsString();
                if (obj.has("treeId")) return obj.get("treeId").getAsString();
            }
            if (fileId != null) {
                String path = fileId.getPath();
                int slash = path.lastIndexOf('/');
                return slash >= 0 ? path.substring(slash + 1) : path;
            }
            return null;
        } catch (Exception e) {
            ResourceFarm.LOGGER.error("[ResourceFarm] Failed parsing resource_tree_remove: {}", e.toString());
            return null;
        }
    }

    @FunctionalInterface
    private interface InputStreamOpener {

        InputStream open() throws Exception;
    }

    private record PendingTree(Identifier fileId, JsonElement json) {}
}
