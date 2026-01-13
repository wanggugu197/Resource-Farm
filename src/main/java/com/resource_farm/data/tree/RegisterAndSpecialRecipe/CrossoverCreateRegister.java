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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;

import java.util.ArrayList;
import java.util.List;

import static com.resource_farm.api.ResourceOre.ResourceOreTypes.*;
import static com.resource_farm.api.ResourceTree.ResourceTreeTypes.*;

public class CrossoverCreateRegister {

    private static final boolean GENERATE_STRIPPED_LOG = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog;
    private static final boolean GENERATE_WOOD = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood;
    private static final boolean GENERATE_STRIPPED_WOOD = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood;
    private static final boolean GENERATE_PLANKS = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks;

    private static final boolean GENERATE_TREE_ITEM_RECIPES = ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateTreeItemRecipes;
    private static final boolean GENERATE_SAPLING_RECIPES = ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateSaplingRecipes;

    public static void init(RecipeOutput consumer) {
        ResourceFarmBlocks.ResourceTreeMap.forEach((name, resourceTree) -> {
            // 漂洗配方 漂洗树叶 增产约 275%
            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateCreateSplashingLeavesRecipe) {
                BlockEntry<?> leavesEntry = resourceTree.getLeaves();
                new SplashingRecipe.Builder<>(SplashingRecipe::new, ResourceFarm.id("wash_" + leavesEntry.getId().getPath()))
                        .require(leavesEntry)
                        .output(0.125f, resourceTree.getResin(), 2)
                        .output(0.5f, resourceTree.getFruit(), 2)
                        .build(consumer);
            }

            // 混合搅拌配方 水萃原木获得树脂 增产 25%
            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateCreateMixingGetResinRecipes) {
                ItemEntry<?> resin = resourceTree.getResin();
                List<ItemLike> logs = new ArrayList<>();
                logs.add(resourceTree.getLog());
                if (GENERATE_STRIPPED_LOG) logs.add(resourceTree.getStrippedLog());
                if (GENERATE_WOOD) logs.add(resourceTree.getWood());
                if (GENERATE_STRIPPED_WOOD) logs.add(resourceTree.getStrippedWood());
                new MixingRecipe.Builder<>(MixingRecipe::new, ResourceFarm.id(name + "_water_extraction_logs"))
                        .require(Ingredient.of(logs.toArray(new ItemLike[0]))).require(Fluids.WATER, 1000)
                        .output(resin.asStack(4)).output(0.25f, resin.asStack(4))
                        .duration(200).requiresHeat(HeatCondition.HEATED).build(consumer);

                new MixingRecipe.Builder<>(MixingRecipe::new, ResourceFarm.id(name + "_water_extraction_planks"))
                        .require(resourceTree.getPlanks()).require(Fluids.WATER, 1000)
                        .output(resin.asStack()).output(0.25f, resin.asStack())
                        .duration(200).requiresHeat(HeatCondition.HEATED).build(consumer);
            }
        });
    }

    public static void create() {
        TreeRegister.createResourceTree("create:raw_zinc", null, false, 32,
                DARK_OAK, IRON, FertilizeSettings.DEFAULT, 50,
                null, ResourceFarmBlockTags.createBlockTag(RLUtils.parse("c:ores/zinc")), 0, 0xb7e6bf);
        TreeRegister.createResourceTree("create:andesite_alloy", null, false, 32,
                OAK, LAPIS, FertilizeSettings.of("create:zinc_nugget", 0.8, "minecraft:iron_nugget", 0.6), 50,
                "create:andesite_casing", null, 0, 0xa7ad9f);
        TreeRegister.createResourceTree("create:brass_ingot", null, false, 16,
                CHERRY, DIAMOND, FertilizeSettings.of("create:brass_nugget", 0.8), 50,
                "create:brass_casing", null, 0, 0xf8ca67);
        TreeRegister.createResourceTree("create:cardboard", null, false, 32,
                SPRUCE, IRON, FertilizeSettings.of("create:pulp", 0.8), 50,
                "create:cardboard_block", null, 0, 0xcfa87e);
        TreeRegister.createResourceTree("create:rose_quartz", null, false, 16,
                JUNGLE, GOLD, FertilizeSettings.of("minecraft:quartz", 1, "minecraft:redstone", 0.75), 50,
                "create:rose_quartz_lamp", null, 0, 0xfc8383);
        TreeRegister.createResourceTree("create:precision_mechanism", null, false, 8,
                ACACIA, NETHER_QUARTZ, FertilizeSettings.of("minecraft:gold_ingot", 0.6), 50,
                "create:rotation_speed_controller", null, 0, 0xd09f54);
    }

    public static void createSpecialRecipe(RecipeOutput consumer) {
        createTreeItemAndSaplingRecipeBuild(consumer, "create_raw_zinc");
        {
            if (GENERATE_SAPLING_RECIPES) {
                String treeId = "create_raw_zinc" + "_tree";
                ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
                if (resourceTree == null) return;
                Item item = AllItems.ZINC_INGOT.asItem();
                Item resin = resourceTree.getResin().get();
                new CompactingRecipe.Builder<>(CompactingRecipe::new, ResourceFarm.id(treeId + "_craft_" + BuiltInRegistries.ITEM.getKey(item).getPath()))
                        .require(resin).require(resin).require(resin).require(resin).require(resourceTree.getFruit())
                        .output(item, resourceTree.getResourceTreeConfig().productOutput()).requiresHeat(HeatCondition.HEATED)
                        .duration(200).build(consumer);
            }
        }
        createTreeItemAndSaplingRecipeBuild(consumer, "create_andesite_alloy");
        createTreeItemAndSaplingRecipeBuild(consumer, "create_brass_ingot");
        createTreeItemAndSaplingRecipeBuild(consumer, "create_cardboard");
        {
            if (GENERATE_SAPLING_RECIPES) {
                String treeId = "create_cardboard" + "_tree";
                ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
                if (resourceTree == null) return;
                Item item = AllBlocks.CARDBOARD_BLOCK.asItem();
                Item resin = resourceTree.getResin().get();
                new CompactingRecipe.Builder<>(CompactingRecipe::new, ResourceFarm.id(treeId + "_craft_" + BuiltInRegistries.ITEM.getKey(item).getPath()))
                        .require(resin).require(resin).require(resin).require(resin).require(resourceTree.getFruit())
                        .output(item, resourceTree.getResourceTreeConfig().productOutput() / 4)
                        .duration(200).build(consumer);
            }
        }
        {
            String treeId = "create_rose_quartz" + "_tree";
            ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
            if (resourceTree == null) return;
            Item treeItem = resourceTree.getTreeItem().get();
            Item item = AllItems.POLISHED_ROSE_QUARTZ.asItem();
            if (GENERATE_SAPLING_RECIPES) {
                new SequencedAssemblyRecipeBuilder(ResourceFarm.id(treeId + "_craft_sapling"))
                        .require(Items.JUNGLE_SAPLING).transitionTo(Items.JUNGLE_SAPLING)
                        .loops(3)
                        .addStep(DeployerApplicationRecipe::new, db -> db.require(item))
                        .addStep(PressingRecipe::new, pb -> pb)
                        .addStep(FillingRecipe::new, fb -> fb.require(Fluids.WATER, 1000))
                        .addStep(DeployerApplicationRecipe::new, db -> db.require(treeItem))
                        .addStep(PressingRecipe::new, pb -> pb)
                        .addStep(DeployerApplicationRecipe::new, db -> db.require(AllTags.AllItemTags.SANDPAPER.tag).toolNotConsumed())
                        .addOutput(resourceTree.getSapling(), 1)
                        .build(consumer);
            }
            if (GENERATE_TREE_ITEM_RECIPES) {
                Item resin = resourceTree.getResin().get();
                new CompactingRecipe.Builder<>(CompactingRecipe::new, ResourceFarm.id(treeId + "_craft_" + BuiltInRegistries.ITEM.getKey(treeItem).getPath()))
                        .require(resin).require(resin).require(resin).require(resin).require(resourceTree.getFruit())
                        .output(treeItem, resourceTree.getResourceTreeConfig().productOutput())
                        .duration(200).build(consumer);
                new CompactingRecipe.Builder<>(CompactingRecipe::new, ResourceFarm.id(treeId + "_craft_" + BuiltInRegistries.ITEM.getKey(item).getPath()))
                        .require(resin).require(resin).require(resin).require(resin).require(resourceTree.getFruit())
                        .require(AllTags.AllItemTags.SANDPAPER.tag)
                        .output(item, resourceTree.getResourceTreeConfig().productOutput())
                        .duration(200).build(consumer);
            }
        }
        {
            String treeId = "create_precision_mechanism" + "_tree";
            ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
            if (resourceTree == null) return;
            Item treeItem = resourceTree.getTreeItem().get();
            if (GENERATE_SAPLING_RECIPES) {
                MechanicalCraftingRecipeBuilder
                        .shapedRecipe(resourceTree.getSapling(), 1)
                        .key('A', treeItem)
                        .key('B', AllItems.BRASS_INGOT)
                        .key('C', AllItems.ANDESITE_ALLOY)
                        .key('D', AllBlocks.COGWHEEL)
                        .key('E', AllBlocks.LARGE_COGWHEEL)
                        .key('T', ItemTags.SAPLINGS)
                        .patternLine("    B    ")
                        .patternLine("   BEB   ")
                        .patternLine("  BDADB  ")
                        .patternLine(" BDACADB ")
                        .patternLine("BEACTCAEB")
                        .patternLine(" BDACADB ")
                        .patternLine("  BDADB  ")
                        .patternLine("   BEB   ")
                        .patternLine("    B    ")
                        .build(consumer, ResourceFarm.id(treeId + "_craft_sapling"));
            }
            if (GENERATE_TREE_ITEM_RECIPES) {
                MechanicalCraftingRecipeBuilder
                        .shapedRecipe(treeItem, resourceTree.getResourceTreeConfig().productOutput())
                        .key('A', resourceTree.getResin())
                        .key('B', resourceTree.getFruit())
                        .key('C', AllItems.BRASS_NUGGET)
                        .patternLine("CAC")
                        .patternLine("ABA")
                        .patternLine("CAC")
                        .build(consumer, ResourceFarm.id(treeId + "_craft_" + BuiltInRegistries.ITEM.getKey(treeItem).getPath()));
            }
        }
        // 和原版资源树的特殊联动配方
        if (GENERATE_TREE_ITEM_RECIPES) {
            {
                String treeId = "water" + "_tree";
                ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
                if (resourceTree == null) return;
                new EmptyingRecipe.Builder<>(EmptyingRecipe::new, ResourceFarm.id("empty_" + treeId + "resin"))
                        .require(resourceTree.getResin()).output(Fluids.WATER, 1000).build(consumer);
                new EmptyingRecipe.Builder<>(EmptyingRecipe::new, ResourceFarm.id("empty_" + treeId + "fruit"))
                        .require(resourceTree.getFruit()).output(Fluids.WATER, 1000).build(consumer);
            }
            {
                String treeId = "lava" + "_tree";
                ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
                if (resourceTree == null) return;
                new EmptyingRecipe.Builder<>(EmptyingRecipe::new, ResourceFarm.id("empty_" + treeId + "resin"))
                        .require(resourceTree.getResin()).output(Fluids.LAVA, 1000).build(consumer);
                new EmptyingRecipe.Builder<>(EmptyingRecipe::new, ResourceFarm.id("empty_" + treeId + "fruit"))
                        .require(resourceTree.getFruit()).output(Fluids.LAVA, 1000).build(consumer);
            }
            {
                String treeId = "honeycomb" + "_tree";
                ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
                if (resourceTree == null) return;
                new EmptyingRecipe.Builder<>(EmptyingRecipe::new, ResourceFarm.id("empty_" + treeId + "resin"))
                        .require(resourceTree.getResin()).output(AllFluids.HONEY.get(), 250).build(consumer);
                new EmptyingRecipe.Builder<>(EmptyingRecipe::new, ResourceFarm.id("empty_" + treeId + "fruit"))
                        .require(resourceTree.getFruit()).output(AllFluids.HONEY.get(), 250).build(consumer);
            }
        }
    }

    private static void createTreeItemAndSaplingRecipeBuild(RecipeOutput consumer, String id) {
        String treeId = id + "_tree";
        ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;
        Item treeItem = resourceTree.getTreeItem().get();
        if (GENERATE_SAPLING_RECIPES) {
            new CompactingRecipe.Builder<>(CompactingRecipe::new, ResourceFarm.id(treeId + "_craft_sapling"))
                    .require(ItemTags.SAPLINGS)
                    .require(treeItem).require(treeItem).require(treeItem).require(treeItem)
                    .require(treeItem).require(treeItem).require(treeItem).require(treeItem)
                    .output(resourceTree.getSapling())
                    .duration(200).build(consumer);
        }
        if (GENERATE_TREE_ITEM_RECIPES) {
            Item resin = resourceTree.getResin().get();
            new CompactingRecipe.Builder<>(CompactingRecipe::new, ResourceFarm.id(treeId + "_craft_" + BuiltInRegistries.ITEM.getKey(treeItem).getPath()))
                    .require(resin).require(resin).require(resin).require(resin).require(resourceTree.getFruit())
                    .output(treeItem, resourceTree.getResourceTreeConfig().productOutput())
                    .duration(200).build(consumer);
        }
    }
}
