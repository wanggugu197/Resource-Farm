package com.resource_farm.api.addon;

import com.resource_farm.ResourceFarm;

import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.util.*;

public final class AddonFinder {

    private static final List<IAddon> cache = new ArrayList<>();
    private static Map<String, IAddon> modIdMap = null;

    @UnmodifiableView
    public static List<IAddon> getAddonList() {
        ensureInitialized();
        return Collections.unmodifiableList(cache);
    }

    @UnmodifiableView
    public static Map<String, IAddon> getAddons() {
        ensureInitialized();
        return Collections.unmodifiableMap(modIdMap);
    }

    @Nullable
    public static IAddon getAddon(String modId) {
        return modIdMap.get(modId);
    }

    private static void ensureInitialized() {
        if (modIdMap == null) {
            modIdMap = getInstances();
            cache.addAll(modIdMap.values());
        }
    }

    private static Map<String, IAddon> getInstances() {
        List<IModInfo> allMods = ModList.get().getMods();
        Map<String, String> addonClassNames = new LinkedHashMap<>();
        for (IModInfo modInfo : allMods) {
            ModFileScanData scanData = modInfo.getOwningFile().getFile().getScanResult();
            scanData.getAnnotatedBy(Addon.class, ElementType.TYPE)
                    .filter(data -> data.annotationData().get("value").equals(modInfo.getModId()))
                    .map(ModFileScanData.AnnotationData::memberName)
                    .forEach(className -> addonClassNames.put(modInfo.getModId(), className));
        }
        Map<String, IAddon> instances = new LinkedHashMap<>();
        for (var entry : addonClassNames.entrySet()) {
            String modId = entry.getKey();
            String className = entry.getValue();
            try {
                Class<?> asmClass = Class.forName(className);
                Class<? extends IAddon> asmInstanceClass = asmClass.asSubclass(IAddon.class);
                try {
                    Constructor<? extends IAddon> constructor = asmInstanceClass.getDeclaredConstructor();
                    IAddon instance = constructor.newInstance();
                    instances.put(modId, instance);
                } catch (ReflectiveOperationException e) {
                    ResourceFarm.LOGGER.error("Resource Farm addon class {} for addon {} must have a public constructor with no arguments, found {}",
                            className, modId, Arrays.toString(asmInstanceClass.getConstructors()));
                }
            } catch (ClassCastException e) {
                ResourceFarm.LOGGER.error("Failed to load: {} from {}, does not extend IAddon!", className, modId, e);
            } catch (ClassNotFoundException | LinkageError e) {
                ResourceFarm.LOGGER.error("Failed to load: {} from {}", className, modId, e);
            }
        }
        return instances;
    }
}
