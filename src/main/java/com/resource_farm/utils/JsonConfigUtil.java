package com.resource_farm.utils;

import com.resource_farm.ResourceFarm;

import net.neoforged.fml.loading.FMLPaths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonConfigUtil {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    /**
     * 获取配置文件路径（统一放在 config/resource_farm/ 下）
     */
    public static Path getConfigPath(String fileName) {
        Path configDir = FMLPaths.CONFIGDIR.get().resolve("resource_farm");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            ResourceFarm.LOGGER.error("Failed to create configuration folder: {}", configDir, e);
        }
        return configDir.resolve(fileName);
    }

    /**
     * 通用读取方法：读取指定配置文件并解析为JsonElement
     */
    private static JsonElement loadJsonElement(String fileName, JsonElement defaultElement) {
        Path configPath = getConfigPath(fileName);
        File configFile = configPath.toFile();

        if (!configFile.exists()) {
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(defaultElement, writer);
                ResourceFarm.LOGGER.info("The configuration file does not exist, a default file has been created: {}", configPath);
                return defaultElement;
            } catch (IOException e) {
                ResourceFarm.LOGGER.error("Failed to create default configuration file: {}", configPath, e);
                return defaultElement;
            }
        }

        try (FileReader reader = new FileReader(configFile)) {
            return GSON.fromJson(reader, JsonElement.class);
        } catch (IOException e) {
            ResourceFarm.LOGGER.error("Reading configuration file IO failed: {}", configPath, e);
            return defaultElement;
        } catch (JsonParseException e) {
            ResourceFarm.LOGGER.error("The JSON format of the configuration file is incorrect and cannot be parsed: {}", configPath, e);
            return defaultElement;
        }
    }

    /**
     * 读取指定配置文件并解析为JsonObject
     */
    public static JsonObject loadSingleJsonObject(String fileName) {
        JsonElement defaultElement = new JsonObject();
        JsonElement element = loadJsonElement(fileName, defaultElement);

        if (element.isJsonObject()) {
            return element.getAsJsonObject();
        } else {
            ResourceFarm.LOGGER.warn("The configuration file {} is not a valid JsonObject and returns an empty object", fileName);
            return new JsonObject();
        }
    }

    /**
     * 通用方法：读取指定配置文件并解析为JsonArray
     */
    public static JsonArray loadJsonArray(String fileName) {
        JsonElement defaultElement = new JsonArray();
        JsonElement element = loadJsonElement(fileName, defaultElement);

        if (element.isJsonArray()) {
            return element.getAsJsonArray();
        } else {
            ResourceFarm.LOGGER.warn("The configuration file {} is not a valid JsonArray and returns an empty array", fileName);
            return new JsonArray();
        }
    }
}
