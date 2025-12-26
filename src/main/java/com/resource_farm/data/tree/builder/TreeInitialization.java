package com.resource_farm.data.tree.builder;

import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.utils.RegistriesUtils;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class TreeInitialization {

    public static void initializationTreeMap() {
        ResourceFarmBlocks.ResourceTreeMap.forEach((name, resourceTree) -> {
            if (resourceTree == null) return;
            String itemId = resourceTree.getResourceTreeConfig().correspondingItem();
            String translateKey = resourceTree.getResourceTreeConfig().translateKey();

            if (itemId != null) {
                Item item = RegistriesUtils.getItem(itemId);
                if (item != Items.BARRIER) {
                    resourceTree.setTreeItem(item);
                    if (translateKey == null) {
                        resourceTree.setTranslateKey(item.getDescriptionId());
                        return;
                    }
                }
            }
            if (translateKey != null) {
                resourceTree.setTranslateKey(translateKey);
                return;
            }
            resourceTree.setTranslateKey("§ktranslate");
        });
    }
}
