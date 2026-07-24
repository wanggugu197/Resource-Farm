package com.maple.resource_farm.api.ResourceOre;

import net.minecraft.resources.Identifier;

public record ResourceOreType(
                              Identifier base,
                              Identifier center,
                              Identifier secondary) {

    public static ResourceOreType of(Identifier singleResource) {
        return new ResourceOreType(singleResource, singleResource, singleResource);
    }

    public static ResourceOreType of(Identifier firstResource, Identifier secondResource) {
        return new ResourceOreType(firstResource, secondResource, firstResource);
    }
}
