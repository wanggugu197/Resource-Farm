package com.resource_farm;

import com.resource_farm.common.CommonInit;
import com.resource_farm.config.ConfigHolder;
import com.resource_farm.utils.FormattingUtil;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ResourceFarm.MOD_ID)
public class ResourceFarm {

    public static final String MOD_ID = "resource_farm";
    private static final ResourceLocation TEMPLATE_LOCATION = ResourceLocation.fromNamespaceAndPath(MOD_ID, "");
    public static final Codec<ResourceLocation> ResourceFarm_ID = Codec.STRING.comapFlatMap(
            str -> ResourceLocation.read(appendIdString(str)),
            s -> s.getNamespace().equals(MOD_ID) ? s.getPath() : s.toString());

    public static final String NAME = "Resource Farm";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public ResourceFarm(IEventBus modBus, FMLModContainer container) {
        ConfigHolder.init();

        CommonInit.init(modBus);
    }

    public static boolean isDataGen() {
        return DatagenModLoader.isRunningDataGen();
    }

    public static ResourceLocation id(String path) {
        if (path.isBlank()) {
            return TEMPLATE_LOCATION;
        }

        int i = path.indexOf(':');
        if (i > 0) {
            return ResourceLocation.tryParse(path);
        } else if (i == 0) {
            path = path.substring(i + 1);
        }
        if (FormattingUtil.hasUpperCase(path)) {
            path = FormattingUtil.toLowerCaseUnderscore(path);
        }
        return TEMPLATE_LOCATION.withPath(path);
    }

    public static String appendIdString(String id) {
        int i = id.indexOf(':');
        if (i > 0) {
            return id;
        } else if (i == 0) {
            return MOD_ID + id;
        } else {
            return MOD_ID + ":" + id;
        }
    }
}
