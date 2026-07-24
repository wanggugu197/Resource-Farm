package com.maple.resource_farm.data.lang;

import net.minecraft.network.chat.Component;

import com.mapleutillib.api.registry.ModLangProvider;

import static com.maple.resource_farm.common.registry.ResourceFarmRegistration.REGISTRY;

public final class LangHandler {

    public static Component addLang(String key, String cn, String en) {
        if (REGISTRY.doDatagen()) {
            REGISTRY.lang(key, en);
            REGISTRY.lang(ModLangProvider.LANG_ZH_CN, key, cn);
        }
        return Component.translatable(key);
    }

    public static void init() {
        // 资源物品 名称
        addLang("block.resource_farm.tree.sapling", "%s树苗", "%s Sapling");
        addLang("block.resource_farm.tree.leaves", "%s树叶", "%s Leaves");
        addLang("block.resource_farm.tree.log", "%s原木", "%s Log");
        addLang("block.resource_farm.tree.stripped_log", "去皮%s原木", "Stripped %s Log");
        addLang("block.resource_farm.tree.wood", "%s木", "%s Wood");
        addLang("block.resource_farm.tree.stripped_wood", "去皮%s木", "Stripped %s Wood");
        addLang("block.resource_farm.tree.planks", "%s木板", "%s Planks");
        addLang("block.resource_farm.tree.propagule", "%s胎生苗", "%s Propagule");

        addLang("item.resource_farm.tree.resin", "%s树脂", "%s Resin");
        addLang("item.resource_farm.tree.fruit", "%s果实", "%s Fruit");
        addLang("item.resource_farm.tree.clump", "%s碎块", "%s Clump");

        // 描述
        addLang("tooltip.resource_farm.sapling.placed_on_block", "§7可放于§e⌈%s§e⌋§7方块§r", "§7Placeable on §e⌈%s§e⌋§7 block§r");
        addLang("tooltip.resource_farm.sapling.placed_on_block_tag", "§7可放于§e⌈%s§e⌋§7标签下所有方块§r", "§7Placeable on all blocks in §e⌈%s§e⌋§7 block tag§r");
        addLang("tooltip.resource_farm.sapling.fertilize_1", "§7可使用§a⌈%s§a⌋§7进行催熟§r", "§7Fertilizable with §a⌈%s§a⌋§7§r");
        addLang("tooltip.resource_farm.sapling.fertilize_2", "§7可使用§a⌈%s§a/%s§a⌋§7进行催熟§r", "§7Fertilizable with §a⌈%s§a/%s§a⌋§7§r");

        // 方块 Tag
        addLang("tag.block.resource_farm.resource_sapling", "资源树苗", "Resource Sapling");
        addLang("tag.block.resource_farm.resource_leaves", "资源树叶", "Resource Leaves");
        addLang("tag.block.resource_farm.resource_log", "资源原木", "Resource Log");
        addLang("tag.block.resource_farm.resource_planks", "资源木板", "Resource Planks");

        // 物品 Tag
        addLang("tag.item.resource_farm.resource_sapling", "资源树苗", "Resource Sapling");
        addLang("tag.item.resource_farm.resource_leaves", "资源树叶", "Resource Leaves");
        addLang("tag.item.resource_farm.resource_log", "资源原木", "Resource Log");
        addLang("tag.item.resource_farm.resource_planks", "资源木板", "Resource Planks");
        addLang("tag.item.resource_farm.resource_resin", "资源树脂", "Resource Resin");
        addLang("tag.item.resource_farm.resource_fruit", "资源果实", "Resource Fruit");
        addLang("tag.item.resource_farm.resource_clump", "资源碎块", "Resource Clump");
    }
}
