package com.maple.resource_farm.plantPot.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.gto.registrylib.util.entry.ItemEntry;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class ClocheHelper {

    private static boolean init = false;

    public static final Int2ObjectOpenHashMap<ItemEntry<Item>> ClocheItemMap = new Int2ObjectOpenHashMap<>();
    public static final Object2IntOpenHashMap<Item> ClocheTireMap = new Object2IntOpenHashMap<>();
    public static final Int2FloatMap speedModifierMap = new Int2FloatOpenHashMap();
    public static final Int2FloatMap yieldModifierMap = new Int2FloatOpenHashMap();

    public static void addCloche(ItemEntry<Item> item, int tire, float speedModifier, float yieldModifier) {
        ClocheItemMap.put(tire, item);
        speedModifierMap.put(tire, speedModifier);
        yieldModifierMap.put(tire, yieldModifier);
    }

    public static ItemStack getClocheItem(int tire) {
        if (ClocheItemMap.containsKey(tire)) return new ItemStack(ClocheItemMap.get(tire).asItem());
        return ItemStack.EMPTY;
    }

    public static int getClocheTire(Item item) {
        if (!init) {
            ClocheItemMap.forEach((tire, itemEntry) -> ClocheTireMap.addTo(itemEntry.get(), tire));
            init = true;
        }
        return ClocheTireMap.getOrDefault(item, 0);
    }
}
