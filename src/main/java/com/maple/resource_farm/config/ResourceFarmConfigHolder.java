package com.maple.resource_farm.config;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.ResourceTree.config.PresetResourceTreeConfigHolder;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;
import org.jetbrains.annotations.ApiStatus;

@Config(id = ResourceFarm.MOD_ID, filename = "resource_farm/resource_farm")
public class ResourceFarmConfigHolder {

    public static ResourceFarmConfigHolder FarmConfigHolder;
    public static PresetResourceTreeConfigHolder TreeConfigHolder;

    private static final Object LOCK = new Object();

    @ApiStatus.Internal
    public static ConfigHolder<ResourceFarmConfigHolder> INTERNAL_INSTANCE;

    public static void init() {
        synchronized (LOCK) {
            if (FarmConfigHolder == null || INTERNAL_INSTANCE == null) {
                // 基础配置
                INTERNAL_INSTANCE = Configuration.registerConfig(ResourceFarmConfigHolder.class, ConfigFormats.YAML);
                FarmConfigHolder = INTERNAL_INSTANCE.getConfigInstance();

                // 预设树木组配置（控制数据包 group=base/mineral/... 是否加载）
                PresetResourceTreeConfigHolder.INSTANCE = Configuration.registerConfig(PresetResourceTreeConfigHolder.class, ConfigFormats.YAML);
                TreeConfigHolder = PresetResourceTreeConfigHolder.INSTANCE.getConfigInstance();
            }
        }
    }

    @Configurable
    public DeveloperConfigs dev = new DeveloperConfigs();

    public static class DeveloperConfigs {

        @Configurable
        @Configurable.Comment({
                "是否导出所有已注册的数据？ Dump all registered data? Default: false"
        })
        public boolean dumpData = false;

        @Configurable
        @Configurable.Comment({
                "是否导出所有已注册的资源？ Dump all registered assets? Default: false"
        })
        public boolean dumpAssets = false;

        @Configurable
        @Configurable.Comment({
                "是否启用资源树模块？ Whether to enable the resource tree module? Default: true"
        })
        public boolean enableResourceTree = true;
    }
}
