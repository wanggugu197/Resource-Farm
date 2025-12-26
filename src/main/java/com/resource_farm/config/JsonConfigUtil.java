package com.resource_farm.config;

import com.resource_farm.ResourceFarm;

import net.neoforged.fml.loading.FMLPaths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    // ========== 读取原始 JsonObject 列表 ==========
    public static List<JsonObject> loadResourceTreeRawJsons() {
        String fileName = "resource_tree_configs.json";
        Path configPath = getConfigPath(fileName);
        File configFile = configPath.toFile();
        List<JsonObject> jsonList = new ArrayList<>();

        // 无文件则创建空数组的 JSON 文件
        if (!configFile.exists()) {
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(new JsonArray(), writer);
                return jsonList;
            } catch (IOException e) {
                ResourceFarm.LOGGER.error("创建资源树配置文件失败: {}", configPath, e);
                return jsonList;
            }
        }

        // 读取文件并解析为原始 JsonObject 列表
        try (FileReader reader = new FileReader(configFile)) {
            JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);
            for (int i = 0; i < jsonArray.size(); i++) {
                if (jsonArray.get(i).isJsonObject()) {
                    jsonList.add(jsonArray.get(i).getAsJsonObject());
                }
            }
            return jsonList;
        } catch (Exception e) {
            ResourceFarm.LOGGER.error("读取资源树原始 JSON 失败，返回空列表", e);
            return jsonList;
        }
    }
}
