package com.maple.resource_farm.common.pack;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.api.addon.AddonFinder;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;

import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.InclusiveRange;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public class ResourceFarmDynamicResourcePack implements PackResources {

    protected static final ObjectSet<String> CLIENT_DOMAINS = new ObjectOpenHashSet<>();
    protected static final ResourceFarmDynamicPackContents CONTENTS = new ResourceFarmDynamicPackContents();

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private static final FileToIdConverter ATLAS_ID_CONVERTER = FileToIdConverter.json("atlases");
    private static final FileToIdConverter TEXTURE_ID_CONVERTER = SpriteSource.TEXTURE_ID_CONVERTER;
    private static final FileToIdConverter BLOCKSTATE_ID_CONVERTER = FileToIdConverter.json("blockstates");
    private static final FileToIdConverter BLOCK_MODEL_ID_CONVERTER = FileToIdConverter.json("models/block");
    private static final FileToIdConverter ITEM_MODEL_ID_CONVERTER = FileToIdConverter.json("models/item");
    /** MC 26.1+：物品 → 模型映射定义（assets/.../items/*.json） */
    private static final FileToIdConverter ITEM_DEFINITION_ID_CONVERTER = FileToIdConverter.json("items");

    private final PackLocationInfo info;

    static {
        CLIENT_DOMAINS.addAll(Sets.newHashSet(ResourceFarm.MOD_ID, "minecraft", "neoforge", "c"));
    }

    public ResourceFarmDynamicResourcePack(PackLocationInfo info) {
        this(info, AddonFinder.getAddons().keySet());
    }

    public ResourceFarmDynamicResourcePack(PackLocationInfo info, Collection<String> domains) {
        this.info = info;
        synchronized (CLIENT_DOMAINS) {
            CLIENT_DOMAINS.addAll(domains);
        }
    }

    public static void clearClient() {
        CONTENTS.clearData();
        synchronized (CLIENT_DOMAINS) {
            CLIENT_DOMAINS.retainAll(Sets.newHashSet(ResourceFarm.MOD_ID, "minecraft", "neoforge", "c"));
        }
    }

    private static void addResourceInternal(Identifier targetLoc, byte[] data) {
        if (ResourceFarmConfigHolder.FarmConfigHolder.dev.dumpAssets) {
            Path parent = ResourceFarm.getGameDir().resolve("resource_farm/dumped/assets");
            writeByteArray(targetLoc, null, parent, data);
        }
        CONTENTS.addToData(targetLoc, data);
    }

    private static byte[] jsonToBytes(JsonElement json) {
        return GSON.toJson(json).getBytes(StandardCharsets.UTF_8);
    }

    public static void addResource(Identifier location, JsonElement obj) {
        addResource(location, jsonToBytes(obj));
    }

    public static void addResource(Identifier location, byte[] data) {
        addResourceInternal(location, data);
    }

    public static void addBlockModel(Identifier loc, JsonElement obj) {
        Identifier targetLoc = getBlockModelLocation(loc);
        addResourceInternal(targetLoc, jsonToBytes(obj));
    }

    public static void addBlockModel(Identifier loc, Supplier<JsonElement> obj) {
        addBlockModel(loc, obj.get());
    }

    public static void addItemModel(Identifier loc, JsonElement obj) {
        Identifier targetLoc = getItemModelLocation(loc);
        addResourceInternal(targetLoc, jsonToBytes(obj));
    }

    public static void addItemModel(Identifier loc, Supplier<JsonElement> obj) {
        addItemModel(loc, obj.get());
    }

    /**
     * 写入 MC 26.1+ 的客户端物品定义：{@code assets/&lt;ns&gt;/items/&lt;path&gt;.json}，
     * 将物品 ID 映射到具体模型（及可选 tints）。
     */
    public static void addItemDefinition(Identifier loc, JsonElement obj) {
        Identifier targetLoc = getItemDefinitionLocation(loc);
        addResourceInternal(targetLoc, jsonToBytes(obj));
    }

    public static void addItemDefinition(Identifier loc, Supplier<JsonElement> obj) {
        addItemDefinition(loc, obj.get());
    }

    public static void addBlockState(Identifier loc, JsonElement stateJson) {
        Identifier targetLoc = getBlockStateLocation(loc);
        addResourceInternal(targetLoc, jsonToBytes(stateJson));
    }

    public static void addBlockState(Identifier loc, Supplier<JsonElement> generator) {
        addBlockState(loc, generator.get());
    }

    public static void addAtlasSpriteSource(Identifier atlasLoc, SpriteSource source) {
        addAtlasSpriteSourceList(atlasLoc, Collections.singletonList(source));
    }

    public static void addAtlasSpriteSourceList(Identifier atlasLoc, List<SpriteSource> sources) {
        try {
            Identifier targetLoc = getAtlasLocation(atlasLoc);
            JsonElement sourceJson = SpriteSources.FILE_CODEC.encodeStart(JsonOps.INSTANCE, sources).getOrThrow();
            addResourceInternal(targetLoc, jsonToBytes(sourceJson));
        } catch (Exception e) {
            ResourceFarm.LOGGER.error("Failed to add atlas sprite source for {}", atlasLoc, e);
        }
    }

    public static void addBlockTexture(Identifier loc, byte[] data) {
        Identifier targetLoc = getTextureLocation("block", loc);
        addResourceInternal(targetLoc, data);
    }

    public static void addItemTexture(Identifier loc, byte[] data) {
        Identifier targetLoc = getTextureLocation("item", loc);
        addResourceInternal(targetLoc, data);
    }

    @ApiStatus.Internal
    public static void writeByteArray(Identifier id, @Nullable String subdir, Path parent, byte[] data) {
        if (data.length == 0) {
            return;
        }
        try {
            Path file;
            if (subdir != null) {
                file = parent.resolve(id.getNamespace()).resolve(subdir).resolve(id.getPath() + ".png");
            } else {
                file = parent.resolve(id.getNamespace()).resolve(id.getPath());
            }
            Files.createDirectories(Objects.requireNonNull(file.getParent()));
            try (OutputStream output = Files.newOutputStream(file)) {
                output.write(data);
            }
        } catch (IOException e) {
            ResourceFarm.LOGGER.error("Failed to write binary export for file {}", id, e);
        }
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... elements) {
        if (elements.length > 0 && elements[0].equals("pack.png")) {
            return () -> Objects.requireNonNull(ResourceFarm.class.getResourceAsStream("/icon.png"));
        }
        return null;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType type, Identifier location) {
        if (type == PackType.CLIENT_RESOURCES) {
            return CONTENTS.getResource(location);
        }
        return null;
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
        if (packType == PackType.CLIENT_RESOURCES) {
            CONTENTS.listResources(namespace, path, resourceOutput);
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        synchronized (CLIENT_DOMAINS) {
            return type == PackType.CLIENT_RESOURCES ? Collections.unmodifiableSet(CLIENT_DOMAINS) : Set.of();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionType<T> metaReader) {
        if (metaReader == PackMetadataSection.CLIENT_TYPE || metaReader == PackMetadataSection.FALLBACK_TYPE) {
            PackFormat packFormat = SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES);
            return (T) new PackMetadataSection(
                    Component.literal("Resource Farm dynamic assets"),
                    new InclusiveRange<>(packFormat));
        }
        return null;
    }

    @Override
    public @NotNull PackLocationInfo location() {
        return info;
    }

    public boolean isBuiltin() {
        return true;
    }

    @Override
    public void close() {
        // NOOP
    }

    public static Identifier getBlockStateLocation(Identifier blockId) {
        return BLOCKSTATE_ID_CONVERTER.idToFile(blockId);
    }

    public static Identifier getBlockModelLocation(Identifier blockId) {
        return BLOCK_MODEL_ID_CONVERTER.idToFile(blockId);
    }

    public static Identifier getItemModelLocation(Identifier itemId) {
        return ITEM_MODEL_ID_CONVERTER.idToFile(itemId);
    }

    /** {@code assets/&lt;ns&gt;/items/&lt;path&gt;.json} */
    public static Identifier getItemDefinitionLocation(Identifier itemId) {
        return ITEM_DEFINITION_ID_CONVERTER.idToFile(itemId);
    }

    public static Identifier getTextureLocation(@Nullable String path, Identifier textureId) {
        if (path == null) {
            return TEXTURE_ID_CONVERTER.idToFile(textureId);
        }
        return TEXTURE_ID_CONVERTER.idToFile(textureId.withPrefix(path + "/"));
    }

    public static Identifier getAtlasLocation(Identifier atlasId) {
        return ATLAS_ID_CONVERTER.idToFile(atlasId);
    }
}
