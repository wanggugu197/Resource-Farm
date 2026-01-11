package com.resource_farm.data.tree;

import com.resource_farm.common.block.ResourceTree.*;
import com.resource_farm.common.item.ResourceTree.ResourceFruitItem;
import com.resource_farm.common.item.ResourceTree.ResourceResinItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.util.Lazy;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ResourceTree {

    @Setter
    private ResourceTreeConfig resourceTreeConfig;

    @Setter
    private Lazy<Item> treeItem;
    @Setter
    private Lazy<String> translateKey;

    @Setter
    private ResourceLocation treeConfigKey;
    @Setter
    private BlockEntry<ResourceSaplingBlock> sapling;
    @Setter
    private BlockEntry<ResourceLeavesBlock> leaves;
    @Setter
    private BlockEntry<ResourceLogBlock> log;
    @Setter
    private BlockEntry<ResourceStrippedLogBlock> strippedLog;
    @Setter
    private BlockEntry<ResourceWoodBlock> wood;
    @Setter
    private BlockEntry<ResourceStrippedWoodBlock> strippedWood;
    @Setter
    private BlockEntry<ResourcePlanksBlock> planks;
    @Setter
    private ItemEntry<ResourceResinItem> resin;
    @Setter
    private ItemEntry<ResourceFruitItem> fruit;

    public ResourceTree(ResourceTreeConfig resourceTreeConfig) {
        this.resourceTreeConfig = resourceTreeConfig;
    }

    public static ResourceTree create(ResourceTreeConfig resourceTreeConfig) {
        return new ResourceTree(resourceTreeConfig);
    }
}
