package com.maple.resource_farm.common.pack;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.api.addon.AddonFinder;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.utils.RLUtils;

import net.minecraft.SharedConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapFile;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class ResourceFarmDynamicDataPack implements PackResources {

    protected static final ObjectSet<String> SERVER_DOMAINS = new ObjectOpenHashSet<>();
    protected static final ResourceFarmDynamicPackContents CONTENTS = new ResourceFarmDynamicPackContents();

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    public static final FileToIdConverter RECIPE_CONVERTER = FileToIdConverter.json("recipe");
    private static final FileToIdConverter LOOT_TABLE_CONVERTER = FileToIdConverter.json("loot_table");
    private static final FileToIdConverter ADVANCEMENT_CONVERTER = FileToIdConverter.json("advancement");

    public static final boolean DUMP_DATA = ResourceFarmConfigHolder.FarmConfigHolder.dev.dumpData;

    private final PackLocationInfo info;

    static {
        SERVER_DOMAINS.addAll(Sets.newHashSet(ResourceFarm.MOD_ID, "minecraft", "neoforge", "c"));
    }

    public ResourceFarmDynamicDataPack(PackLocationInfo info) {
        this(info, AddonFinder.getAddons().keySet());
    }

    public ResourceFarmDynamicDataPack(PackLocationInfo info, Collection<String> domains) {
        this.info = info;
        synchronized (SERVER_DOMAINS) {
            SERVER_DOMAINS.addAll(domains);
        }
    }

    public static void clearServer() {
        CONTENTS.clearData();
        synchronized (SERVER_DOMAINS) {
            SERVER_DOMAINS.retainAll(Sets.newHashSet(ResourceFarm.MOD_ID, "minecraft", "neoforge", "c"));
        }
    }

    public static void addToData(Identifier location, byte[] bytes) {
        if (CONTENTS.getResource(location) != null) {
            ResourceFarm.LOGGER.warn("Duplicate server resource detected: {}", location);
            return;
        }
        CONTENTS.addToData(location, bytes);
    }

    public static byte[] jsonToBytes(JsonElement json) {
        return GSON.toJson(json).getBytes(StandardCharsets.UTF_8);
    }

    public static void addRecipe(Identifier recipeId, Recipe<?> recipe, @Nullable AdvancementHolder advancement,
                                 HolderLookup.Provider provider) {
        try {
            Identifier targetRecipeLoc = RECIPE_CONVERTER.idToFile(recipeId);
            JsonElement recipeJson = Recipe.CODEC
                    .encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), recipe)
                    .getOrThrow();
            byte[] recipeBytes = jsonToBytes(recipeJson);

            Path parent = ResourceFarm.getGameDir().resolve("resource_farm/dumped/data");
            if (DUMP_DATA) {
                writeJson(targetRecipeLoc, null, parent, recipeBytes);
            }
            addToData(targetRecipeLoc, recipeBytes);

            if (advancement != null) {
                Identifier targetAdvancementLoc = ADVANCEMENT_CONVERTER.idToFile(advancement.id());
                JsonElement advancementJson = Advancement.CODEC
                        .encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), advancement.value())
                        .getOrThrow();
                byte[] advancementBytes = jsonToBytes(advancementJson);
                if (DUMP_DATA) {
                    writeJson(targetAdvancementLoc, null, parent, advancementBytes);
                }
                addToData(targetAdvancementLoc, advancementBytes);
            }
        } catch (Exception e) {
            ResourceFarm.LOGGER.error("Failed to add recipe for {}", recipeId, e);
        }
    }

    public static void addLootTable(Identifier lootTableId, LootTable table, HolderLookup.Provider provider) {
        try {
            Identifier targetLootLoc = LOOT_TABLE_CONVERTER.idToFile(lootTableId);
            if (CONTENTS.getResource(targetLootLoc) != null) {
                ResourceFarm.LOGGER.error("Duplicate loot table: {}", lootTableId);
                return;
            }

            JsonElement lootTableJson = LootTable.DIRECT_CODEC
                    .encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), table).getOrThrow();
            byte[] lootTableBytes = jsonToBytes(lootTableJson);

            Path parent = ResourceFarm.getGameDir().resolve("resource_farm/dumped/data");
            if (DUMP_DATA) {
                writeJson(targetLootLoc, null, parent, lootTableBytes);
            }
            addToData(targetLootLoc, lootTableBytes);
        } catch (Exception e) {
            ResourceFarm.LOGGER.error("Failed to add loot table for {}", lootTableId, e);
        }
    }

    public static <T, R> void addDataMap(DataMapType<R, T> type, DataMapProvider.Builder<T, R> builder,
                                         HolderLookup.Provider provider) {
        try {
            Identifier registryLoc = type.registryKey().identifier();
            Identifier dataMapLoc = type.id();
            String dataMapPath = String.format("data_maps/%s/%s.json", registryLoc.getPath(), dataMapLoc.getPath());
            Identifier dataMapId = RLUtils.get("neoforge", dataMapPath);
            WithConditions<DataMapFile<T, R>> dataMapProvider = builder.build();
            ResourceKey<Registry<R>> registryKey = type.registryKey();
            Codec<DataMapFile<T, R>> dataMapFileCodec = DataMapFile.codec(registryKey, type);
            JsonElement dataMapJson = dataMapFileCodec
                    .encodeStart(
                            provider.createSerializationContext(JsonOps.INSTANCE),
                            dataMapProvider.carrier())
                    .getOrThrow();
            byte[] dataMapBytes = jsonToBytes(dataMapJson);
            Path parent = ResourceFarm.getGameDir().resolve("resource_farm/dumped/data");
            if (DUMP_DATA) {
                writeJson(dataMapId, null, parent, dataMapBytes);
            }
            addToData(dataMapId, dataMapBytes);
        } catch (Exception e) {
            ResourceFarm.LOGGER.error("Failed to add data map for {}", type.id(), e);
        }
    }

    @ApiStatus.Internal
    public static void writeJson(Identifier id, @Nullable String subDir, Path parent, byte[] json) {
        if (json.length == 0) {
            return;
        }
        try {
            Path file;
            String path = id.getPath();
            String string = path.endsWith(".json") ? path : path + ".json";

            if (subDir != null) {
                file = parent.resolve(id.getNamespace()).resolve(subDir).resolve(string);
            } else {
                file = parent.resolve(id.getNamespace()).resolve(string);
            }
            Files.createDirectories(Objects.requireNonNull(file.getParent()));
            try (OutputStream output = Files.newOutputStream(file)) {
                output.write(json);
            }
        } catch (IOException e) {
            ResourceFarm.LOGGER.error("Failed to write JSON export for file {}", id, e);
        }
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        if (elements.length > 0 && elements[0].equals("pack.png")) {
            return () -> Objects.requireNonNull(ResourceFarm.class.getResourceAsStream("/icon.png"));
        }
        return null;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType type, Identifier location) {
        if (type == PackType.SERVER_DATA) {
            return CONTENTS.getResource(location);
        } else {
            return null;
        }
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
        if (packType == PackType.SERVER_DATA) {
            CONTENTS.listResources(namespace, path, resourceOutput);
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        synchronized (SERVER_DOMAINS) {
            return type == PackType.SERVER_DATA ? Collections.unmodifiableSet(SERVER_DOMAINS) : Set.of();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionType<T> metaReader) {
        if (metaReader == PackMetadataSection.SERVER_TYPE || metaReader == PackMetadataSection.FALLBACK_TYPE) {
            PackFormat packFormat = SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA);
            return (T) new PackMetadataSection(
                    Component.literal("Resource Farm dynamic data"),
                    new InclusiveRange<>(packFormat));
        }
        return null;
    }

    @Override
    public PackLocationInfo location() {
        return info;
    }

    @Override
    public void close() {
        // NOOP
    }

    public static Identifier getRecipeLocation(Identifier recipeId) {
        return RECIPE_CONVERTER.idToFile(recipeId);
    }

    public static Identifier getLootTableLocation(Identifier lootTableId) {
        return LOOT_TABLE_CONVERTER.idToFile(lootTableId);
    }

    public static Identifier getAdvancementLocation(Identifier advancementId) {
        return ADVANCEMENT_CONVERTER.idToFile(advancementId);
    }

    public static Identifier getTagLocation(String identifier, Identifier tagId) {
        FileToIdConverter tagConverter = FileToIdConverter.json("tags/" + identifier);
        return tagConverter.idToFile(tagId);
    }
}
