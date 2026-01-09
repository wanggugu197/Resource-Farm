package com.resource_farm.common.block.grower;

import net.minecraft.core.HolderSet;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.*;
import net.minecraft.world.level.levelgen.feature.rootplacers.AboveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.*;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static com.resource_farm.data.ResourceFarmBlocks.ResourceTreeMap;

public class ResourceTreeFeatures {

    public static ConfiguredFeature<?, ?> getResourceTreeConfiguredFeature(String treeId, String style) {
        ResourceTreeConfiguration treeConfiguration = ResourceTreeFeatures.ResourceTreeConfigurationMap.get(style);
        treeConfiguration.setTrunkProvider(BlockStateProvider.simple(ResourceTreeMap.get(treeId).getLog().getDefaultState()));
        treeConfiguration.setFoliageProvider(BlockStateProvider.simple(ResourceTreeMap.get(treeId).getLeaves().getDefaultState()));
        return new ConfiguredFeature<>(Feature.TREE, treeConfiguration.get());
    }

    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder OAK = new ResourceTreeConfiguration.ResourceTreeConfigurationBuilder(
            BlockStateProvider.simple(Blocks.OAK_LOG), new StraightTrunkPlacer(4, 2, 0),
            BlockStateProvider.simple(Blocks.OAK_LEAVES), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
            new TwoLayersFeatureSize(1, 0, 1));
    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder FANCY_OAK = new ResourceTreeConfiguration.ResourceTreeConfigurationBuilder(
            BlockStateProvider.simple(Blocks.OAK_LOG), new FancyTrunkPlacer(3, 11, 0),
            BlockStateProvider.simple(Blocks.OAK_LEAVES), new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
            new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))).ignoreVines();
    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder OAK_BEES_005 = OAK
            .decorators(List.of(new BeehiveDecorator(0.05F)));
    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder FANCY_OAK_BEES_005 = FANCY_OAK
            .decorators(List.of(new BeehiveDecorator(0.05F)));

    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder DARK_OAK = new ResourceTreeConfiguration.ResourceTreeConfigurationBuilder(
            BlockStateProvider.simple(Blocks.DARK_OAK_LOG), new DarkOakTrunkPlacer(6, 2, 1),
            BlockStateProvider.simple(Blocks.DARK_OAK_LEAVES), new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0)),
            new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty())).ignoreVines();

    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder BIRCH = new ResourceTreeConfiguration.ResourceTreeConfigurationBuilder(
            BlockStateProvider.simple(Blocks.BIRCH_LOG), new StraightTrunkPlacer(5, 2, 0),
            BlockStateProvider.simple(Blocks.BIRCH_LEAVES), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
            new TwoLayersFeatureSize(1, 0, 1));
    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder BIRCH_BEES_005 = BIRCH
            .decorators(List.of(new BeehiveDecorator(0.05F)));

    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder SPRUCE = new ResourceTreeConfiguration.ResourceTreeConfigurationBuilder(
            BlockStateProvider.simple(Blocks.SPRUCE_LOG), new StraightTrunkPlacer(5, 2, 1),
            BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)),
            new TwoLayersFeatureSize(2, 0, 2)).ignoreVines();
    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder MEGA_SPRUCE = new ResourceTreeConfiguration.ResourceTreeConfigurationBuilder(
            BlockStateProvider.simple(Blocks.SPRUCE_LOG), new GiantTrunkPlacer(13, 2, 14),
            BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17)),
            new TwoLayersFeatureSize(1, 1, 2));
    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder MEGA_PINE = new ResourceTreeConfiguration.ResourceTreeConfigurationBuilder(
            BlockStateProvider.simple(Blocks.SPRUCE_LOG), new GiantTrunkPlacer(13, 2, 14),
            BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(3, 7)),
            new TwoLayersFeatureSize(1, 1, 2));

    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder JUNGLE_TREE_NO_VINE = new ResourceTreeConfiguration.ResourceTreeConfigurationBuilder(
            BlockStateProvider.simple(Blocks.JUNGLE_LOG), new StraightTrunkPlacer(4, 8, 0),
            BlockStateProvider.simple(Blocks.JUNGLE_LEAVES), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
            new TwoLayersFeatureSize(1, 0, 1)).ignoreVines();
    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder MEGA_JUNGLE_TREE = new ResourceTreeConfiguration.ResourceTreeConfigurationBuilder(
            BlockStateProvider.simple(Blocks.JUNGLE_LOG), new MegaJungleTrunkPlacer(10, 2, 19),
            BlockStateProvider.simple(Blocks.JUNGLE_LEAVES), new MegaJungleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2),
            new TwoLayersFeatureSize(1, 1, 2)).decorators(ImmutableList.of(TrunkVineDecorator.INSTANCE, new LeaveVineDecorator(0.125F)));

    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder ACACIA = new ResourceTreeConfiguration.ResourceTreeConfigurationBuilder(
            BlockStateProvider.simple(Blocks.ACACIA_LOG), new ForkingTrunkPlacer(5, 2, 2),
            BlockStateProvider.simple(Blocks.ACACIA_LEAVES), new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
            new TwoLayersFeatureSize(1, 0, 2)).ignoreVines();

    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder CHERRY = new ResourceTreeConfiguration.ResourceTreeConfigurationBuilder(
            BlockStateProvider.simple(Blocks.CHERRY_LOG),
            new CherryTrunkPlacer(7, 1, 0,
                    new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(1), 1).add(ConstantInt.of(2), 1)
                            .add(ConstantInt.of(3), 1).build()),
                    UniformInt.of(2, 4), UniformInt.of(-4, -3), UniformInt.of(-1, 0)),
            BlockStateProvider.simple(Blocks.CHERRY_LEAVES),
            new CherryFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(5), 0.25F, 0.5F, 0.16666667F, 0.33333334F),
            new TwoLayersFeatureSize(1, 0, 2)).ignoreVines();
    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder CHERRY_BEES_005 = CHERRY
            .decorators(List.of(new BeehiveDecorator(0.05F)));

    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder MANGROVE = new ResourceTreeConfiguration.ResourceTreeConfigurationBuilder(
            BlockStateProvider.simple(Blocks.MANGROVE_LOG),
            new UpwardsBranchingTrunkPlacer(2, 1, 4, UniformInt.of(1, 4), 0.5F, UniformInt.of(0, 1), HolderSet.empty()),
            BlockStateProvider.simple(Blocks.MANGROVE_LEAVES),
            new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70),
            Optional.of(new MangroveRootPlacer(UniformInt.of(1, 3), BlockStateProvider.simple(Blocks.MANGROVE_ROOTS),
                    Optional.of(new AboveRootPlacement(BlockStateProvider.simple(Blocks.MOSS_CARPET), 0.5F)),
                    new MangroveRootPlacement(HolderSet.empty(), HolderSet.direct(Block::builtInRegistryHolder, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS),
                            BlockStateProvider.simple(Blocks.MUDDY_MANGROVE_ROOTS), 8, 15, 0.2F))),
            new TwoLayersFeatureSize(2, 0, 2))
            .decorators(List.of(new LeaveVineDecorator(0.075F), new BeehiveDecorator(0.01F)))
            .ignoreVines();
    public static final ResourceTreeConfiguration.ResourceTreeConfigurationBuilder TALL_MANGROVE = new ResourceTreeConfiguration.ResourceTreeConfigurationBuilder(
            BlockStateProvider.simple(Blocks.MANGROVE_LOG),
            new UpwardsBranchingTrunkPlacer(4, 1, 9, UniformInt.of(1, 6), 0.5F, UniformInt.of(0, 1), HolderSet.empty()),
            BlockStateProvider.simple(Blocks.MANGROVE_LEAVES),
            new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70),
            Optional.of(new MangroveRootPlacer(UniformInt.of(3, 7), BlockStateProvider.simple(Blocks.MANGROVE_ROOTS),
                    Optional.of(new AboveRootPlacement(BlockStateProvider.simple(Blocks.MOSS_CARPET), 0.5F)),
                    new MangroveRootPlacement(HolderSet.empty(), HolderSet.direct(Block::builtInRegistryHolder, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS),
                            BlockStateProvider.simple(Blocks.MUDDY_MANGROVE_ROOTS), 8, 15, 0.2F))),
            new TwoLayersFeatureSize(3, 0, 2))
            .decorators(List.of(new LeaveVineDecorator(0.075F), new BeehiveDecorator(0.01F)))
            .ignoreVines();

    public static final Object2ObjectOpenHashMap<String, ResourceTreeConfiguration> ResourceTreeConfigurationMap = new Object2ObjectOpenHashMap<>();

    static {
        ResourceTreeConfigurationMap.put("oak", OAK.build());
        ResourceTreeConfigurationMap.put("fancy_oak", FANCY_OAK.build());
        ResourceTreeConfigurationMap.put("oak_bees_005", OAK_BEES_005.build());
        ResourceTreeConfigurationMap.put("fancy_oak_bees_005", FANCY_OAK_BEES_005.build());

        ResourceTreeConfigurationMap.put("dark_oak", DARK_OAK.build());

        ResourceTreeConfigurationMap.put("birch", BIRCH.build());
        ResourceTreeConfigurationMap.put("birch_bees_005", BIRCH_BEES_005.build());

        ResourceTreeConfigurationMap.put("spruce", SPRUCE.build());
        ResourceTreeConfigurationMap.put("mega_spruce", MEGA_SPRUCE.build());
        ResourceTreeConfigurationMap.put("mega_pine", MEGA_PINE.build());

        ResourceTreeConfigurationMap.put("jungle_tree_no_vine", JUNGLE_TREE_NO_VINE.build());
        ResourceTreeConfigurationMap.put("mega_jungle_tree", MEGA_JUNGLE_TREE.build());

        ResourceTreeConfigurationMap.put("acacia", ACACIA.build());

        ResourceTreeConfigurationMap.put("cherry", CHERRY.build());
        ResourceTreeConfigurationMap.put("cherry_bees_005", CHERRY_BEES_005.build());

        ResourceTreeConfigurationMap.put("mangrove", MANGROVE.build());
        ResourceTreeConfigurationMap.put("tall_mangrove", TALL_MANGROVE.build());
    }
}
