package com.resource_farm.data.tree.RegisterAndSpecialRecipe;

import com.resource_farm.ResourceFarm;
import com.resource_farm.api.block.FertilizeSettings;
import com.resource_farm.config.ResourceFarmConfigHolder;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.data.misc.ResourceFarmBlockTags;
import com.resource_farm.data.tree.ResourceTree;
import com.resource_farm.data.tree.builder.TreeRegister;
import com.resource_farm.utils.RLUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import mekanism.api.chemical.Chemical;
import mekanism.api.datagen.recipe.builder.*;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.registration.impl.DeferredChemical;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.registries.MekanismItems;

import java.util.ArrayList;
import java.util.List;

import static com.resource_farm.api.ResourceOre.ResourceOreTypes.*;
import static com.resource_farm.api.ResourceTree.ResourceTreeTypes.*;

public class CrossoverMekanismRegister {

    private static final boolean GENERATE_STRIPPED_LOG = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog;
    private static final boolean GENERATE_WOOD = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood;
    private static final boolean GENERATE_STRIPPED_WOOD = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood;
    private static final boolean GENERATE_PLANKS = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks;
    private static final boolean GENERATE_CLUMPS = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateClump();

    private static final boolean GENERATE_TREE_ITEM_RECIPES = ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateTreeItemRecipes;
    private static final boolean GENERATE_SAPLING_RECIPES = ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateSaplingRecipes;

