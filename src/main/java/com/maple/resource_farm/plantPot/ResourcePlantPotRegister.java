package com.maple.resource_farm.plantPot;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.common.conditions.ConfigCondition;
import com.maple.resource_farm.config.ResourceFarmConfigHolder;
import com.maple.resource_farm.plantPot.block.*;
import com.maple.resource_farm.plantPot.datamap.FertilizerData;
import com.maple.resource_farm.plantPot.datamap.SoilModifierData;
import com.maple.resource_farm.plantPot.recipe.GrowthRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import com.gto.registrylib.tooltip.SubNode;
import com.gto.registrylib.util.DistExecutor;
import com.gto.registrylib.util.entry.BlockEntityTypeEntry;
import com.gto.registrylib.util.entry.BlockEntry;
import com.gto.registrylib.util.entry.ItemEntry;
import com.gto.registrylib.util.entry.RecipeTypeEntry;
import com.mapleutillib.client.ClientInit;
import com.mapleutillib.utils.recipe.VanillaRecipeHelper;

import static com.maple.resource_farm.ResourceFarm.REGISTRY;
import static com.maple.resource_farm.data.misc.ResourceFarmCreativeModeTabs.PLANT_POT_TAB;

public final class ResourcePlantPotRegister {

    // ========== 私有构造 ==========
    private ResourcePlantPotRegister() {}

    // ============================================================
    // 方块
    // ============================================================
    public static final BlockEntry<BonsaiPotBlock> BONSAI_POT = REGISTRY
            .block("bonsai_pot", BonsaiPotBlock::new)
            .langCn("盆栽盆")
            .blockstate(() -> (block, prov) -> prov.create(block, ResourceFarm.id("block/bonsai_pot")))
            .initialProperties(Blocks.TERRACOTTA)
            .addTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item(builder -> builder.addTab(PLANT_POT_TAB.getKey()))
            .register();

    public static final BlockEntry<HoppingBonsaiPotBlock> HOPPING_BONSAI_POT = REGISTRY
            .block("hopping_bonsai_pot", HoppingBonsaiPotBlock::new)
            .langCn("漏式盆栽盆")
            .blockstate(() -> (block, prov) -> prov.create(block, ResourceFarm.id("block/hopping_bonsai_pot")))
            .initialProperties(Blocks.IRON_BLOCK)
            .addTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item(builder -> builder.addTab(PLANT_POT_TAB.getKey()))
            .register();

    // ============================================================
    // 方块实体
    // ============================================================
    public static final BlockEntityTypeEntry<BonsaiPotBlockEntity> BONSAI_POT_BLOCK_ENTITY = REGISTRY
            .blockEntity(REGISTRY, "bonsai_pot_entity", BonsaiPotBlockEntity::new)
            .validBlock(BONSAI_POT)
            .renderer(() -> () -> BonsaiPotBlockEntityRenderer::new)
            .register();

    public static final BlockEntityTypeEntry<HoppingBonsaiPotBlockEntity> HOPPING_BONSAI_POT_BLOCK_ENTITY = REGISTRY
            .blockEntity(REGISTRY, "hopping_bonsai_pot_entity", HoppingBonsaiPotBlockEntity::new)
            .validBlock(HOPPING_BONSAI_POT)
            .renderer(() -> () -> HoppingBonsaiPotBlockEntityRenderer::new)
            .register();

    // ============================================================
    // 玻璃罩
    // ============================================================
    public static final ItemEntry<Item> GLASS_CLOCHE = registerCloche(
            "glass_cloche", "玻璃罩", 1,
            ResourceFarmConfigHolder.bonsaiPotConfigHolder.plantPot.glassClocheSpeed,
            ResourceFarmConfigHolder.bonsaiPotConfigHolder.plantPot.glassClocheYield,
            0xFFFFFFFF);

    public static final ItemEntry<Item> ADVANCED_CLOCHE = registerCloche(
            "advanced_cloche", "高级玻璃罩", 2,
            ResourceFarmConfigHolder.bonsaiPotConfigHolder.plantPot.advancedClocheSpeed,
            ResourceFarmConfigHolder.bonsaiPotConfigHolder.plantPot.advancedClocheYield,
            0xFFe5634c);

    public static final ItemEntry<Item> ELITE_CLOCHE = registerCloche(
            "elite_cloche", "精英玻璃罩", 3,
            ResourceFarmConfigHolder.bonsaiPotConfigHolder.plantPot.eliteClocheSpeed,
            ResourceFarmConfigHolder.bonsaiPotConfigHolder.plantPot.eliteClocheYield,
            0xFF74fcf6);

    public static final ItemEntry<Item> ULTIMATE_CLOCHE = registerCloche(
            "ultimate_cloche", "终极玻璃罩", 4,
            ResourceFarmConfigHolder.bonsaiPotConfigHolder.plantPot.ultimateClocheSpeed,
            ResourceFarmConfigHolder.bonsaiPotConfigHolder.plantPot.ultimateClocheYield,
            0xFF8B00FF);

    // ============================================================
    // 数据映射
    // ============================================================
    public static final DataMapType<Item, FertilizerData> FERTILIZERS = DataMapType
            .builder(ResourceFarm.id("fertilizers"), Registries.ITEM, FertilizerData.CODEC)
            .synced(FertilizerData.CODEC, false)
            .build();

    public static final DataMapType<Item, SoilModifierData> SOIL_MODIFIERS = DataMapType
            .builder(ResourceFarm.id("soil_modifiers"), Registries.ITEM, SoilModifierData.CODEC)
            .synced(SoilModifierData.CODEC, false)
            .build();

