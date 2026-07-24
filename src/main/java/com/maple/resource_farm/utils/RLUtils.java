package com.maple.resource_farm.utils;

import net.minecraft.resources.Identifier;

public class RLUtils {

    private RLUtils() {}

    public static Identifier get(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Identifier parse(String location) {
        String namespace = "minecraft";
        String path = location;
        int i = location.indexOf(':');
        if (i >= 0) {
            path = location.substring(i + 1);
            if (i >= 1) namespace = location.substring(0, i);
        }
        return RLUtils.get(namespace, path);
    }

    public static Identifier mc(String path) {
        return RLUtils.get("minecraft", path);
    }

    public static Identifier nf(String path) {
        return RLUtils.get("neoforge", path);
    }

    public static Identifier cr(String path) {
        return RLUtils.get("create", path);
    }

    public static Identifier mek(String path) {
        return RLUtils.get("mekanism", path);
    }
}
