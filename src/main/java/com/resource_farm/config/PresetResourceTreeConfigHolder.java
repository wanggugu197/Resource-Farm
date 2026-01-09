package com.resource_farm.config;

import com.resource_farm.ResourceFarm;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.Configurable;
import org.jetbrains.annotations.ApiStatus;

@Config(id = ResourceFarm.MOD_ID, filename = "resource_farm/resource_farm_preset_tree")
public class PresetResourceTreeConfigHolder {

    @ApiStatus.Internal
    public static ConfigHolder<PresetResourceTreeConfigHolder> INSTANCE;

    @Configurable
    @Configurable.Comment({
            "是否启用预设树木组？ Whether to enable preset tree groups? Default: true"
    })
    public boolean enablePresetTreeGroups = true;

    @Configurable
    @Configurable.Comment({
            "预设树木组配置 Preset tree group configuration"
    })
    public PresetTreeGenerationConfigs presetTreeGeneration = new PresetTreeGenerationConfigs();

    /** 预设树木组配置类 */
    public static class PresetTreeGenerationConfigs {

        @Configurable
        @Configurable.Comment({
                "MC原版 基础？ MC original version Base? Default: true"
        })
        public boolean minecraftBase = true;

        @Configurable
        @Configurable.Comment({
                "MC原版 矿产？ MC original version Mineral? Default: true"
        })
        public boolean minecraftMineral = true;

        @Configurable
        @Configurable.Comment({
                "MC原版 生物？ MC original version Biology? Default: true"
        })
        public boolean minecraftBiology = true;

        @Configurable
        @Configurable.Comment({
                "MC原版 农业？ MC original version Agriculture? Default: true"
        })
        public boolean minecraftAgriculture = true;
    }
}
