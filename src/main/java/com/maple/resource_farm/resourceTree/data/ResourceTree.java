package com.maple.resource_farm.resourceTree.data;

import com.maple.resource_farm.resourceTree.data.dataMaps.ResourceTreeConfig;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.util.Lazy;

import com.gto.registrylib.util.entry.BlockEntry;
import com.gto.registrylib.util.entry.ItemEntry;
import lombok.Getter;
import lombok.Setter;

/**
 * 运行时资源树实例（方块/物品句柄）。
 * 生长器不保存在此处：键见 {@link ResourceTreeConfig#growerId()}，
 * 实例见 {@link com.maple.resource_farm.resourceTree.data.dataMaps.ResourceFarmMaps#GROWERS}。
 */
@Getter
public class ResourceTree {

    @Setter
    private ResourceTreeConfig resourceTreeConfig;

    @Setter
    private Lazy<Item> treeItem;
    @Setter
    private Lazy<String> translateKey;

    @Setter
    private Identifier treeConfigKey;
    @Setter
    private BlockEntry<? extends Block> sapling;
    @Setter
    private BlockEntry<? extends Block> leaves;
    @Setter
    private BlockEntry<? extends Block> log;
    @Setter
    private BlockEntry<? extends Block> strippedLog;
    @Setter
    private BlockEntry<? extends Block> wood;
    @Setter
    private BlockEntry<? extends Block> strippedWood;
    @Setter
    private BlockEntry<? extends Block> planks;
    @Setter
    private ItemEntry<? extends Item> resin;
    @Setter
    private ItemEntry<? extends Item> fruit;
    @Setter
    private ItemEntry<? extends Item> clump;

    public ResourceTree(ResourceTreeConfig resourceTreeConfig) {
        this.resourceTreeConfig = resourceTreeConfig;
    }

    public static ResourceTree create(ResourceTreeConfig resourceTreeConfig) {
        return new ResourceTree(resourceTreeConfig);
    }
}
