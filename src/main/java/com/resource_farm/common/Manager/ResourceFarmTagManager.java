package com.resource_farm.common.Manager;

import com.resource_farm.ResourceFarm;
import com.resource_farm.data.tree.builder.TreeTagInsert;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import com.google.common.collect.Lists;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.List;
import java.util.Map;

public class ResourceFarmTagManager {

    public static <T> void generateResourceFarmDynamicTags(Map<ResourceLocation, List<TagLoader.EntryWithSource>> tagMap, Registry<T> registry) {
        // 方块注册表分支
        if (registry == BuiltInRegistries.BLOCK) {
            // 临时分类Map
            Object2ObjectOpenHashMap<ResourceLocation, List<TagLoader.EntryWithSource>> tempBlockTagMap = new Object2ObjectOpenHashMap<>();

            // 资源树 Tag
            TreeTagInsert.generateBlockTags(tempBlockTagMap);

            // 批量追加到最终tagMap
            for (Map.Entry<ResourceLocation, List<TagLoader.EntryWithSource>> entry : tempBlockTagMap.entrySet()) {
                tagMap.computeIfAbsent(entry.getKey(), k -> Lists.newArrayList()).addAll(entry.getValue());
            }
        }

        // 物品注册表分支
        else if (registry == BuiltInRegistries.ITEM) {
            // 临时分类Map
            Object2ObjectOpenHashMap<ResourceLocation, List<TagLoader.EntryWithSource>> tempItemTagMap = new Object2ObjectOpenHashMap<>();

            // 资源树 Tag
            TreeTagInsert.generateItemTags(tempItemTagMap);

            // 批量追加到最终tagMap
            for (Map.Entry<ResourceLocation, List<TagLoader.EntryWithSource>> entry : tempItemTagMap.entrySet()) {
                tagMap.computeIfAbsent(entry.getKey(), k -> Lists.newArrayList()).addAll(entry.getValue());
            }
        }

        // 流体注册表分支
        // else if (registry == BuiltInRegistries.FLUID) {}
    }

    // ====================== 工具方法 ======================
    public static <B extends Block> TagLoader.EntryWithSource makeBlockTagEntry(BlockEntry<B> blockEntry) {
        return new TagLoader.EntryWithSource(TagEntry.element(blockEntry.getId()), ResourceFarm.MOD_ID);
    }

    public static <B extends Block> TagLoader.EntryWithSource makeItemTagEntry(BlockEntry<B> blockEntry) {
        return new TagLoader.EntryWithSource(TagEntry.element(blockEntry.getId()), ResourceFarm.MOD_ID);
    }

    public static <B extends Item> TagLoader.EntryWithSource makeItemTagEntry(ItemEntry<B> itemEntry) {
        return new TagLoader.EntryWithSource(TagEntry.element(itemEntry.getId()), ResourceFarm.MOD_ID);
    }
}
