package com.resource_farm.config;

import com.resource_farm.ResourceFarm;
import com.resource_farm.data.tree.RegisterResourceTrees;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class TreeRemoveConfig {

    private static final String fileName = "resource_tree_remove_configs.json";

    private static JsonArray removeTreesConfig;

    public static void init() {
        removeTreesConfig = JsonConfigUtil.loadJsonArray(fileName);

        ResourceFarm.LOGGER.info(
                "⌈Resource Tree Config Initialization⌋ Removal of resource trees via JSON configuration completed. " +
                        "Will attempt to remove ⌈{}⌋ registered resource tree configuration entries.",
                removeTreesConfig.size());
    }

    public static void parseJSONtoRemoveTree() {
        int configCount = 0;
        int successCount = 0;

        for (JsonElement json : removeTreesConfig) {
            configCount++;
            try {
                String removeTree = json.getAsString();
                RegisterResourceTrees.removeResourceTree(removeTree);
                successCount++;
            } catch (Exception e) {
                ResourceFarm.LOGGER.error("Failed to parse {}th resource tree remove config: {}", configCount, e.getMessage(), e);
            }
        }

        ResourceFarm.LOGGER.info("⌈Resource Tree Removal⌋ Detected {} removal entries, removal completed.",
                successCount);
    }
}