    // ============================================================
    // 配方类型
    // ============================================================
    public static final RecipeTypeEntry<GrowthRecipe> GROWTH = REGISTRY
            .<GrowthRecipe>recipeType("growth")
            .serializer(GrowthRecipe.CODEC, GrowthRecipe.STREAM_CODEC)
            .register();

    // ============================================================
    // 注册入口
    // ============================================================
    public static void register(IEventBus modBus) {
        modBus.addListener(ResourcePlantPotRegister::registerCapabilities);
        modBus.addListener(ResourcePlantPotRegister::registerDataMaps);
        registerRecipes();
    }

    // ============================================================
    // 能力注册
    // ============================================================
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, HOPPING_BONSAI_POT_BLOCK_ENTITY.get(), (be, ignoredSide) -> be.getItemCapability());
    }

    // ============================================================
    // 数据映射注册
    // ============================================================
    private static void registerDataMaps(RegisterDataMapTypesEvent event) {
        event.register(FERTILIZERS);
        event.register(SOIL_MODIFIERS);
    }

    // ============================================================
    // 配方注册
    // ============================================================
    private static void registerRecipes() {
        REGISTRY.addRecipeData(prov -> {
            VanillaRecipeHelper.shaped(prov.withConditions(new ConfigCondition("dev.enableBonsaiPot")),
                    ResourceFarm.id(BONSAI_POT.getRegisteredName()))
                    .output(BONSAI_POT)
                    .pattern("   ", "A A", "AAA")
                    .define('A', Items.BRICK)
                    .save();
            VanillaRecipeHelper.shaped(prov.withConditions(new ConfigCondition("dev.enableBonsaiPot")),
                    ResourceFarm.id(HOPPING_BONSAI_POT.getRegisteredName()))
                    .output(HOPPING_BONSAI_POT)
                    .pattern("   ", "ABA", "DCD")
                    .define('A', Items.IRON_INGOT)
                    .define('B', BONSAI_POT)
                    .define('C', Items.HOPPER)
                    .define('D', Items.BARREL)
                    .save();

            VanillaRecipeHelper.shaped(prov.withConditions(new ConfigCondition("dev.enableBonsaiPot")),
                    ResourceFarm.id(GLASS_CLOCHE.getRegisteredName()))
                    .output(GLASS_CLOCHE)
                    .pattern("AAA", "A A", "ABA")
                    .define('A', "#c:glass_panes")
                    .define('B', "#wooden_buttons")
                    .save();
            VanillaRecipeHelper.shaped(prov.withConditions(new ConfigCondition("dev.enableBonsaiPot")),
                    ResourceFarm.id(ADVANCED_CLOCHE.getRegisteredName()))
                    .output(ADVANCED_CLOCHE)
                    .pattern("BBB", "BAB", "BBB")
                    .define('A', GLASS_CLOCHE)
                    .define('B', Items.COPPER_BLOCK)
                    .save();
            VanillaRecipeHelper.shaped(prov.withConditions(new ConfigCondition("dev.enableBonsaiPot")),
                    ResourceFarm.id(ELITE_CLOCHE.getRegisteredName()))
                    .output(ELITE_CLOCHE)
                    .pattern("BBB", "CAC", "BBB")
                    .define('A', ADVANCED_CLOCHE)
                    .define('B', Items.DIAMOND_BLOCK)
                    .define('C', Items.ENDER_EYE)
                    .save();
            VanillaRecipeHelper.shaped(prov.withConditions(new ConfigCondition("dev.enableBonsaiPot")),
                    ResourceFarm.id(ULTIMATE_CLOCHE.getRegisteredName()))
                    .output(ULTIMATE_CLOCHE)
                    .pattern("BCB", "EAE", "BDB")
                    .define('A', ELITE_CLOCHE)
                    .define('B', Items.ECHO_SHARD)
                    .define('C', Items.RESPAWN_ANCHOR)
                    .define('D', Items.NETHERITE_BLOCK)
                    .define('E', Items.SHULKER_BOX)
                    .save();
        });
    }

    // ============================================================
    // 玻璃罩注册辅助
    // ============================================================
    public static ItemEntry<Item> registerCloche(
                                                 String id,
                                                 String langName,
                                                 int tier,
                                                 float speedModifier,
                                                 float yieldModifier,
                                                 int color) {
        Style style = Style.EMPTY.withColor(color);
        var entry = REGISTRY.item(id)
                .langCn(langName)
                // 1.21.1：模型为普通 parent JSON（委托到方块模型），着色由运行时 ItemColor 驱动
                .model(() -> (item, prov) -> prov.createWithExistingModel(item, ResourceFarm.id("block/cloche")))
                .addTab(PLANT_POT_TAB.getKey())
                .addTooltip((collector, ignored) -> {
                    collector.node(new SubNode.Basic(
                            Component.translatable("tooltip.resource_farm.cloche_item_1", tier)
                                    .withStyle(style)));
                    collector.node(new SubNode.Basic(
                            Component.translatable("tooltip.resource_farm.cloche_item_2",
                                    String.format("%.2f", speedModifier),
                                    String.format("%.2f", yieldModifier))
                                    .withStyle(style)));
                })
                .register();
        // 1.21.1：registrylib 的条目在 RegisterEvent 时才绑定，此处条目尚未绑定，
        // 因此把运行时 tint 注册推迟到注册完成后（MapleUtilLib ClientInit 统一注册）。
        // 仅客户端执行：服务端加载 ClientInit/ItemColor 会触发 RuntimeDistCleaner 崩溃。
        REGISTRY.addRegisterCallback(Registries.ITEM,
                () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> ClientInit.registerItemTint(entry.get(), (stack, tintIndex) -> color)));
        // 注册到 ClocheHelper
        ClocheHelper.addCloche(entry, tier, speedModifier, yieldModifier);
        return entry;
    }
}
