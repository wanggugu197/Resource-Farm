package com.resource_farm.data.tree.builder;

import com.resource_farm.api.IntObjectHolder;
import com.resource_farm.api.ResourceOre.ResourceOreType;
import com.resource_farm.api.ResourceTree.ResourceTreeType;
import com.resource_farm.api.block.FertilizeSettings;
import com.resource_farm.config.ResourceFarmConfigHolder;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.data.tree.ResourceTree;
import com.resource_farm.data.tree.ResourceTreeConfig;
import com.resource_farm.utils.RFArrayUtils;
import com.resource_farm.utils.VanillaRecipeHelper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class TreeRegister {

    public static void createResourceTree(ResourceTreeConfig resourceTreeConfig) {
        String treeId = resourceTreeConfig.id() + "_tree";
        ResourceFarmBlocks.ResourceTreeMap.put(treeId, ResourceTree.create(resourceTreeConfig));
        ResourceFarmBlocks.ResourceTreeKeyList.add(treeId);
    }

    public static void createResourceTree(@Nullable String correspondingItem,
                                          @Nullable String translateKey,
                                          int productOutput,
                                          ResourceTreeType treeStyle,
                                          ResourceOreType oreStyle,
                                          FertilizeSettings fertilizeSetting,
                                          int growthFrequency,
                                          @Nullable Block customPlaceBlock,
                                          @Nullable TagKey<Block> customPlaceBlockTag,
                                          int lightLevel,
                                          int colors) {
        createResourceTree(ResourceTreeConfig.create(
                correspondingItem, translateKey, productOutput, treeStyle, oreStyle, fertilizeSetting, growthFrequency,
                customPlaceBlock, customPlaceBlockTag, lightLevel, colors));
    }

    public static void createSimpleResourceTree(@Nullable String correspondingItem,
                                                @Nullable String translateKey,
                                                int productOutput,
                                                ResourceTreeType treeStyle,
                                                ResourceOreType oreStyle,
                                                int lightLevel,
                                                int colors) {
        createResourceTree(correspondingItem, translateKey, productOutput, treeStyle, oreStyle,
                FertilizeSettings.DEFAULT, 10, null, null, lightLevel, colors);
    }

    public static void createSimpleResourceTree(@Nullable String correspondingItem,
                                                @Nullable String translateKey,
                                                int productOutput,
                                                ResourceTreeType treeStyle,
                                                ResourceOreType oreStyle,
                                                int colors) {
        createResourceTree(correspondingItem, translateKey, productOutput, treeStyle, oreStyle,
                FertilizeSettings.DEFAULT, 10, null, null, 0, colors);
    }

    public static void createMissingItemResourceTree(@Nullable String translateKey,
                                                     ResourceTreeType treeStyle,
                                                     ResourceOreType oreStyle,
                                                     int colors) {
        createResourceTree(null, translateKey, 1, treeStyle, oreStyle,
                FertilizeSettings.DEFAULT, 10, null, null, 0, colors);
    }

    public static void createResourceTrees(ResourceTreeConfig... resourceTreeConfigs) {
        for (ResourceTreeConfig resourceTreeConfig : resourceTreeConfigs) createResourceTree(resourceTreeConfig);
    }

    public static void removeResourceTree(String treeId) {
        ResourceFarmBlocks.ResourceTreeMap.remove(treeId);
        ResourceFarmBlocks.ResourceTreeKeyList.add(treeId);
    }

    public static void removeResourceTrees(String... treeIds) {
        for (String treeId : treeIds) removeResourceTree(treeId);
    }

    private static final boolean GENERATE_TREE_ITEM_RECIPES = ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.recipeGeneration.generateTreeItemRecipes;
    private static final boolean GENERATE_SAPLING_RECIPES = ResourceFarmConfigHolder.resourceFarmConfigHolder.tree.recipeGeneration.generateSaplingRecipes;

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

    @SafeVarargs
    public static void TreeItemRecipeBuild(RecipeOutput consumer, String id, boolean generateSaplingRecipe, IntObjectHolder<Item>... treeItems) {
        if (generateSaplingRecipe)
            TreeSaplingRecipeBuild(consumer, id, Arrays.stream(treeItems).map(holder -> holder.obj).toArray(Object[]::new));

        if (!GENERATE_TREE_ITEM_RECIPES) return;

        String treeId = id + "_tree";
        ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;

        Object[] breedingOutput = { 'A', resourceTree.getResin(), 'B', resourceTree.getFruit() };
        Set<IntObjectHolder<Item>> itemSet = Arrays.stream(treeItems).collect(Collectors.toSet());
        for (IntObjectHolder<Item> item : itemSet) {
            VanillaRecipeHelper.addShapedRecipe(consumer, treeId + "_craft_tree_" + BuiltInRegistries.ITEM.getKey(item.obj).getPath(),
                    new ItemStack(item.obj, item.number), RFArrayUtils.concatenateArrays(SHAPE[resourceTree.getRecipeCount()], breedingOutput));
            resourceTree.addRecipeCount();
        }
    }

    public static void TreeSaplingRecipeBuild(RecipeOutput consumer, String id, Object... treeItems) {
        if (!GENERATE_SAPLING_RECIPES) return;
        String treeId = id + "_tree";
        ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;

        Object[] newTreeItems = switch (treeItems.length) {
            case 1 -> new Object[] { treeItems[0], treeItems[0], treeItems[0], treeItems[0], treeItems[0], treeItems[0], treeItems[0], treeItems[0] };
            case 2 -> new Object[] { treeItems[0], treeItems[1], treeItems[0], treeItems[1], treeItems[1], treeItems[0], treeItems[1], treeItems[0] };
            case 3 -> new Object[] { treeItems[1], treeItems[0], treeItems[0], treeItems[2], treeItems[2], treeItems[0], treeItems[0], treeItems[1] };
            case 4 -> new Object[] { treeItems[0], treeItems[1], treeItems[2], treeItems[3], treeItems[3], treeItems[2], treeItems[1], treeItems[0] };
            case 5 -> new Object[] { treeItems[0], treeItems[1], treeItems[2], treeItems[3], treeItems[4], treeItems[2], treeItems[1], treeItems[0] };
            case 6 -> new Object[] { treeItems[2], treeItems[0], treeItems[3], treeItems[1], treeItems[1], treeItems[4], treeItems[0], treeItems[5] };
            case 7 -> new Object[] { treeItems[0], treeItems[1], treeItems[2], treeItems[3], treeItems[4], treeItems[5], treeItems[6], treeItems[0] };
            default -> new Object[] { treeItems[0], treeItems[1], treeItems[2], treeItems[3], treeItems[4], treeItems[5], treeItems[6], treeItems[7] };
        };

        VanillaRecipeHelper.addShapedRecipe(consumer, treeId + "_craft_sapling",
                new ItemStack(resourceTree.getSapling()),
                RFArrayUtils.concatenateArrays("ABC", "DIE", "FGH", RFArrayUtils.insertCharBeforeElement(RFArrayUtils.concatenateArrays(
                        newTreeItems, ItemTags.SAPLINGS))));
    }

    public static final String[][] SHAPEWithContainer = {
            { "CA ", "ABA", " A " },
            { "CA ", "ABA", " AC" },
            { "CAC", "ABA", " AC" },
            { "CAC", "ABA", "CAC" },
    };

    public static void QuickTreeItemRecipeBuildWithContainer(RecipeOutput consumer, String id, Item container, Item item, int count) {
        String treeId = id + "_tree";
        ResourceTree resourceTree = ResourceFarmBlocks.ResourceTreeMap.get(treeId);
        if (resourceTree == null) return;

        Object[] breedingOutput = { 'A', resourceTree.getResin(), 'B', resourceTree.getFruit(), 'C', container };
        VanillaRecipeHelper.addShapedRecipe(consumer, treeId + "_craft_tree_" + BuiltInRegistries.ITEM.getKey(item).getPath(),
                new ItemStack(item, count), RFArrayUtils.concatenateArrays(SHAPEWithContainer[count - 1], breedingOutput));
    }
}
