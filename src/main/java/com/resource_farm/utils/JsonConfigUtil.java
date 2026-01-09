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
            ResourceFarm.LOGGER.error("创建配置文件夹失败: {}", configDir, e);
        }
        return configDir.resolve(fileName);
    }

    // ====================== 新增：通用基础读取方法 ======================
    /**
     * 通用读取方法：读取指定配置文件并解析为JsonElement
     */
    private static JsonElement loadJsonElement(String fileName, JsonElement defaultElement) {
        Path configPath = getConfigPath(fileName);
        File configFile = configPath.toFile();

        if (!configFile.exists()) {
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(defaultElement, writer);
                ResourceFarm.LOGGER.info("配置文件不存在，已创建默认文件: {}", configPath);
                return defaultElement;
            } catch (IOException e) {
                ResourceFarm.LOGGER.error("创建默认配置文件失败: {}", configPath, e);
                return defaultElement;
            }
        }

        try (FileReader reader = new FileReader(configFile)) {
            return GSON.fromJson(reader, JsonElement.class);
        } catch (IOException e) {
            ResourceFarm.LOGGER.error("读取配置文件IO失败: {}", configPath, e);
            return defaultElement;
        } catch (JsonParseException e) {
            ResourceFarm.LOGGER.error("配置文件JSON格式错误，无法解析: {}", configPath, e);
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
            ResourceFarm.LOGGER.warn("配置文件 {} 不是合法的JsonObject，返回空对象", fileName);
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
            ResourceFarm.LOGGER.warn("配置文件 {} 不是合法的JsonArray，返回空数组", fileName);
            return new JsonArray();
        }
    }
}
