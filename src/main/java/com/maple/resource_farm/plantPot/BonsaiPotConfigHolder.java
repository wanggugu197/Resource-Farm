package com.maple.resource_farm.plantPot;

import com.maple.resource_farm.ResourceFarm;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.Configurable;
import org.jetbrains.annotations.ApiStatus;

@Config(id = ResourceFarm.MOD_ID, filename = "resource_farm/resource_farm_bonsai_pot")
public class BonsaiPotConfigHolder {

    @ApiStatus.Internal
    public static ConfigHolder<BonsaiPotConfigHolder> INSTANCE;

    @Configurable
    public PlantPotConfig plantPot = new PlantPotConfig();

    public static class PlantPotConfig {

        @Configurable
        @Configurable.Comment({
                "露天白天的生长速度倍率（默认 2.0）",
                "Growth speed multiplier for outdoor daylight (default: 2.0)"
        })
        public float skyDayModifier = 2.0f;

        @Configurable
        @Configurable.Comment({
                "使用肥料的基础时间增益（默认 400）",
                "Base duration bonus from using fertilizer (default: 400)"
        })
        public int baseFertilizerBoost = 400;

        @Configurable
        @Configurable.Comment({
                "玻璃罩（等级1）速度倍率",
                "Glass Cloche (Tier 1) speed multiplier"
        })
        public float glassClocheSpeed = 1.5f;

        @Configurable
        @Configurable.Comment({
                "玻璃罩（等级1）产量倍率",
                "Glass Cloche (Tier 1) yield multiplier"
        })
        public float glassClocheYield = 1.0f;

        @Configurable
        @Configurable.Comment({
                "高级玻璃罩（等级2）速度倍率",
                "Advanced Cloche (Tier 2) speed multiplier"
        })
        public float advancedClocheSpeed = 2.5f;

        @Configurable
        @Configurable.Comment({
                "高级玻璃罩（等级2）产量倍率",
                "Advanced Cloche (Tier 2) yield multiplier"
        })
        public float advancedClocheYield = 2.0f;

        @Configurable
        @Configurable.Comment({
                "精英玻璃罩（等级3）速度倍率",
                "Elite Cloche (Tier 3) speed multiplier"
        })
        public float eliteClocheSpeed = 4.0f;

        @Configurable
        @Configurable.Comment({
                "精英玻璃罩（等级3）产量倍率",
                "Elite Cloche (Tier 3) yield multiplier"
        })
        public float eliteClocheYield = 4.0f;

        @Configurable
        @Configurable.Comment({
                "终极玻璃罩（等级4）速度倍率",
                "Ultimate Cloche (Tier 4) speed multiplier"
        })
        public float ultimateClocheSpeed = 6.0f;

        @Configurable
        @Configurable.Comment({
                "终极玻璃罩（等级4）产量倍率",
                "Ultimate Cloche (Tier 4) yield multiplier"
        })
        public float ultimateClocheYield = 8.0f;
    }
}
