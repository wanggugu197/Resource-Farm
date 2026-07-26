package com.maple.resource_farm.data.lang;

import net.minecraft.network.chat.Component;

import com.mapleutillib.api.registry.ModLangProvider;

import static com.maple.resource_farm.ResourceFarm.REGISTRY;

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

        // ==================== JEI 翻译 ====================
        addLang("jei.resource_farm.growth_recipe", "盆栽盆配方", "Bonsai Pot Recipe");
        addLang("jei.resource_farm.recipe.drop.count", "数量: %s", "Count: %s");
        addLang("jei.resource_farm.recipe.drop.count_between", "数量: %s - %s", "Count: %s - %s");
        addLang("jei.resource_farm.recipe.drop.chance", "概率: %s", "Chance: %s");
        addLang("jei.resource_farm.recipe.seed.during", "耗时: %s 秒", "During: %s s");
        addLang("jei.resource_farm.recipe.seed.fertilizable", "§c§l无法手动施肥加速", "§c§lCannot manually apply fertilizer to speed up the process");

        // ==================== Jade 翻译 ====================
        addLang("config.jade.plugin_resource_farm.bonsai_pot_info", "盆栽盆信息", "Bonsai Pot Info");
        addLang("config.jade.plugin_resource_farm.hopping_bonsai_pot_info", "漏式盆栽盆信息", "Hopping Bonsai Pot Info");
        // --- Bonsai Pot ---
        addLang("jade.resource_farm.sky_boost", "§e露天加速 §r§7: §a%s§r§7", "§eSky boost §r§7: §a%s§r§7");
        addLang("jade.resource_farm.no_crop", "§7未种植作物", "§7No crop planted");
        // --- Hopping Bonsai Pot ---
        addLang("jade.resource_farm.crop_ready", "§a§l%s §r§a已成熟，可以收获！", "§a§l%s §r§ais ready to harvest!");
        addLang("jade.resource_farm.crop_progress", "§2%s §r§7: 阶段 §e%d§7/§e%d §r§7(§b%d%%§7)", "§2%s §r§7: Stage §e%d§7/§e%d §r§7(§b%d%%§7)");
        addLang("jade.resource_farm.soil_info", "§7土壤: §f%s §r§7(生长倍率: §e%s§r§7)", "§7Soil: §f%s §r§7(Growth: §e%s§r§7)");
        addLang("jade.resource_farm.fertilizer_info", "§6肥料: §f%s §r§7(速度: §b%s§r§7, 产量: §b%s§r§7)", "§6Fertilizer: §f%s §r§7(Speed: §b%s§r§7, Yield: §b%s§r§7)");
        addLang("jade.resource_farm.cloche_installed", "§b%s §r§7: 速度 §b×%s§r§7, 产量 §b×%s§r§7", "§b%s §r§7: Speed §b×%s§r§7, Yield §b×%s§r§7");

        addLang("tooltip.resource_farm.cloche_item_1", "§7玻璃罩等级§r §b%s§r", "§7Glass Cloche Tire§r §b%s§r");
        addLang("tooltip.resource_farm.cloche_item_2", "§7速度§r §b×%s§r  §7产量§r §b×%s§r", "§7Speed§r §b×%s§r  §7Yield§r §b×%s§r");

        // ==================== 消息翻译 ====================
        addLang("message.resource_farm.invalid_seed_soil_combination", "§6该种子与当前土壤不兼容", "§6This seed is not compatible with the current soil");
    }
}
