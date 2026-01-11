package com.resource_farm.data.tree.builder;

import com.resource_farm.api.IntObjectHolder;
import com.resource_farm.config.ResourceFarmConfigHolder;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.data.tree.ResourceTree;
import com.resource_farm.utils.RFArrayUtils;
import com.resource_farm.utils.VanillaRecipeHelper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.resource_farm.common.Manager.ResourceFarmRecipesManager.treeCommonRecipeCount;

public class TreeRecipe {

    public static final String[][] SHAPE = {
            { " A ", "ABA", " A " },
            { "A A", " B ", "A A" },
            { "AAA", " B ", " A " },
            { " A ", " B ", "AAA" },
            { "A  ", "ABA", "A  " },
            { "  A", "ABA", "  A" },
            { "AA ", " B ", " AA" },
            { " AA", " B ", "AA " },
            { "  A", "ABA", "A  " },
            { "A  ", "ABA", "  A" },

            { "AA ", " BA", " A " },
            { " AA", " B ", "A A" },
            { "  A", "ABA", " A " },
            { "A  ", " BA", "A A" },
            { " A ", "AB ", " AA" },
            { "A A", " B ", "AA " },
            { " A ", "ABA", "A  " },
            { "A A", "AB ", "  A" },

            { " AA", "A B", " A " },
            { "AA ", " B ", "A A" },
            { "A  ", "ABA", " A " },
            { "  A", "A B", "A A" },
            { " A ", " BA", "AA " },
            { "A A", " B ", " AA" },
            { " A ", "ABA", "  A" },
            { "A A", " BA", "A  " }
    };
    public static final String[][] SHAPEWithTwo = {
            { " A ", "BCB", " A " },
            { "A B", " C ", "B A" },
            { "AA ", " C ", " BB" },
            { " AA", " C ", "BB " },
            { "  A", "BCA", "B  " },
            { "B  ", "BCA", "  A" },
            { "ABA", " C ", " B " },
            { " B ", " C ", "ABA" },
            { "A  ", "BCB", "A  " },
            { "  A", "BCB", "  A" },
    };
    public static final String[][] SHAPEWithContainer = {
            { "CA ", "ABA", " A " },
            { "CA ", "ABA", " AC" },
            { "CAC", "ABA", " AC" },
            { "CAC", "ABA", "CAC" },
    };
    // === 配置缓存 ===
    private static final boolean GENERATE_STRIPPED_LOG = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog;
    private static final boolean GENERATE_WOOD = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood;
    private static final boolean GENERATE_STRIPPED_WOOD = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood;
    private static final boolean GENERATE_PLANKS = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks;

    private static final boolean GENERATE_TREE_ITEM_RECIPES = ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateTreeItemRecipes;
    private static final boolean GENERATE_SAPLING_RECIPES = ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateSaplingRecipes;

