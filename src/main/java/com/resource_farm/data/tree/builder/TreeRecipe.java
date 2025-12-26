package com.resource_farm.data.tree.builder;

import com.resource_farm.config.ConfigHolder;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.utils.VanillaRecipeHelper;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

public class TreeRecipe {

    // === 配置缓存 ===
    private static final boolean GENERATE_STRIPPED_LOG = ConfigHolder.INSTANCE.tree.blockGeneration.generateStrippedLog;
    private static final boolean GENERATE_WOOD = ConfigHolder.INSTANCE.tree.blockGeneration.generateWood;
    private static final boolean GENERATE_STRIPPED_WOOD = ConfigHolder.INSTANCE.tree.blockGeneration.generateStrippedWood;
    private static final boolean GENERATE_PLANKS = ConfigHolder.INSTANCE.tree.blockGeneration.generatePlanks;

    public static void init(RecipeOutput consumer) {
        ResourceFarmBlocks.ResourceTreeMap.forEach((name, resourceTree) -> {

            // 制作树苗/物品
            if (resourceTree.getTreeItem() != null) {
                if (ConfigHolder.INSTANCE.tree.recipeGeneration.generateTreeItemRecipes) {
                    VanillaRecipeHelper.addShapedRecipe(consumer, name + "_craft_tree_item",
                            new ItemStack(resourceTree.getTreeItem()),
                            "AAA", "ABA", "AAA",
                            'A', resourceTree.getResin(), 'B', resourceTree.getFruit());
                }

                if (ConfigHolder.INSTANCE.tree.recipeGeneration.generateSaplingRecipes) {
                    VanillaRecipeHelper.addShapedRecipe(consumer, name + "_craft_sapling",
                            new ItemStack(resourceTree.getSapling()),
                            "AAA", "ABA", "AAA",
                            'A', resourceTree.getTreeItem(), 'B', ItemTags.SAPLINGS);
                }
            }

            // 处理配方 烟熏/熔炼获得树脂
            if (ConfigHolder.INSTANCE.tree.recipeGeneration.generateResinRecipes) {
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
            if (ConfigHolder.INSTANCE.tree.recipeGeneration.generateBasicWoodConversionRecipes) {
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
