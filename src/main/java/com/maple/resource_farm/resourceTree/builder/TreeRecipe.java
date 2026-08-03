package com.maple.resource_farm.resourceTree.builder;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.api.IntObjectHolder;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.data.misc.ResourceFarmItemTags;
import com.maple.resource_farm.plantPot.recipe.GrowthRecipe;
import com.maple.resource_farm.plantPot.recipe.OutputEntry;
import com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement;
import com.maple.resource_farm.resourceTree.data.ResourceTree;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeConfig;
import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeFertilizeSettings;
import com.maple.resource_farm.utils.RFArrayUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.BlockTagIngredient;

import com.gto.registrylib.util.entry.BlockEntry;
import com.gto.registrylib.util.entry.ItemEntry;
import com.mapleutillib.utils.RegistriesUtils;
import com.mapleutillib.utils.recipe.RecipeHelper;
import com.mapleutillib.utils.recipe.VanillaRecipeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement.addTreeRecipeCount;
import static com.maple.resource_farm.resourceTree.ResourceTreeAccessManagement.getTreeRecipeCount;

/**
 * 资源树配方生成。
 */
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
    private static final Object[] SAPLING_RECIPE_BASE = RFArrayUtils.concatenateArrays("ABC", "DIE", "FGH");
    // === 配置缓存 ===
    private static final boolean GENERATE_STRIPPED_LOG = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedLog;
    private static final boolean GENERATE_WOOD = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateWood;
    private static final boolean GENERATE_STRIPPED_WOOD = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generateStrippedWood;
    private static final boolean GENERATE_PLANKS = ResourceFarmConfigHolder.treeConfigHolder.tree.blockGeneration.generatePlanks;

    private static final boolean GENERATE_TREE_ITEM_RECIPES = ResourceFarmConfigHolder.treeConfigHolder.tree.recipeGeneration.generateTreeItemRecipes;
    private static final boolean GENERATE_SAPLING_RECIPES = ResourceFarmConfigHolder.treeConfigHolder.tree.recipeGeneration.generateSaplingRecipes;

    public static void init(RecipeOutput consumer, HolderLookup.Provider registries) {
        ResourceTreeAccessManagement.ResourceTreeMap.forEach((name, resourceTree) -> {

            ResourceTreeConfig config = resourceTree.getResourceTreeConfig();
            // 1. 基本自动配方
            if (config.automaticBasicRecipe()) {
                SimpleTreeItemAndSaplingRecipeBuild(consumer, name, resourceTree);
            }

            // 2. 额外物品配方
            if (config.extraItemOutputs() != null && !config.extraItemOutputs().isEmpty()) {
                for (ResourceTreeConfig.ExtraItemOutput out : config.extraItemOutputs()) {
                    // 将 ExtraItemOutput 转为 IntObjectHolder<Item>
                    Item item = RegistriesUtils.getItem(out.item().toString());
                    IntObjectHolder<Item> holder = new IntObjectHolder<>(out.count(), item);
                    // 调用现有的 TreeItemRecipeBuild（此方法内部会轮换形状）
                    TreeItemRecipeBuild(consumer, name, resourceTree, holder);
                }
            }

            // 3. 树苗配方（使用原料列表）
            if (config.saplingIngredients() != null && !config.saplingIngredients().get().isEmpty()) {
                // 将 Ingredient 列表转为 Object[] 供 TreeSaplingRecipeBuild 使用（兼容旧参数）
                Object[] ingredients = config.saplingIngredients().get().toArray();
                TreeSaplingRecipeBuild(consumer, name, resourceTree, ingredients);
            }

            // 4. 容器配方（如果存在）
            ResourceTreeConfig.ContainerOutput containerOut = config.containerOutput();
            if (containerOut != null) {
                Item containerItem = RegistriesUtils.getItem(containerOut.container().toString());
                Item outputItem = RegistriesUtils.getItem(containerOut.output().item().toString());
                IntObjectHolder<Item> outputHolder = new IntObjectHolder<>(containerOut.output().count(), outputItem);
                TreeItemRecipeBuildWithExtra(consumer, name, resourceTree,
                        containerItem, containerOut.containerCount(), outputHolder);
            }

            List<ItemLike> logs = new ArrayList<>();
            logs.add(resourceTree.getLog());
            if (GENERATE_STRIPPED_LOG) logs.add(resourceTree.getStrippedLog());
            if (GENERATE_WOOD) logs.add(resourceTree.getWood());
            if (GENERATE_STRIPPED_WOOD) logs.add(resourceTree.getStrippedWood());
            Ingredient ingredientLogs = Ingredient.of(logs.toArray(new ItemLike[0]));

            // 处理配方 烟熏/熔炼获得树脂
            if (ResourceFarmConfigHolder.treeConfigHolder.tree.recipeGeneration.generateResinRecipes) {
                ItemEntry<?> resin = resourceTree.getResin();
                VanillaRecipeHelper.smoking(consumer, ResourceFarm.id(name + "_smoke_resin_from_logs"))
                        .input(ingredientLogs)
                        .output(resin.get(), 4)
                        .experience(0.5f)
                        .save();

                if (GENERATE_PLANKS) {
                    VanillaRecipeHelper.smelting(consumer, ResourceFarm.id(name + "_smelt_resin_from_planks"))
                            .input(resourceTree.getPlanks())
                            .output(resin.get())
                            .experience(0.1f)
                            .save();
                    VanillaRecipeHelper.smoking(consumer, ResourceFarm.id(name + "_smoke_resin_from_planks"))
                            .input(resourceTree.getPlanks())
                            .output(resin.get())
                            .experience(0.1f)
                            .save();
                }
            }

            // 通用配方
            if (ResourceFarmConfigHolder.treeConfigHolder.tree.recipeGeneration.generateBasicWoodConversionRecipes) {
                if (GENERATE_PLANKS) {
                    BlockEntry<?> planks = resourceTree.getPlanks();
                    VanillaRecipeHelper.shapeless(consumer, ResourceFarm.id(name + "_craft_planks_from_log"))
                            .output(planks.get(), 4)
                            .requires(ingredientLogs)
                            .save();
                }
                if (GENERATE_WOOD) {
                    VanillaRecipeHelper.shaped(consumer, ResourceFarm.id(name + "_craft_wood_from_log"))
                            .output(resourceTree.getWood().get())
                            .pattern("AA ", "AA ", "   ")
                            .define('A', resourceTree.getLog())
                            .save();
                }
                if (GENERATE_STRIPPED_LOG && GENERATE_STRIPPED_WOOD) {
                    VanillaRecipeHelper.shaped(consumer, ResourceFarm.id(name + "_craft_stripped_wood_from_stripped_log"))
                            .output(resourceTree.getStrippedWood().get())
                            .pattern("AA ", "AA ", "   ")
                            .define('A', resourceTree.getStrippedLog())
                            .save();
                }
            }

            if (ResourceFarmConfigHolder.treeConfigHolder.tree.recipeGeneration.generateBonsaiPotRecipes) {
                GrowthRecipe.Builder builder = GrowthRecipe.builder(consumer)
                        .id(ResourceFarm.id(name + "_in_bonsai_pot"))
                        .duration(180 * resourceTree.getResourceTreeConfig().growthFrequency())
                        .seed(RecipeHelper.ingredient(resourceTree.getSapling()))
                        .addOutput(OutputEntry.of(resourceTree.getLog().asItem(), 1, 4))
                        .addOutput(OutputEntry.of(resourceTree.getResin().asItem(), 4, 6))
                        .addOutput(OutputEntry.of(resourceTree.getFruit().asItem(), 6, 8))
                        .addOutput(OutputEntry.of(resourceTree.getSapling().asItem(), 1, 2, 0.1f));
                boolean blockSoil = resourceTree.getResourceTreeConfig().customPlaceBlock().get() != Blocks.BARRIER;
                boolean blockTagSoil = resourceTree.getResourceTreeConfig().customPlaceBlockTag() != null;
                if (blockSoil)
                    builder.addSoil(RecipeHelper.ingredient(resourceTree.getResourceTreeConfig().customPlaceBlock().get()));
                if (blockTagSoil)
                    builder.addSoil(new BlockTagIngredient(resourceTree.getResourceTreeConfig().customPlaceBlockTag()).toVanilla());
                if (!blockSoil && !blockTagSoil)
                    builder.addSoil(Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(ResourceFarmItemTags.TREE_SOILS)));
                if (resourceTree.getResourceTreeConfig().fertilizeSetting() != ResourceTreeFertilizeSettings.DEFAULT)
                    builder.fertilizable(false);
                builder.register();
            }
        });
    }

    public static void SimpleTreeItemAndSaplingRecipeBuild(RecipeOutput consumer, String... ids) {
        for (String id : ids) {
            String treeId = id + "_tree";
            ResourceTree resourceTree = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId);
            if (resourceTree == null) return;
            SimpleTreeItemAndSaplingRecipeBuild(consumer, treeId, resourceTree);
        }
    }

    public static void SimpleTreeItemAndSaplingRecipeBuild(RecipeOutput consumer, String treeId, ResourceTree resourceTree) {
        if (resourceTree == null) return;
        // 制作树苗/物品 — 不使用 new ItemStack（组件未绑定时会 NPE）
        if (resourceTree.getTreeItem().get() != Items.BARRIER) {
            if (GENERATE_TREE_ITEM_RECIPES) {
                Item treeItem = resourceTree.getTreeItem().get();
                VanillaRecipeHelper.shaped(consumer, ResourceFarm.id(treeId + "_craft_" + BuiltInRegistries.ITEM.getKey(treeItem).getPath()))
                        .output(treeItem, resourceTree.getResourceTreeConfig().productOutput())
                        .pattern(" A ", "ABA", " A ")
                        .define('A', resourceTree.getResin())
                        .define('B', resourceTree.getFruit())
                        .save();
                addTreeRecipeCount(treeId, 1);
            }

            if (GENERATE_SAPLING_RECIPES) {
                VanillaRecipeHelper.shaped(consumer, ResourceFarm.id(treeId + "_craft_sapling"))
                        .output(resourceTree.getSapling().get())
                        .pattern("AAA", "ABA", "AAA")
                        .define('A', resourceTree.getTreeItem().get())
                        .define('B', ItemTags.SAPLINGS)
                        .save();
            }
        }
    }

    @SafeVarargs
    public static void TreeItemAndSaplingRecipeBuild(RecipeOutput consumer, String id, IntObjectHolder<Item>... treeItems) {
        TreeSaplingRecipeBuild(consumer, id, Arrays.stream(treeItems).map(holder -> holder.obj()).toArray(Object[]::new));
        TreeItemRecipeBuild(consumer, id, treeItems);
    }

    public static void TreeSaplingRecipeBuild(RecipeOutput consumer, String id, Object... treeItems) {
        if (!GENERATE_SAPLING_RECIPES) return;
        String treeId = id + "_tree";
        ResourceTree resourceTree = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;

        TreeSaplingRecipeBuild(consumer, treeId, resourceTree, treeItems);
    }

    public static void TreeSaplingRecipeBuild(RecipeOutput consumer, String treeId, ResourceTree resourceTree, Object... treeItems) {
        if (!GENERATE_SAPLING_RECIPES) return;
        Object[] newTreeItems = refactorShape(treeItems);

        VanillaRecipeHelper.shaped(consumer, ResourceFarm.id(treeId + "_craft_sapling"))
                .output(resourceTree.getSapling().get())
                .pattern(RFArrayUtils.concatenateArrays("ABC", "DIE", "FGH", RFArrayUtils.insertCharBeforeElement(RFArrayUtils.concatenateArrays(
                        newTreeItems, ItemTags.SAPLINGS))))
                .save();
    }

    public static void AscensionTreeSaplingRecipeBuild(RecipeOutput consumer, String id, Item items, String... ids) {
        if (!GENERATE_SAPLING_RECIPES) return;
        String treeId = id + "_tree";
        ResourceTree resourceTree = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;

        Object[] treeItems = new Object[ids.length];
        for (int i = 0; i < ids.length; i++) {
            ResourceTree tree = ResourceTreeAccessManagement.ResourceTreeMap.get(ids[i] + "_tree");
            if (tree == null) return;
            treeItems[i] = tree.getResin();
        }

        Object[] newTreeItems = refactorShape(treeItems);

        VanillaRecipeHelper.shaped(consumer, ResourceFarm.id("ascension_" + treeId + "_craft_sapling"))
                .output(resourceTree.getSapling().get())
                .pattern(RFArrayUtils.concatenateArrays(SAPLING_RECIPE_BASE, RFArrayUtils.insertCharBeforeElement(RFArrayUtils.concatenateArrays(
                        newTreeItems, ItemTags.SAPLINGS))))
                .save();
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
        ResourceTree resourceTree = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;

        TreeItemRecipeBuild(consumer, treeId, resourceTree, treeItems);
    }

    @SafeVarargs
    public static void TreeItemRecipeBuild(RecipeOutput consumer, String treeId, ResourceTree resourceTree, IntObjectHolder<Item>... treeItems) {
        if (!GENERATE_TREE_ITEM_RECIPES) return;

        Object[] breedingOutput = { 'A', resourceTree.getResin(), 'B', resourceTree.getFruit() };
        Set<IntObjectHolder<Item>> itemSet = Arrays.stream(treeItems).collect(Collectors.toSet());
        for (IntObjectHolder<Item> item : itemSet) {
            VanillaRecipeHelper.shaped(consumer, ResourceFarm.id(treeId + "_craft_tree_" + BuiltInRegistries.ITEM.getKey(item.obj()).getPath()))
                    .output(item.obj(), item.number())
                    .pattern(RFArrayUtils.concatenateArrays(SHAPE[getTreeRecipeCount(treeId)], breedingOutput))
                    .save();
            addTreeRecipeCount(treeId, 1);
        }
    }

    public static void TreeItemRecipeBuildWithExtra(RecipeOutput consumer, String id, Item extra, int count, IntObjectHolder<Item> item) {
        String treeId = id + "_tree";
        ResourceTree resourceTree = ResourceTreeAccessManagement.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;

        TreeItemRecipeBuildWithExtra(consumer, treeId, resourceTree, extra, count, item);
    }

    public static void TreeItemRecipeBuildWithExtra(RecipeOutput consumer, String treeId, ResourceTree resourceTree,
                                                    Item extra, int count, IntObjectHolder<Item> item) {
        Object[] breedingOutput = { 'A', resourceTree.getResin(), 'B', resourceTree.getFruit(), 'C', extra };
        VanillaRecipeHelper.shaped(consumer, ResourceFarm.id(treeId + "_craft_tree_" + BuiltInRegistries.ITEM.getKey(item.obj()).getPath()))
                .output(item.obj(), item.number())
                .pattern(RFArrayUtils.concatenateArrays(SHAPEWithContainer[count - 1], breedingOutput))
                .save();
    }
}
