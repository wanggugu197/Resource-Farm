package com.maple.resource_farm;

import com.maple.resource_farm.common.CommonInit;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

import com.mapleutillib.api.registry.ModRegistryCore;
import com.mapleutillib.utils.FormattingUtil;
import com.mapleutillib.utils.RLUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@Mod(ResourceFarm.MOD_ID)
public class ResourceFarm {

    public static final String MOD_ID = "resource_farm";
    private static final Identifier TEMPLATE_LOCATION = RLUtils.get(MOD_ID, "");
    public static final ModRegistryCore REGISTRY = ModRegistryCore.create(MOD_ID);

    public static final String NAME = "Resource Farm";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @ApiStatus.Internal
    public static IEventBus rfModBus;

    public ResourceFarm(IEventBus modBus, FMLModContainer container) {
        ResourceFarm.rfModBus = modBus;
        ResourceFarmConfigHolder.init();
        CommonInit.init(modBus);
    }

    public static boolean isDataGen() {
        return DatagenModLoader.isRunningDataGen();
    }

    public static Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static Identifier id(String path) {
        if (path.isBlank()) {
            return TEMPLATE_LOCATION;
        }

        int i = path.indexOf(':');
        if (i > 0) {
            return Identifier.tryParse(path);
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