    public static void init(RecipeOutput consumer) {
        ResourceFarmBlocks.ResourceTreeMap.forEach((name, resourceTree) -> {

            List<ItemLike> logs = new ArrayList<>();
            logs.add(resourceTree.getLog());
            if (GENERATE_STRIPPED_LOG) logs.add(resourceTree.getStrippedLog());
            if (GENERATE_WOOD) logs.add(resourceTree.getWood());
            if (GENERATE_STRIPPED_WOOD) logs.add(resourceTree.getStrippedWood());

            if (GENERATE_PLANKS && ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateMekSawingGetPlanksRecipes) {
                SawmillRecipeBuilder.sawing(
                        IngredientCreatorAccess.item().from(Ingredient.of(logs.toArray(new ItemLike[0]))),
                        resourceTree.getPlanks().asStack(6),
                        MekanismItems.SAWDUST.asStack(), 0.25)
                        .build(consumer, ResourceFarm.id(name + "_sawing_logs"));
            }

            if (GENERATE_CLUMPS && ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateMekClumpGetResinRecipes) {
                ItemStackChemicalToItemStackRecipeBuilder.injecting(
                        IngredientCreatorAccess.item().from(Ingredient.of(logs.toArray(new ItemLike[0])), 2),
                        IngredientCreatorAccess.chemicalStack().from(MekanismChemicals.HYDROGEN.asStack(1)),
                        resourceTree.getClump().asStack(15),
                        true)
                        .build(consumer, ResourceFarm.id(name + "_injecting_logs"));

                ItemStackChemicalToItemStackRecipeBuilder.purifying(
                        IngredientCreatorAccess.item().from(resourceTree.getClump()),
                        IngredientCreatorAccess.chemicalStack().from(MekanismChemicals.OXYGEN.asStack(1)),
                        resourceTree.getResin().asStack(),
                        true)
                        .build(consumer, ResourceFarm.id(name + "_purifying_clump"));
            }

            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateMekEnrichingGetFruitRecipes) {
                ItemStackToItemStackRecipeBuilder.enriching(
                        IngredientCreatorAccess.item().from(resourceTree.getLeaves(), resourceTree.getSapling()),
                        resourceTree.getFruit().asStack(2))
                        .build(consumer, ResourceFarm.id(name + "_enriching_leaves_or_sapling"));
            }
        });
    }

    public static void create() {
        TreeRegister.createResourceTree("mekanism:raw_osmium", null, false, 32,
                DARK_OAK, IRON, FertilizeSettings.DEFAULT, 15,
                null, ResourceFarmBlockTags.createBlockTag(RLUtils.parse("c:ores/osmium")), 0, 0xc0dcdd);
        TreeRegister.createResourceTree("mekanism:raw_tin", null, false, 32,
                JUNGLE, COPPER, FertilizeSettings.DEFAULT, 15,
                null, ResourceFarmBlockTags.createBlockTag(RLUtils.parse("c:ores/tin")), 0, 0xf0e9d9);
        TreeRegister.createResourceTree("mekanism:raw_lead", null, false, 32,
                JUNGLE, DIAMOND, FertilizeSettings.DEFAULT, 15,
                null, ResourceFarmBlockTags.createBlockTag(RLUtils.parse("c:ores/lead")), 0, 0x5d6376);
        TreeRegister.createResourceTree("mekanism:raw_uranium", null, false, 32,
                DARK_OAK, NETHER_GOLD, FertilizeSettings.DEFAULT, 15,
                null, ResourceFarmBlockTags.createBlockTag(RLUtils.parse("c:ores/uranium")), 0, 0x96e66b);
        TreeRegister.createResourceTree("mekanism:fluorite_gem", null, false, 32,
                SPRUCE, LAPIS, FertilizeSettings.DEFAULT, 15,
                null, ResourceFarmBlockTags.createBlockTag(RLUtils.parse("c:ores/fluorite")), 0, 0xe5f8f5);

        TreeRegister.createResourceTree("mekanism:ingot_steel", null, false, 16,
                CHERRY, IRON, FertilizeSettings.of("mekanism:nugget_steel", 0.9), 50,
                "mekanism:block_steel", null, 0, 0x999996);
        TreeRegister.createResourceTree("mekanism:ingot_bronze", null, false, 16,
                JUNGLE, IRON, FertilizeSettings.of("mekanism:nugget_bronze", 0.9), 50,
                "mekanism:block_bronze", null, 0, 0xfdd688);
        TreeRegister.createResourceTree("mekanism:ingot_refined_glowstone", null, false, 8,
                OAK, GOLD, FertilizeSettings.of("mekanism:nugget_refined_glowstone", 0.9), 100,
                "mekanism:block_refined_glowstone", null, 0, 0xfff0a5);
        TreeRegister.createResourceTree("mekanism:ingot_refined_obsidian", null, false, 8,
                OAK, REDSTONE, FertilizeSettings.of("mekanism:nugget_refined_obsidian", 0.9), 100,
                "mekanism:block_refined_obsidian", null, 0, 0x8f76c8);

        TreeRegister.createResourceTree("mekanism:basic_control_circuit", null, false, 32,
                CHERRY, NETHER_QUARTZ, FertilizeSettings.of("mekanism:ingot_osmium", 0.9), 50,
                "mekanism:steel_casing", null, 1, 0x18f8a2);
        TreeRegister.createResourceTree("mekanism:alloy_infused", null, false, 32,
                BIRCH, EMERALD, FertilizeSettings.of("minecraft:copper_ingot", 0.9), 50,
                "mekanism:steel_casing", null, 1, 0xf5564e);
        TreeRegister.createResourceTree("mekanism:alloy_reinforced", null, false, 16,
                ACACIA, EMERALD, FertilizeSettings.of("mekanism:alloy_infused", 0.9), 50,
                "mekanism:steel_casing", null, 2, 0xa3f8ff);
        TreeRegister.createResourceTree("mekanism:alloy_atomic", null, false, 8,
                MANGROVE, EMERALD, FertilizeSettings.of("mekanism:alloy_reinforced", 0.9), 50,
                "mekanism:steel_casing", null, 2, 0xd890f7);

        TreeRegister.createResourceTree("mekanism:hdpe_pellet", null, false, 8,
                ACACIA, NETHER_GOLD, FertilizeSettings.of("mekanism:substrate", 0.6), 50,
                "mekanism:block_bio_fuel", null, 0, 0xebedee);
        TreeRegister.createResourceTree("mekanism:salt", null, false, 32,
                SPRUCE, LAPIS, FertilizeSettings.of("mekanism:salt", 0.9), 50,
                "mekanism:thermal_evaporation_block", null, 0, 0xf8f8f7);
        TreeRegister.createResourceTree("mekanism:dust_lithium", null, false, 32,
                SPRUCE, LAPIS, FertilizeSettings.of("mekanism:dust_lithium", 0.9), 50,
                "mekanism:thermal_evaporation_block", null, 0, 0xb9a79c);

        TreeRegister.createResourceTree("mekanism:pellet_plutonium", null, false, 1,
                BIRCH, DIAMOND, FertilizeSettings.of("mekanism:pellet_plutonium", 1), 500,
                "mekanism:sps_casing", null, 5, 0xacf6f8);
        TreeRegister.createResourceTree("mekanism:pellet_polonium", null, false, 1,
                OAK, DIAMOND, FertilizeSettings.of("mekanism:pellet_polonium", 1), 500,
                "mekanism:sps_casing", null, 5, 0xbdf7d3);
        TreeRegister.createResourceTree("mekanism:pellet_antimatter", null, false, 1,
                ACACIA, EMERALD, FertilizeSettings.of("mekanism:pellet_antimatter", 1), 500,
                "mekanism:sps_casing", null, 6, 0xc270d8);

        TreeRegister.createResourceTree(null, "chemical.mekanism.hydrogen", false, 250,
                CHERRY, CRACK, FertilizeSettings.of("mekanism:pellet_antimatter", 1), 20,
                "mekanism:steel_casing", null, 0, 0xFFFFFFFF);
        TreeRegister.createResourceTree(null, "chemical.mekanism.oxygen", false, 250,
                DARK_OAK, CRACK, FertilizeSettings.of("mekanism:pellet_antimatter", 1), 20,
                "mekanism:steel_casing", null, 0, 0xFF6CE2FF);
        TreeRegister.createResourceTree(null, "chemical.mekanism.hydrogen_chloride", false, 200,
                ACACIA, CRACK, FertilizeSettings.of("mekanism:pellet_antimatter", 1), 20,
                "mekanism:steel_casing", null, 0, 0xFFA8F1E9);
        TreeRegister.createResourceTree(null, "chemical.mekanism.ethene", false, 200,
                BIRCH, CRACK, FertilizeSettings.of("mekanism:pellet_antimatter", 1), 20,
                "mekanism:steel_casing", null, 0, 0xFFEACCF9);
        TreeRegister.createResourceTree(null, "chemical.mekanism.sodium", false, 100,
                OAK, CRACK, FertilizeSettings.of("mekanism:pellet_antimatter", 1), 20,
                "mekanism:steel_casing", null, 0, 0xFFE9FEF4);
        TreeRegister.createResourceTree(null, "chemical.mekanism.sulfuric_acid", false, 100,
                BIRCH, CRACK, FertilizeSettings.of("mekanism:pellet_antimatter", 1), 20,
                "mekanism:steel_casing", null, 0, 0xFF82802B);
        TreeRegister.createResourceTree(null, "chemical.mekanism.uranium_hexafluoride", false, 10,
                OAK, CRACK, FertilizeSettings.of("mekanism:pellet_antimatter", 1), 20,
                "mekanism:steel_casing", null, 8, 0xFF809960);

        if (ResourceFarm.isModLoaded("mekanismgenerators")) {
            TreeRegister.createResourceTree(null, "chemical.mekanismgenerators.fusion_fuel", false, 10,
                    ACACIA, CRACK, FertilizeSettings.of("mekanism:pellet_antimatter", 1), 20,
                    "mekanism:steel_casing", null, 10, 0x7E007D);
        }
    }

    public static void createSpecialRecipe(RecipeOutput consumer) {
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_raw_osmium", 16);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_raw_tin", 16);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_raw_lead", 16);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_raw_uranium", 16);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_fluorite_gem", 16);

        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_ingot_steel", 8);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_ingot_bronze", 8);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_ingot_refined_glowstone", 8);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_ingot_refined_obsidian", 8);

        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_basic_control_circuit", 16);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_alloy_infused", 16);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_alloy_reinforced", 32);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_alloy_atomic", 64);

        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_pellet_plutonium", 8);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_pellet_polonium", 8);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_pellet_antimatter", 8);

        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_dust_lithium", 32);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_dust_lithium", 32);
        createTreeItemAndSaplingRecipeBuild(consumer, "mekanism_dust_lithium", 32);

        createTreeChemicalAndSaplingRecipeBuild(consumer, "hydrogen", MekanismChemicals.HYDROGEN);
        createTreeChemicalAndSaplingRecipeBuild(consumer, "oxygen", MekanismChemicals.OXYGEN);
        createTreeChemicalAndSaplingRecipeBuild(consumer, "hydrogen_chloride", MekanismChemicals.HYDROGEN_CHLORIDE);
        createTreeChemicalAndSaplingRecipeBuild(consumer, "ethene", MekanismChemicals.ETHENE);
        createTreeChemicalAndSaplingRecipeBuild(consumer, "sodium", MekanismChemicals.SODIUM);
        createTreeChemicalAndSaplingRecipeBuild(consumer, "sulfuric_acid", MekanismChemicals.SULFURIC_ACID);
        createTreeChemicalAndSaplingRecipeBuild(consumer, "uranium_hexafluoride", MekanismChemicals.URANIUM_HEXAFLUORIDE);

        if (ResourceFarm.isModLoaded("mekanismgenerators")) {
            createTreeChemicalAndSaplingRecipeBuild(consumer, "fusion_fuel", mekanism.generators.common.registries.GeneratorsChemicals.FUSION_FUEL);
        }
    }

    private static void createTreeItemAndSaplingRecipeBuild(RecipeOutput consumer, String id, int count) {
        String treeId = id + "_tree";
        ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;
        Item treeItem = resourceTree.getTreeItem().get();
        if (GENERATE_SAPLING_RECIPES) {
            CombinerRecipeBuilder.combining(
                    IngredientCreatorAccess.item().from(treeItem, count),
                    IngredientCreatorAccess.item().from(ItemTags.SAPLINGS),
                    resourceTree.getSapling().asStack())
                    .build(consumer, ResourceFarm.id(treeId + "_craft_sapling"));
        }
        if (GENERATE_TREE_ITEM_RECIPES) {
            CombinerRecipeBuilder.combining(
                    IngredientCreatorAccess.item().from(resourceTree.getResin(), 4),
                    IngredientCreatorAccess.item().from(resourceTree.getFruit()),
                    new ItemStack(resourceTree.getTreeItem().get(), resourceTree.getResourceTreeConfig().productOutput()))
                    .build(consumer, ResourceFarm.id(treeId + "_craft_" + BuiltInRegistries.ITEM.getKey(treeItem).getPath()));
        }
    }

    private static void createTreeChemicalAndSaplingRecipeBuild(RecipeOutput consumer, String id, DeferredChemical<Chemical> chemical) {
        String treeId = id + "_tree";
        ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;
        if (GENERATE_SAPLING_RECIPES) {
            PressurizedReactionRecipeBuilder.reaction(
                    IngredientCreatorAccess.item().from(ItemTags.SAPLINGS),
                    IngredientCreatorAccess.fluid().from(MekanismFluids.NUTRITIONAL_PASTE.asStack(1000)),
                    IngredientCreatorAccess.chemicalStack().from(chemical.asStack(1000)),
                    20,
                    resourceTree.getSapling().asStack())
                    .build(consumer, ResourceFarm.id(treeId + "_craft_sapling"));
        }
        if (GENERATE_TREE_ITEM_RECIPES) {
            ItemStackToChemicalRecipeBuilder.oxidizing(
                    IngredientCreatorAccess.item().from(resourceTree.getResin(), resourceTree.getFruit()),
                    chemical.asStack(resourceTree.getResourceTreeConfig().productOutput()))
                    .build(consumer, ResourceFarm.id(treeId + "_craft_" + chemical.getId().getPath()));
        }
    }
}
