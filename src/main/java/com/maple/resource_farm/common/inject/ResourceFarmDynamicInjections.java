package com.maple.resource_farm.common.inject;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.mixin.RecipeManagerAccessor;
import com.maple.resource_farm.mixin.ServerAdvancementManagerAccessor;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 服务端动态数据<strong>内存直注</strong>暂存区（非数据包）。
 * <p>
 * 客户端模型 / 可走 JSON 的 data 请用 MapleUtilLib {@code REGISTRY.packs()}。
 * 配方引用物品 Tag（如 {@code #minecraft:saplings}）须在标签 bind 之后生成，故仍走本类直注。
 *
 * <h2>加载时序（MC 26.1）</h2>
 * <ol>
 * <li>{@code loadResources} 早期：标签未 bind → 仅战利品 / 堆肥 id</li>
 * <li>loot {@code WritableRegistry} 冻结前：注入战利品对象</li>
 * <li>{@code PendingTags.apply} + {@code TagsUpdatedEvent}：标签就绪</li>
 * <li>{@code updateComponentsAndStaticRegistryTags} RETURN：生成并注入配方 / 进度</li>
 * <li>{@code RecipeManager.finalizeRecipeLoading}：兜底合并</li>
 * </ol>
 */
public final class ResourceFarmDynamicInjections {

    private static final RegistrationInfo DEFAULT_REGISTRATION_INFO = new RegistrationInfo(Optional.empty(), Lifecycle.experimental());
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private static final Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> RECIPES = new ConcurrentHashMap<>();
    private static final Map<Identifier, AdvancementHolder> ADVANCEMENTS = new ConcurrentHashMap<>();
    private static final Map<Identifier, LootTable> LOOT_TABLES = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Item>, Compostable> COMPOSTABLES = new ConcurrentHashMap<>();

    /** 本轮 reload 是否已完成配方生成（避免 finalize 与 updateTags 双次生成） */
    private static final AtomicBoolean RECIPES_BUILT = new AtomicBoolean(false);

    private ResourceFarmDynamicInjections() {}

    public static void clear() {
        RECIPES.clear();
        ADVANCEMENTS.clear();
        LOOT_TABLES.clear();
        COMPOSTABLES.clear();
        RECIPES_BUILT.set(false);
    }

    // ========================= Recipe =========================

    public static void addRecipe(ResourceKey<Recipe<?>> id, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
        RECIPES.put(id, new RecipeHolder<>(id, recipe));
        if (advancement != null) {
            ADVANCEMENTS.put(advancement.id(), advancement);
        }
    }

    public static boolean markRecipesBuilt() {
        return RECIPES_BUILT.compareAndSet(false, true);
    }

    /**
     * 将暂存配方合并进已有 {@link RecipeMap}。
     */
    public static RecipeMap mergeRecipes(RecipeMap vanilla) {
        if (RECIPES.isEmpty()) {
            return vanilla;
        }
        Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey = new ConcurrentHashMap<>();
        for (RecipeHolder<?> h : vanilla.values()) {
            byKey.put(h.id(), h);
        }
        byKey.putAll(RECIPES);
        RecipeMap merged = RecipeMap.create(byKey.values());
        ResourceFarm.LOGGER.info("[ResourceFarm] Injected {} recipe(s) into RecipeMap (total {})",
                RECIPES.size(), byKey.size());
        return merged;
    }

    public static void injectRecipes(RecipeManager manager) {
        if (RECIPES.isEmpty()) {
            return;
        }
        RecipeManagerAccessor acc = (RecipeManagerAccessor) manager;
        RecipeMap merged = mergeRecipes(acc.resource_farm$getRecipes());
        acc.resource_farm$setRecipes(merged);
    }

    // ========================= Advancement =========================

    /**
     * 在进度树已 apply 之后合并动态进度并重建树。
     */
    public static void injectAdvancements(ServerAdvancementManager manager) {
        if (ADVANCEMENTS.isEmpty()) {
            return;
        }
        ServerAdvancementManagerAccessor acc = (ServerAdvancementManagerAccessor) manager;
        ImmutableMap.Builder<Identifier, AdvancementHolder> builder = ImmutableMap.builder();
        Map<Identifier, AdvancementHolder> existing = acc.resource_farm$getAdvancements();
        for (Map.Entry<Identifier, AdvancementHolder> e : existing.entrySet()) {
            if (!ADVANCEMENTS.containsKey(e.getKey())) {
                builder.put(e.getKey(), e.getValue());
            }
        }
        ADVANCEMENTS.forEach(builder::put);
        Map<Identifier, AdvancementHolder> merged = builder.buildOrThrow();
        acc.resource_farm$setAdvancements(merged);

        AdvancementTree tree = new AdvancementTree();
        tree.addAll(merged.values());
        for (AdvancementNode root : tree.roots()) {
            if (root.holder().value().display().isPresent()) {
                TreeNodePosition.run(root);
            }
        }
        acc.resource_farm$setTree(tree);
        ResourceFarm.LOGGER.info("[ResourceFarm] Injected {} advancement(s) into ServerAdvancementManager",
                ADVANCEMENTS.size());
    }

    // ========================= Loot =========================

    public static void addLootTable(Identifier id, LootTable table) {
        LOOT_TABLES.put(id, table);
    }

    @SuppressWarnings("unchecked")
    public static void injectLootTables(List<? extends WritableRegistry<?>> registries) {
        if (LOOT_TABLES.isEmpty()) {
            return;
        }
        for (WritableRegistry<?> registry : registries) {
            if (registry.key() != Registries.LOOT_TABLE) {
                continue;
            }
            WritableRegistry<LootTable> lootRegistry = (WritableRegistry<LootTable>) registry;
            int n = 0;
            for (Map.Entry<Identifier, LootTable> e : LOOT_TABLES.entrySet()) {
                Identifier id = e.getKey();
                LootTable table = e.getValue();
                table.setLootTableId(id);
                ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, id);
                if (lootRegistry.containsKey(key)) {
                    ResourceFarm.LOGGER.warn("[ResourceFarm] Loot table already registered, skip: {}", id);
                    continue;
                }
                lootRegistry.register(key, table, DEFAULT_REGISTRATION_INFO);
                n++;
            }
            ResourceFarm.LOGGER.info("[ResourceFarm] Injected {} loot table(s) into LOOT_TABLE registry", n);
            return;
        }
        ResourceFarm.LOGGER.warn("[ResourceFarm] LOOT_TABLE WritableRegistry not found; {} table(s) dropped",
                LOOT_TABLES.size());
    }

    // ========================= Compostable DataMap =========================

    public static void addCompostable(ResourceKey<Item> itemKey, Compostable value) {
        COMPOSTABLES.put(itemKey, value);
    }

    public static void addCompostable(Identifier itemId, Compostable value) {
        addCompostable(ResourceKey.create(Registries.ITEM, itemId), value);
    }

    public static int injectCompostables(Map<ResourceKey<Item>, Compostable> target) {
        if (COMPOSTABLES.isEmpty() || target == null) {
            return 0;
        }
        try {
            target.putAll(COMPOSTABLES);
        } catch (UnsupportedOperationException ex) {
            ResourceFarm.LOGGER.error(
                    "[ResourceFarm] Compostable DataMap is immutable (size={}); injection failed",
                    target.size(), ex);
            return 0;
        }
        ResourceFarm.LOGGER.info("[ResourceFarm] Injected {} compostable(s) into ITEM data map", COMPOSTABLES.size());
        return COMPOSTABLES.size();
    }

    public static void dumpAll(HolderLookup.Provider provider) {
        if (!ResourceFarmConfigHolder.FarmConfigHolder.dev.dumpData) {
            return;
        }
        Path parent = ResourceFarm.getGameDir().resolve("resource_farm/dumped/data");
        try {
            for (RecipeHolder<?> holder : RECIPES.values()) {
                JsonElement json = Recipe.CODEC
                        .encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), holder.value())
                        .getOrThrow();
                writeDump(parent, "recipe/" + holder.id().identifier().getNamespace() + "/" + holder.id().identifier().getPath() + ".json", json);
            }
            for (Map.Entry<Identifier, LootTable> e : LOOT_TABLES.entrySet()) {
                JsonElement json = LootTable.DIRECT_CODEC
                        .encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), e.getValue())
                        .getOrThrow();
                writeDump(parent, "loot_table/" + e.getKey().getNamespace() + "/" + e.getKey().getPath() + ".json", json);
            }
        } catch (Exception ex) {
            ResourceFarm.LOGGER.error("[ResourceFarm] Failed dumping dynamic data", ex);
        }
    }

    private static void writeDump(Path parent, String relative, JsonElement json) throws IOException {
        Path file = parent.resolve(relative);
        Files.createDirectories(Objects.requireNonNull(file.getParent()));
        byte[] bytes = GSON.toJson(json).getBytes(StandardCharsets.UTF_8);
        try (OutputStream out = Files.newOutputStream(file)) {
            out.write(bytes);
        }
    }
}
