package com.resource_farm.common.pack;

import com.resource_farm.ResourceFarm;
import com.resource_farm.api.addon.AddonFinder;
import com.resource_farm.config.ResourceFarmConfigHolder;

import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;

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

    private static void addResourceInternal(ResourceLocation targetLoc, byte[] data) {
        if (ResourceFarmConfigHolder.FarmConfigHolder.dev.dumpAssets) {
            Path parent = ResourceFarm.getGameDir().resolve("resource_farm/dumped/assets");
            writeByteArray(targetLoc, null, parent, data);
        }
        CONTENTS.addToData(targetLoc, data);
    }

    private static byte[] jsonToBytes(JsonElement json) {
        return GSON.toJson(json).getBytes(StandardCharsets.UTF_8);
    }

    public static void addResource(ResourceLocation location, JsonElement obj) {
        addResource(location, jsonToBytes(obj));
    }

    public static void addResource(ResourceLocation location, byte[] data) {
        addResourceInternal(location, data);
    }

    public static void addBlockModel(ResourceLocation loc, JsonElement obj) {
        ResourceLocation targetLoc = getBlockModelLocation(loc);
        addResourceInternal(targetLoc, jsonToBytes(obj));
    }

    public static void addBlockModel(ResourceLocation loc, Supplier<JsonElement> obj) {
        addBlockModel(loc, obj.get());
    }

    public static void addItemModel(ResourceLocation loc, JsonElement obj) {
        ResourceLocation targetLoc = getItemModelLocation(loc);
        addResourceInternal(targetLoc, jsonToBytes(obj));
    }

    public static void addItemModel(ResourceLocation loc, Supplier<JsonElement> obj) {
        addItemModel(loc, obj.get());
    }

    public static void addBlockState(ResourceLocation loc, JsonElement stateJson) {
        ResourceLocation targetLoc = getBlockStateLocation(loc);
        addResourceInternal(targetLoc, jsonToBytes(stateJson));
    }

    public static void addBlockState(ResourceLocation loc, Supplier<JsonElement> generator) {
        addBlockState(loc, generator.get());
    }

    public static void addAtlasSpriteSource(ResourceLocation atlasLoc, SpriteSource source) {
        addAtlasSpriteSourceList(atlasLoc, Collections.singletonList(source));
    }

    public static void addAtlasSpriteSourceList(ResourceLocation atlasLoc, List<SpriteSource> sources) {
        try {
            ResourceLocation targetLoc = getAtlasLocation(atlasLoc);
            JsonElement sourceJson = SpriteSources.FILE_CODEC.encodeStart(JsonOps.INSTANCE, sources).getOrThrow();
            addResourceInternal(targetLoc, jsonToBytes(sourceJson));
        } catch (Exception e) {
            ResourceFarm.LOGGER.error("Failed to add atlas sprite source for {}", atlasLoc, e);
        }
    }

    public static void addBlockTexture(ResourceLocation loc, byte[] data) {
        ResourceLocation targetLoc = getTextureLocation("block", loc);
        addResourceInternal(targetLoc, data);
    }

    public static void addItemTexture(ResourceLocation loc, byte[] data) {
        ResourceLocation targetLoc = getTextureLocation("item", loc);
        addResourceInternal(targetLoc, data);
    }

    @ApiStatus.Internal
    public static void writeByteArray(ResourceLocation id, @Nullable String subdir, Path parent, byte[] data) {
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
    public @Nullable IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
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
    public @Nullable <T> T getMetadataSection(MetadataSectionSerializer<T> metaReader) {
        if (metaReader == PackMetadataSection.TYPE) {
            return (T) new PackMetadataSection(Component.literal("Resource Farm dynamic assets"),
                    SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES));
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

    public static ResourceLocation getBlockStateLocation(ResourceLocation blockId) {
        return BLOCKSTATE_ID_CONVERTER.idToFile(blockId);
    }

    public static ResourceLocation getBlockModelLocation(ResourceLocation blockId) {
        return BLOCK_MODEL_ID_CONVERTER.idToFile(blockId);
    }

    public static ResourceLocation getItemModelLocation(ResourceLocation itemId) {
        return ITEM_MODEL_ID_CONVERTER.idToFile(itemId);
    }

    public static ResourceLocation getTextureLocation(@Nullable String path, ResourceLocation textureId) {
        if (path == null) {
            return TEXTURE_ID_CONVERTER.idToFile(textureId);
        }
        return TEXTURE_ID_CONVERTER.idToFile(textureId.withPrefix(path + "/"));
    }

    public static ResourceLocation getAtlasLocation(ResourceLocation atlasId) {
        return ATLAS_ID_CONVERTER.idToFile(atlasId);
    }
}
