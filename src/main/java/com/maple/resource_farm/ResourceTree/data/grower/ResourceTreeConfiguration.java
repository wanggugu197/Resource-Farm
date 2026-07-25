package com.maple.resource_farm.ResourceTree.data.grower;

import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

import java.util.List;
import java.util.Optional;

/**
 * 原版的<strong>结构骨架</strong>：
 */
public final class ResourceTreeConfiguration {

    private final TrunkPlacer trunkPlacer;
    private final FoliagePlacer foliagePlacer;
    private final Optional<RootPlacer> rootPlacer;
    private final FeatureSize minimumSize;
    private final List<TreeDecorator> decorators;
    private final boolean ignoreVines;
    private final BlockStateProvider belowTrunkProvider;

    public ResourceTreeConfiguration(TrunkPlacer trunkPlacer,
                                     FoliagePlacer foliagePlacer,
                                     Optional<RootPlacer> rootPlacer,
                                     FeatureSize minimumSize,
                                     List<TreeDecorator> decorators,
                                     boolean ignoreVines,
                                     BlockStateProvider belowTrunkProvider) {
        this.trunkPlacer = trunkPlacer;
        this.foliagePlacer = foliagePlacer;
        this.rootPlacer = rootPlacer;
        this.minimumSize = minimumSize;
        this.decorators = List.copyOf(decorators);
        this.ignoreVines = ignoreVines;
        this.belowTrunkProvider = belowTrunkProvider;
    }

    /** 从已解析的原版树配置提取结构（忽略 trunk/foliage provider）。 */
    public static ResourceTreeConfiguration fromVanilla(TreeConfiguration treeConfig) {
        return new ResourceTreeConfiguration(
                treeConfig.trunkPlacer,
                treeConfig.foliagePlacer,
                treeConfig.rootPlacer,
                treeConfig.minimumSize,
                treeConfig.decorators,
                treeConfig.ignoreVines,
                treeConfig.belowTrunkProvider);
    }

    /**
     * 用指定原木/树叶 provider 构建可放置的 {@link TreeConfiguration}。
     * 不修改本实例，可安全缓存后多次用于不同资源树。
     */
    public TreeConfiguration build(BlockStateProvider trunkProvider, BlockStateProvider foliageProvider) {
        TreeConfiguration.TreeConfigurationBuilder builder = new TreeConfiguration.TreeConfigurationBuilder(
                trunkProvider,
                trunkPlacer,
                foliageProvider,
                foliagePlacer,
                rootPlacer,
                minimumSize,
                belowTrunkProvider);
        builder.decorators(decorators);
        if (ignoreVines) {
            builder.ignoreVines();
        }
        return builder.build();
    }
}
