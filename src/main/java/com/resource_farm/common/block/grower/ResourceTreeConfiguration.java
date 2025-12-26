package com.resource_farm.common.block.grower;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
public class ResourceTreeConfiguration {

    @Setter
    public BlockStateProvider trunkProvider;
    @Setter
    public BlockStateProvider dirtProvider;
    public final TrunkPlacer trunkPlacer;
    @Setter
    public BlockStateProvider foliageProvider;
    public final FoliagePlacer foliagePlacer;
    public final Optional<RootPlacer> rootPlacer;
    public final FeatureSize minimumSize;
    public final List<TreeDecorator> decorators;
    public final boolean ignoreVines;

    protected ResourceTreeConfiguration(BlockStateProvider trunkProvider,
                                        TrunkPlacer trunkPlacer,
                                        BlockStateProvider foliageProvider,
                                        FoliagePlacer foliagePlacer,
                                        Optional<RootPlacer> rootPlacer,
                                        BlockStateProvider dirtProvider,
                                        FeatureSize minimumSize,
                                        List<TreeDecorator> decorators,
                                        boolean ignoreVines) {
        this.trunkProvider = trunkProvider;
        this.trunkPlacer = trunkPlacer;
        this.foliageProvider = foliageProvider;
        this.foliagePlacer = foliagePlacer;
        this.rootPlacer = rootPlacer;
        this.dirtProvider = dirtProvider;
        this.minimumSize = minimumSize;
        this.decorators = decorators;
        this.ignoreVines = ignoreVines;
    }

    public TreeConfiguration get() {
        TreeConfiguration.TreeConfigurationBuilder treeConfigBuilder = new TreeConfiguration.TreeConfigurationBuilder(
                trunkProvider,
                trunkPlacer,
                foliageProvider,
                foliagePlacer,
                rootPlacer,
                minimumSize);
        treeConfigBuilder.dirt(dirtProvider);
        treeConfigBuilder.decorators(decorators);
        if (ignoreVines) treeConfigBuilder.ignoreVines();
        return treeConfigBuilder.build();
    }

    public static class ResourceTreeConfigurationBuilder {

        private final BlockStateProvider trunkProvider;
        private final TrunkPlacer trunkPlacer;
        private final BlockStateProvider foliageProvider;
        private final FoliagePlacer foliagePlacer;
        private final Optional<RootPlacer> rootPlacer;
        private BlockStateProvider dirtProvider;
        private final FeatureSize minimumSize;
        private List<TreeDecorator> decorators;
        private boolean ignoreVines;

        public ResourceTreeConfigurationBuilder(BlockStateProvider trunkProvider,
                                                TrunkPlacer trunkPlacer,
                                                BlockStateProvider foliageProvider,
                                                FoliagePlacer foliagePlacer,
                                                Optional<RootPlacer> rootPlacer,
                                                FeatureSize minimumSize) {
            this.decorators = ImmutableList.of();
            this.trunkProvider = trunkProvider;
            this.trunkPlacer = trunkPlacer;
            this.foliageProvider = foliageProvider;
            this.dirtProvider = BlockStateProvider.simple(Blocks.DIRT);
            this.foliagePlacer = foliagePlacer;
            this.rootPlacer = rootPlacer;
            this.minimumSize = minimumSize;
        }

        public ResourceTreeConfigurationBuilder(BlockStateProvider trunkProvider,
                                                TrunkPlacer trunkPlacer,
                                                BlockStateProvider foliageProvider,
                                                FoliagePlacer foliagePlacer,
                                                FeatureSize minimumSize) {
            this(trunkProvider, trunkPlacer, foliageProvider, foliagePlacer, Optional.empty(), minimumSize);
        }

        public ResourceTreeConfigurationBuilder dirt(BlockStateProvider dirtProvider) {
            this.dirtProvider = dirtProvider;
            return this;
        }

        public ResourceTreeConfigurationBuilder decorators(List<TreeDecorator> decorators) {
            this.decorators = decorators;
            return this;
        }

        public ResourceTreeConfigurationBuilder ignoreVines() {
            this.ignoreVines = true;
            return this;
        }

        public ResourceTreeConfiguration build() {
            return new ResourceTreeConfiguration(this.trunkProvider, this.trunkPlacer, this.foliageProvider, this.foliagePlacer,
                    this.rootPlacer, this.dirtProvider, this.minimumSize, this.decorators, this.ignoreVines);
        }
    }
}
