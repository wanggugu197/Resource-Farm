package com.resource_farm.api.ResourceOre;

import net.minecraft.resources.ResourceLocation;

public record ResourceOreType(
                              ResourceLocation base,
                              ResourceLocation center,
                              ResourceLocation secondary) {

    public static ResourceOreType of(ResourceLocation singleResource) {
        return new ResourceOreType(singleResource, singleResource, singleResource);
    }

    public static ResourceOreType of(ResourceLocation firstResource, ResourceLocation secondResource) {
        return new ResourceOreType(firstResource, secondResource, firstResource);
    }
}