    public static void init(RecipeOutput consumer) {
        ResourceFarmBlocks.ResourceTreeMap.forEach((name, resourceTree) -> {

            // 制作树苗/物品
            if (resourceTree.getResourceTreeConfig().automaticBasicRecipe())
                SimpleTreeItemAndSaplingRecipeBuild(consumer, name, resourceTree);

            // 处理配方 烟熏/熔炼获得树脂
            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateResinRecipes) {
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
            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateBasicWoodConversionRecipes) {
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

    public static void SimpleTreeItemAndSaplingRecipeBuild(RecipeOutput consumer, String... ids) {
        for (String id : ids) {
            String treeId = id + "_tree";
            ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
            if (resourceTree == null) return;
            SimpleTreeItemAndSaplingRecipeBuild(consumer, treeId, resourceTree);
        }
    }

    public static void SimpleTreeItemAndSaplingRecipeBuild(RecipeOutput consumer, String treeId, ResourceTree resourceTree) {
        if (resourceTree == null) return;
        // 制作树苗/物品
        if (resourceTree.getTreeItem().get() != Items.BARRIER) {
            if (GENERATE_TREE_ITEM_RECIPES) {
                VanillaRecipeHelper.addShapedRecipe(consumer, treeId + "_craft_tree_item",
                        new ItemStack(resourceTree.getTreeItem().get(), resourceTree.getResourceTreeConfig().productOutput()),
                        " A ", "ABA", " A ",
                        'A', resourceTree.getResin(), 'B', resourceTree.getFruit());
                treeCommonRecipeCount.addTo(treeId, 1);
            }

            if (GENERATE_SAPLING_RECIPES) {
                VanillaRecipeHelper.addShapedRecipe(consumer, treeId + "_craft_sapling",
                        new ItemStack(resourceTree.getSapling()),
                        "AAA", "ABA", "AAA",
                        'A', resourceTree.getTreeItem().get(), 'B', ItemTags.SAPLINGS);
            }
        }
    }

    @SafeVarargs
    public static void TreeItemAndSaplingRecipeBuild(RecipeOutput consumer, String id, IntObjectHolder<Item>... treeItems) {
        TreeSaplingRecipeBuild(consumer, id, Arrays.stream(treeItems).map(holder -> holder.obj).toArray(Object[]::new));
        TreeItemRecipeBuild(consumer, id, treeItems);
    }

    public static void TreeSaplingRecipeBuild(RecipeOutput consumer, String id, Object... treeItems) {
        if (!GENERATE_SAPLING_RECIPES) return;
        String treeId = id + "_tree";
        ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;

        TreeSaplingRecipeBuild(consumer, treeId, resourceTree, treeItems);
    }

    public static void TreeSaplingRecipeBuild(RecipeOutput consumer, String treeId, ResourceTree resourceTree, Object... treeItems) {
        if (!GENERATE_SAPLING_RECIPES) return;
        Object[] newTreeItems = refactorShape(treeItems);

        VanillaRecipeHelper.addShapedRecipe(consumer, treeId + "_craft_sapling",
                new ItemStack(resourceTree.getSapling()),
                RFArrayUtils.concatenateArrays("ABC", "DIE", "FGH", RFArrayUtils.insertCharBeforeElement(RFArrayUtils.concatenateArrays(
                        newTreeItems, ItemTags.SAPLINGS))));
    }

    public static void AscensionTreeSaplingRecipeBuild(RecipeOutput consumer, String id, Item items, String... ids) {
        if (!GENERATE_SAPLING_RECIPES) return;
        String treeId = id + "_tree";
        ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;

        Object[] treeItems = new Object[ids.length];
        for (int i = 0; i < ids.length; i++) {
            ResourceTree tree = ResourceFarmBlocks.ResourceTreeMap.get(ids[i] + "_tree");
            if (tree == null) return;
            treeItems[i] = tree.getResin();
        }

        Object[] newTreeItems = refactorShape(treeItems);

        VanillaRecipeHelper.addShapedRecipe(consumer, "ascension_" + treeId + "_craft_sapling",
                new ItemStack(resourceTree.getSapling()),
                RFArrayUtils.concatenateArrays("ABC", "DIE", "FGH", RFArrayUtils.insertCharBeforeElement(RFArrayUtils.concatenateArrays(
                        newTreeItems, ItemTags.SAPLINGS))));
    }

    public static Object[] refactorShape(Object[] oldShape) {
        return switch (oldShape.length) {
            case 1 -> new Object[] { oldShape[0], oldShape[0], oldShape[0], oldShape[0], oldShape[0], oldShape[0], oldShape[0], oldShape[0] };
            case 2 -> new Object[] { oldShape[0], oldShape[1], oldShape[0], oldShape[1], oldShape[1], oldShape[0], oldShape[1], oldShape[0] };
            case 3 -> new Object[] { oldShape[1], oldShape[0], oldShape[0], oldShape[2], oldShape[2], oldShape[0], oldShape[0], oldShape[1] };
            case 4 -> new Object[] { oldShape[0], oldShape[1], oldShape[2], oldShape[3], oldShape[3], oldShape[2], oldShape[1], oldShape[0] };
            case 5 -> new Object[] { oldShape[0], oldShape[1], oldShape[2], oldShape[3], oldShape[4], oldShape[2], oldShape[1], oldShape[0] };
            case 6 -> new Object[] { oldShape[2], oldShape[0], oldShape[3], oldShape[1], oldShape[1], oldShape[4], oldShape[0], oldShape[5] };
            case 7 -> new Object[] { oldShape[0], oldShape[1], oldShape[2], oldShape[3], oldShape[4], oldShape[5], oldShape[6], oldShape[0] };
            default -> new Object[] { oldShape[0], oldShape[1], oldShape[2], oldShape[3], oldShape[4], oldShape[5], oldShape[6], oldShape[7] };
        };
    }

    @SafeVarargs
    public static void TreeItemRecipeBuild(RecipeOutput consumer, String id, IntObjectHolder<Item>... treeItems) {
        if (!GENERATE_TREE_ITEM_RECIPES) return;
        String treeId = id + "_tree";
        ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;

        TreeItemRecipeBuild(consumer, treeId, resourceTree, treeItems);
    }

    @SafeVarargs
    public static void TreeItemRecipeBuild(RecipeOutput consumer, String treeId, ResourceTree resourceTree, IntObjectHolder<Item>... treeItems) {
        if (!GENERATE_TREE_ITEM_RECIPES) return;

        Object[] breedingOutput = { 'A', resourceTree.getResin(), 'B', resourceTree.getFruit() };
        Set<IntObjectHolder<Item>> itemSet = Arrays.stream(treeItems).collect(Collectors.toSet());
        for (IntObjectHolder<Item> item : itemSet) {
            VanillaRecipeHelper.addShapedRecipe(consumer, treeId + "_craft_tree_" + BuiltInRegistries.ITEM.getKey(item.obj).getPath(),
                    new ItemStack(item.obj, item.number), RFArrayUtils.concatenateArrays(SHAPE[treeCommonRecipeCount.getInt(treeId)], breedingOutput));
            treeCommonRecipeCount.addTo(treeId, 1);
        }
    }

    public static void TreeItemRecipeBuildWithExtra(RecipeOutput consumer, String id, Item extra, int count, IntObjectHolder<Item> item) {
        String treeId = id + "_tree";
        ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;

        TreeItemRecipeBuildWithExtra(consumer, treeId, resourceTree, extra, count, item);
    }

    public static void TreeItemRecipeBuildWithExtra(RecipeOutput consumer, String treeId, ResourceTree resourceTree,
                                                    Item extra, int count, IntObjectHolder<Item> item) {
        Object[] breedingOutput = { 'A', resourceTree.getResin(), 'B', resourceTree.getFruit(), 'C', extra };
        VanillaRecipeHelper.addShapedRecipe(consumer, treeId + "_craft_tree_" + BuiltInRegistries.ITEM.getKey(item.obj).getPath(),
                new ItemStack(item.obj, item.number), RFArrayUtils.concatenateArrays(SHAPEWithContainer[count - 1], breedingOutput));
    }
}
