package com.resource_farm.utils;

import net.minecraft.resources.ResourceLocation;

public class RLUtils {

    private RLUtils() {}

    public static ResourceLocation get(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation parse(String location) {
        String namespace = "minecraft";
        String path = location;
        int i = location.indexOf(':');
        if (i >= 0) {
            path = location.substring(i + 1);
            if (i >= 1) namespace = location.substring(0, i);
        }
        return RLUtils.get(namespace, path);
    }

    public static ResourceLocation mc(String path) {
        return RLUtils.get("minecraft", path);
    }

    public static ResourceLocation nf(String path) {
        return RLUtils.get("neoforge", path);
    }

    public static ResourceLocation cr(String path) {
        return RLUtils.get("create", path);
    }

    public static ResourceLocation mek(String path) {
        return RLUtils.get("mekanism", path);
    }
}
