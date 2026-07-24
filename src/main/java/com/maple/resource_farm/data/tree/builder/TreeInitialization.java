package com.maple.resource_farm.data.tree.builder;

import com.maple.resource_farm.data.ResourceFarmBlocks;
import com.maple.resource_farm.utils.RegistriesUtils;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.Lazy;

public class TreeInitialization {

    public static void initializationTreeMap() {
        ResourceFarmBlocks.ResourceTreeMap.forEach((name, resourceTree) -> {
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
                        return "§ktranslate";
                    }
                }
                return translateKey;
            }));
        });
    }
}
