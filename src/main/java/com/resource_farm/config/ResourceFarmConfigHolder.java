package com.resource_farm.config;

import com.resource_farm.ResourceFarm;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;
import org.jetbrains.annotations.ApiStatus;

@Config(id = ResourceFarm.MOD_ID, filename = "resource_farm/resource_farm")
public class ResourceFarmConfigHolder {

    public static ResourceFarmConfigHolder resourceFarmConfigHolder;
    public static PresetResourceTreeConfigHolder presetTreeConfigHolder;

    private static final Object LOCK = new Object();

    @ApiStatus.Internal
    public static ConfigHolder<ResourceFarmConfigHolder> INTERNAL_INSTANCE;

    public static void init() {
        synchronized (LOCK) {
            if (resourceFarmConfigHolder == null || INTERNAL_INSTANCE == null) {
                // 基础配置
                INTERNAL_INSTANCE = Configuration.registerConfig(ResourceFarmConfigHolder.class, ConfigFormats.YAML);
                resourceFarmConfigHolder = INTERNAL_INSTANCE.getConfigInstance();

                // 预设树木组配置
                PresetResourceTreeConfigHolder.INSTANCE = Configuration.registerConfig(PresetResourceTreeConfigHolder.class, ConfigFormats.YAML);
                presetTreeConfigHolder = PresetResourceTreeConfigHolder.INSTANCE.getConfigInstance();

                // 资源树-配置文件注册
                TreeRegisterConfig.init();
                // 资源树-配置文件移除
                TreeRemoveConfig.init();
            }
        }
    }

    @Configurable
    public DeveloperConfigs dev = new DeveloperConfigs();

    @Configurable
    public TreeConfigs tree = new TreeConfigs();

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
    }

    /** 树木总配置类 */
    public static class TreeConfigs {

        // 方块生成配置
        @Configurable
        @Configurable.Comment({
                "树木方块生成相关配置 Configuration for tree block generation"
        })
        public BlockGenerationConfigs blockGeneration = new BlockGenerationConfigs();

        // 配方生成配置
        @Configurable
        @Configurable.Comment({
                "树木配方生成相关配置 Configuration for tree recipe generation"
        })
        public RecipeGenerationConfigs recipeGeneration = new RecipeGenerationConfigs();
    }

    /** 方块生成配置类 */
    public static class BlockGenerationConfigs {

        @Configurable
        @Configurable.Comment({
                "是否生成去皮原木方块？ Whether to generate Stripped Log blocks? Default: true"
        })
        public boolean generateStrippedLog = true;

        @Configurable
        @Configurable.Comment({
                "是否生成木材（竖原木）方块？ Whether to generate Wood blocks? Default: true"
        })
        public boolean generateWood = true;

        @Configurable
        @Configurable.Comment({
                "是否生成去皮木材方块？ Whether to generate Stripped Wood blocks? Default: true"
        })
        public boolean generateStrippedWood = true;

        @Configurable
        @Configurable.Comment({
                "是否生成木板方块？ Whether to generate Planks blocks? Default: true"
        })
        public boolean generatePlanks = true;
    }

    /** 配方生成配置类 */
    public static class RecipeGenerationConfigs {

        @Configurable
        @Configurable.Comment({
                "是否生成树木物品合成配方？ Whether to generate TreeItem craft recipes? Default: true"
        })
        public boolean generateTreeItemRecipes = true;

        @Configurable
        @Configurable.Comment({
                "是否生成树苗合成配方？ Whether to generate Sapling craft recipes? Default: true"
        })
        public boolean generateSaplingRecipes = true;

        @Configurable
        @Configurable.Comment({
                "是否生成树脂熔炼/烟熏获取配方？ Whether to generate Resin smelting/smoking recipes? Default: true"
        })
        public boolean generateResinRecipes = true;

        @Configurable
        @Configurable.Comment({
                "是否生成基础木材转换配方（原木/木材/木板转换）？ Whether to generate basic wood conversion recipes (log/wood/planks)? Default: true"
        })
        public boolean generateBasicWoodConversionRecipes = true;
    }
}
