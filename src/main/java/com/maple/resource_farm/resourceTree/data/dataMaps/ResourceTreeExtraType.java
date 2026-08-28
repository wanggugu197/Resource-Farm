package com.maple.resource_farm.resourceTree.data.dataMaps;

import com.maple.resource_farm.ResourceFarm;

import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** 矿石/叠加纹理样式（数据包 JSON）。 */
public record ResourceTreeExtraType(
                                    ResourceLocation base,
                                    ResourceLocation center) {

    public static final Codec<ResourceTreeExtraType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("base", ResourceFarm.id("block/ore/iron"))
                    .forGetter(ResourceTreeExtraType::base),
            ResourceLocation.CODEC.optionalFieldOf("center", ResourceFarm.id("block/ore/center"))
                    .forGetter(ResourceTreeExtraType::center))
            .apply(instance, ResourceTreeExtraType::new));

    public static final ResourceTreeExtraType DEFAULT = new ResourceTreeExtraType(
            ResourceFarm.id("block/ore/iron"),
            ResourceFarm.id("block/ore/center"));
}
