package com.maple.resource_farm.ResourceTree.builder;

import com.maple.resource_farm.ResourceTree.ResourceTreeAccessManagement;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.Lazy;

import com.mapleutillib.utils.RegistriesUtils;

public class TreeInitialization {

    public static void initializationTreeMap() {
        ResourceTreeAccessManagement.ResourceTreeMap.forEach((name, resourceTree) -> {
            if (resourceTree == null) return;
            String itemId = resourceTree.getResourceTreeConfig().correspondingItem();
            String translateKey = resourceTree.getResourceTreeConfig().translateKey();

            resourceTree.setTreeItem(Lazy.of(() -> {
                if (itemId != null) {
                    return RegistriesUtils.getItem(itemId);
                }
                return Items.BARRIER;
            }));

            resourceTree.setTranslateKey(Lazy.of(() -> {
                if (translateKey == null) {
                    if (itemId != null) {
                        Item item = resourceTree.getTreeItem().get();
                        if (item != Items.BARRIER) {
                            return item.getDescriptionId();
                        }
                    }
                    return "§ktranslate";
                }
                return translateKey;
            }));
        });
    }
}
