package com.resource_farm.config;

import com.resource_farm.ResourceFarm;
import com.resource_farm.api.ResourceOre.ResourceOreType;
import com.resource_farm.api.ResourceOre.ResourceOreTypes;
import com.resource_farm.api.ResourceTree.ResourceTreeType;
import com.resource_farm.api.ResourceTree.ResourceTreeTypes;
import com.resource_farm.api.block.FertilizeSettings;
import com.resource_farm.data.tree.ResourceTreeConfig;
import com.resource_farm.data.tree.builder.TreeRegister;
import com.resource_farm.utils.FormattingUtil;
import com.resource_farm.utils.JsonConfigUtil;
import com.resource_farm.utils.RLUtils;
import com.resource_farm.utils.RegistriesUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class TreeRegisterConfig {

    private static final String fileName = "resource_tree_register_configs.json";

    private static List<JsonObject> treeRegisterConfig;

    public static void init() {
        JsonArray jsonArray = JsonConfigUtil.loadJsonArray(fileName);
        treeRegisterConfig = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            if (element.isJsonObject()) treeRegisterConfig.add(element.getAsJsonObject());
            else ResourceFarm.LOGGER.warn("There are non JsonObject elements in the resource tree configuration, skip them");
        }

        ResourceFarm.LOGGER.info(
                "⌈Resource Tree Config Initialization⌋ Registration of resource trees via JSON configuration completed. " +
                        "Successfully loaded ⌈{}⌋ registered resource tree configuration entries.",
                treeRegisterConfig.size());
    }

    // 解析JSON并注册资源树
    public static void parseJSONtoRegisterTree() {
        int configCount = 0;
        int successCount = 0;

        for (JsonObject json : treeRegisterConfig) {
            configCount++;
            try {
                String item = GsonHelper.getAsString(json, "item", null);
                String translateKey = GsonHelper.getAsString(json, "translateKey", null);
                if (item == null && translateKey == null) {
                    ResourceFarm.LOGGER.warn("Skip {}th resource tree config: item and translateKey are both null.", configCount);
                    continue;
                }

                // 解析树/矿石样式
                String treeStyle = GsonHelper.getAsString(json, "treeStyle", "oak").toLowerCase();
                ResourceTreeType treeType = ResourceTreeTypes.TREE_TYPES.get(treeStyle);
                if (treeType == null) {
                    ResourceFarm.LOGGER.warn("{}th config: invalid treeStyle '{}', use default OAK.", configCount, treeStyle);
                    treeType = ResourceTreeTypes.OAK;
                }

                String oreStyle = GsonHelper.getAsString(json, "oreStyle", "iron").toLowerCase();
                ResourceOreType oreType = ResourceOreTypes.ORE_TYPES.get(oreStyle);
                if (oreType == null) {
                    ResourceFarm.LOGGER.warn("{}th config: invalid oreStyle '{}', use default IRON.", configCount, oreStyle);
                    oreType = ResourceOreTypes.IRON;
                }

                // 解析催熟配置
                FertilizeSettings fertilizeSetting = FertilizeSettings.fromJson(GsonHelper.getAsJsonObject(json, "fertilizeSetting", null));
                int growthFrequency = GsonHelper.getAsInt(json, "growthFrequency", 10);

                // 解析自定义放置方块/放置方块Tag
                String placeBlockStr = GsonHelper.getAsString(json, "customPlaceBlock", null);
                Block customPlaceBlock = placeBlockStr != null ? RegistriesUtils.getBlock(placeBlockStr) : null;
                String placeBlockTagStr = GsonHelper.getAsString(json, "customPlaceBlockTag", null);
                TagKey<Block> customPlaceBlockTag = placeBlockTagStr != null ? TagKey.create(Registries.BLOCK, RLUtils.parse(placeBlockTagStr)) : null;

                // 解析亮度和颜色
                int lightLevel = GsonHelper.getAsInt(json, "lightLevel", 0);
                int colors = FormattingUtil.parseColorString(GsonHelper.getAsString(json, "colors", "0"));

                // 构建配置并注册资源树
                TreeRegister.createResourceTree(ResourceTreeConfig.create(
                        item, translateKey,
                        treeType, oreType,
                        fertilizeSetting, growthFrequency,
                        customPlaceBlock, customPlaceBlockTag,
                        lightLevel, colors));
                successCount++;

            } catch (Exception e) {
                ResourceFarm.LOGGER.error("Failed to parse {}th resource tree register config: {}", configCount, e.getMessage(), e);
            }
        }

        // 解析完成日志：统计成功/总数量
        ResourceFarm.LOGGER.info("⌈Resource Tree Registration⌋ Parsed {} configs, successfully registered {} resource trees.", configCount, successCount);
        treeRegisterConfig.clear();
        treeRegisterConfig = null;
    }
}
