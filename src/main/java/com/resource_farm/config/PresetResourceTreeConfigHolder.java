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
    public TreeConfigs tree = new TreeConfigs();

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

        @Configurable
        @Configurable.Comment({
                "是否生成机械动力漂洗树叶增产配方？ Whether to generate Create Splashing Leaves increase production recipes? 87.5%↑ Default: true"
        })
        public boolean generateCreateSplashingLeavesRecipe = true;

        @Configurable
        @Configurable.Comment({
                "是否生成机械动力混合搅拌树脂增产配方？ Whether to generate Create Mixing Resin increase production recipes? 25%↑ Default: true"
        })
        public boolean generateCreateMixingGetResinRecipes = true;
    }

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

        @Configurable
        @Configurable.Comment({
                "联动 机械动力？ Crossover Create? Default: true"
        })
        public boolean crossoverCreate = true;
    }
}
