package com.resource_farm.data.tree.builder;

import com.resource_farm.config.ResourceFarmConfigHolder;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.data.tree.ResourceTreeConfig;
import com.resource_farm.utils.VanillaRecipeHelper;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

public class TreeCommonRecipe {

    // === 配置缓存 ===
    private static final boolean GENERATE_STRIPPED_LOG = ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.blockGeneration.generateStrippedLog;
    private static final boolean GENERATE_WOOD = ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.blockGeneration.generateWood;
    private static final boolean GENERATE_STRIPPED_WOOD = ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.blockGeneration.generateStrippedWood;
    private static final boolean GENERATE_PLANKS = ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.blockGeneration.generatePlanks;

    private static final boolean GENERATE_TREE_ITEM_RECIPES = ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.recipeGeneration.generateTreeItemRecipes;
    private static final boolean GENERATE_SAPLING_RECIPES = ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.recipeGeneration.generateSaplingRecipes;

    public static void init(RecipeOutput consumer) {
        ResourceFarmBlocks.ResourceTreeMap.forEach((name, resourceTree) -> {
            ResourceTreeConfig resourceTreeConfig = resourceTree.getResourceTreeConfig();

            // 制作树苗/物品
            if (resourceTree.getTreeItem() != null) {
                if (GENERATE_TREE_ITEM_RECIPES) {
                    VanillaRecipeHelper.addShapedRecipe(consumer, name + "_craft_tree_item",
                            new ItemStack(resourceTree.getTreeItem(), resourceTreeConfig.productOutput()),
                            " A ", "ABA", " A ",
                            'A', resourceTree.getResin(), 'B', resourceTree.getFruit());
                    resourceTree.addRecipeCount();
                }

                if (GENERATE_SAPLING_RECIPES) {
                    VanillaRecipeHelper.addShapedRecipe(consumer, name + "_craft_sapling",
                            new ItemStack(resourceTree.getSapling()),
                            "AAA", "ABA", "AAA",
                            'A', resourceTree.getTreeItem(), 'B', ItemTags.SAPLINGS);
                }
            }

            // 处理配方 烟熏/熔炼获得树脂
            if (ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.recipeGeneration.generateResinRecipes) {
                VanillaRecipeHelper.addSmokingRecipe(consumer, name + "_smoke_resin_from_logs",
                        resourceTree.getLog(), resourceTree.getResin().asStack(4), 0.5f);

                if (GENERATE_STRIPPED_LOG) {
                    VanillaRecipeHelper.addSmokingRecipe(consumer, name + "_smoke_resin_from_stripped_log",
                            resourceTree.getStrippedLog(), resourceTree.getResin().asStack(4), 0.5f);
                }

                if (GENERATE_WOOD) {
                    VanillaRecipeHelper.addSmokingRecipe(consumer, name + "_smoke_resin_from_wood",
                            resourceTree.getWood(), resourceTree.getResin().asStack(4), 0.5f);
                }

                if (GENERATE_STRIPPED_WOOD) {
                    VanillaRecipeHelper.addSmokingRecipe(consumer, name + "_smoke_resin_from_stripped_wood",
                            resourceTree.getStrippedWood(), resourceTree.getResin().asStack(4), 0.5f);
                }

                if (GENERATE_PLANKS) {
                    VanillaRecipeHelper.addSmeltingRecipe(consumer, name + "_smelt_resin_from_planks",
                            resourceTree.getPlanks(), resourceTree.getResin(), 0.1f);
                    VanillaRecipeHelper.addSmokingRecipe(consumer, name + "_smoke_resin_from_planks",
                            resourceTree.getPlanks(), resourceTree.getResin(), 0.1f);
                }
            }

            // 通用配方
            if (ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.recipeGeneration.generateBasicWoodConversionRecipes) {
                if (GENERATE_PLANKS) {
                    VanillaRecipeHelper.addShapelessRecipe(consumer, name + "_craft_planks_from_log",
                            resourceTree.getPlanks().asStack(4), resourceTree.getLog());
                    if (GENERATE_STRIPPED_LOG) {
                        VanillaRecipeHelper.addShapelessRecipe(consumer, name + "_craft_planks_from_stripped_log",
                                resourceTree.getPlanks().asStack(4), resourceTree.getStrippedLog());
                    }
                    if (GENERATE_WOOD) {
                        VanillaRecipeHelper.addShapelessRecipe(consumer, name + "_craft_planks_from_wood",
                                resourceTree.getPlanks().asStack(4), resourceTree.getWood());
                    }
                    if (GENERATE_STRIPPED_WOOD) {
                        VanillaRecipeHelper.addShapelessRecipe(consumer, name + "_craft_planks_from_stripped_wood",
                                resourceTree.getPlanks().asStack(4), resourceTree.getStrippedWood());
                    }
                }
                if (GENERATE_WOOD) {
                    VanillaRecipeHelper.addShapedRecipe(consumer, name + "_craft_wood_from_log",
                            new ItemStack(resourceTree.getWood()),
                            "AA ", "AA ", "   ",
                            'A', resourceTree.getLog());
                }
                if (GENERATE_STRIPPED_LOG && GENERATE_STRIPPED_WOOD) {
                    VanillaRecipeHelper.addShapedRecipe(consumer, name + "_craft_stripped_wood_from_stripped_log",
                            new ItemStack(resourceTree.getStrippedWood()),
                            "AA ", "AA ", "   ",
                            'A', resourceTree.getStrippedLog());
                }
            }
        });
    }
}
