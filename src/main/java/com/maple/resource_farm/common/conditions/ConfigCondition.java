package com.maple.resource_farm.common.conditions;

import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.resourceTree.PresetResourceTreeConfigHolder;

import net.neoforged.neoforge.common.conditions.ICondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record ConfigCondition(String configId) implements ICondition {

    public static final MapCodec<ConfigCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("config_id").forGetter(ConfigCondition::configId)).apply(inst, ConfigCondition::new));

    @Override
    public boolean test(ICondition.@NonNull IContext context) {
        PresetResourceTreeConfigHolder cfg = PresetResourceTreeConfigHolder.INSTANCE.getConfigInstance();
        PresetResourceTreeConfigHolder.TreeConfigs tree = cfg.tree;
        PresetResourceTreeConfigHolder.BlockGenerationConfigs blockGen = tree.blockGeneration;
        PresetResourceTreeConfigHolder.RecipeGenerationConfigs recipeGen = tree.recipeGeneration;
        PresetResourceTreeConfigHolder.PresetTreeGenerationConfigs presetGen = cfg.presetTreeGeneration;

        return switch (configId) {

            case "dev.enableResourceTree" -> ResourceFarmConfigHolder.farmConfigHolder.dev.enableResourceTree;
            case "dev.enableBonsaiPot" -> ResourceFarmConfigHolder.farmConfigHolder.dev.enableBonsaiPot;
            case "dev.dumpData" -> ResourceFarmConfigHolder.farmConfigHolder.dev.dumpData;
            case "dev.dumpAssets" -> ResourceFarmConfigHolder.farmConfigHolder.dev.dumpAssets;

            case "tree.blockGeneration.generateStrippedLog" -> blockGen.generateStrippedLog;
            case "tree.blockGeneration.generateWood" -> blockGen.generateWood;
            case "tree.blockGeneration.generateStrippedWood" -> blockGen.generateStrippedWood;
            case "tree.blockGeneration.generatePlanks" -> blockGen.generatePlanks;
            case "tree.blockGeneration.autoGenerateClump" -> blockGen.autoGenerateClump;
            case "tree.blockGeneration.forceGenerateClump" -> blockGen.forceGenerateClump;

            case "tree.recipeGeneration.generateTreeItemRecipes" -> recipeGen.generateTreeItemRecipes;
            case "tree.recipeGeneration.generateSaplingRecipes" -> recipeGen.generateSaplingRecipes;
            case "tree.recipeGeneration.generateResinRecipes" -> recipeGen.generateResinRecipes;
            case "tree.recipeGeneration.generateBasicWoodConversionRecipes" -> recipeGen.generateBasicWoodConversionRecipes;
            case "tree.recipeGeneration.generateBonsaiPotRecipes" -> recipeGen.generateBonsaiPotRecipes;

            case "tree.enablePresetTreeGroups" -> cfg.enablePresetTreeGroups;

            case "tree.presetTreeGeneration.minecraftBase" -> presetGen.minecraftBase;
            case "tree.presetTreeGeneration.minecraftMineral" -> presetGen.minecraftMineral;
            case "tree.presetTreeGeneration.minecraftBiology" -> presetGen.minecraftBiology;
            case "tree.presetTreeGeneration.minecraftAgriculture" -> presetGen.minecraftAgriculture;
            case "tree.presetTreeGeneration.mysticalAgriculture" -> presetGen.mysticalAgriculture();
            case "tree.presetTreeGeneration.ae2" -> presetGen.ae2();

            default -> false;
        };
    }

    @Override
    public @NonNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
