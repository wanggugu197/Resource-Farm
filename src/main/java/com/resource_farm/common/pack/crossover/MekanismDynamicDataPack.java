package com.resource_farm.common.pack.crossover;

import com.resource_farm.ResourceFarm;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import mekanism.api.recipes.basic.*;
import mekanism.common.recipe.impl.NutritionalLiquifierIRecipe;

import java.nio.file.Path;

import static com.resource_farm.common.pack.ResourceFarmDynamicDataPack.*;

public class MekanismDynamicDataPack {

    private static final String MEKANISM_RECIPE_TYPE_PREFIX = "mekanism:";

    /** 统一 Mekanism 配方注册入口 */
    public static void addMekanismRecipe(ResourceLocation recipeId, Object recipeObj, HolderLookup.Provider provider) {
        if (recipeObj == null) {
            ResourceFarm.LOGGER.error("Failed to register Mekanism recipe: {} - recipe object is null", recipeId);
            return;
        }
        switch (recipeObj) {
            case BasicChemicalCrystallizerRecipe ccr -> addCrystallizerRecipe(recipeId, ccr, provider); // 化学结晶器
            case BasicChemicalDissolutionRecipe cdr -> addDissolutionRecipe(recipeId, cdr, provider); // 化学溶解室
            case BasicChemicalInfuserRecipe cir -> addChemicalInfuserRecipe(recipeId, cir, provider); // 化学灌注器
            case BasicWashingRecipe wr -> addWashingRecipe(recipeId, wr, provider); // 化学清洗机
            case BasicElectrolysisRecipe er -> addElectrolysisRecipe(recipeId, er, provider); // 电解分离器
            case BasicMetallurgicInfuserRecipe mir -> addMetallurgicInfuserRecipe(recipeId, mir, provider); // 冶金灌注机
            case BasicPressurizedReactionRecipe prr -> addReactionRecipe(recipeId, prr, provider); // 加压反应室
            case BasicPigmentExtractingRecipe per -> addPigmentExtractingRecipe(recipeId, per, provider); // 颜料提取机
            case BasicPigmentMixingRecipe pmr -> addPigmentMixingRecipe(recipeId, pmr, provider); // 颜料混合机
            case BasicPaintingRecipe pr -> addPaintingRecipe(recipeId, pr, provider); // 上色机
            case BasicRotaryRecipe rr -> addRotaryRecipe(recipeId, rr, provider); // 回旋式气液转化机 蒸发 冷凝
            case BasicChemicalOxidizerRecipe cor -> addOxidizerRecipe(recipeId, cor, provider); // 化学氧化机
            case NutritionalLiquifierIRecipe nlr -> addNutritionalLiquifierIRecipe(recipeId, nlr, provider); // 营养液化器
            case BasicActivatingRecipe ar -> addActivatingRecipe(recipeId, ar, provider); // 太阳能中子火花器
            case BasicCentrifugingRecipe cr -> addCentrifugingRecipe(recipeId, cr, provider); // 同位素离心机
            case BasicCombinerRecipe cr -> addCombinerRecipe(recipeId, cr, provider); // 融合机
            case BasicPurifyingRecipe pr -> addPurifyingRecipe(recipeId, pr, provider); // 提纯仓
            case BasicCompressingRecipe cr -> addCompressingRecipe(recipeId, cr, provider); // 锇压缩机
            case BasicInjectingRecipe ir -> addInjectingRecipe(recipeId, ir, provider); // 化学压射室
            case BasicNucleosynthesizingRecipe nr -> addNucleosynthesizingRecipe(recipeId, nr, provider); // 反质子核合成器
            // 超临界移相器 硬编码 加不了
            // 热力锅炉 通过添加化学冷却剂自动生成配方
            case BasicSawmillRecipe sr -> addSawmillRecipe(recipeId, sr, provider); // 精密锯木机
            case BasicEnrichingRecipe er -> addEnrichingRecipe(recipeId, er, provider); // 富集仓
            case BasicCrushingRecipe cr -> addCrushingRecipe(recipeId, cr, provider); // 粉碎机
            case BasicSmeltingRecipe sr -> addSmeltingRecipe(recipeId, sr, provider); // 电力熔炼炉
            case BasicFluidToFluidRecipe ffr -> addFluidToFluidRecipe(recipeId, ffr, provider); // 热力蒸馏控制器
            case BasicItemStackToEnergyRecipe ecr -> addEnergyConversionRecipe(recipeId, ecr, provider); // 物品到能量
            case BasicChemicalConversionRecipe ccr -> addChemicalConversionRecipe(recipeId, ccr, provider); // 将物品转化为化学品
            // 裂变反应堆 通过添加化学冷却剂自动生成配方

            default -> {
                ResourceFarm.LOGGER.error("Failed to register Mekanism recipe: {} - unsupported concrete recipe type: {}",
                        recipeId, recipeObj.getClass().getName());
                ResourceFarm.LOGGER.warn("Supported Mekanism recipe types: BasicCrushingRecipe, BasicEnrichingRecipe, BasicSmeltingRecipe, " +
                        "BasicChemicalInfuserRecipe, BasicElectrolysisRecipe, BasicNucleosynthesizingRecipe (and all other Mekanism core recipes)");
            }
        }
    }

    /** Mekanism 配方通用序列化流程 */
    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> void addMekanismGenericRecipe(ResourceLocation recipeId, T recipe,
                                                                       HolderLookup.Provider provider, String recipeType) {
        try {
            // 步骤1：转换配方ID为目标数据包文件路径（复用 ResourceFarm 的 RECIPE_CONVERTER）
            ResourceLocation targetRecipeLoc = RECIPE_CONVERTER.idToFile(recipeId);
            // 步骤2：获取配方对应的序列化器（从 Mekanism 配方实例中提取）
            RecipeSerializer<T> recipeSerializer = (RecipeSerializer<T>) recipe.getSerializer();
            // 步骤3：从序列化器中提取 MapCodec（支持 Mekanism 所有自定义编解码器）
            MapCodec<T> mapCodec = recipeSerializer.codec();
            // 步骤4：执行序列化，将配方实例转为 JsonElement
            JsonElement recipeJson = mapCodec.codec()
                    .encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), recipe)
                    .getOrThrow(unchecked -> new IllegalArgumentException(
                            "Failed to serialize Mekanism recipe (" + recipeType + "): " + unchecked));
            // 步骤5：补充配方 type 字段（严格对齐原版注册名，符合 Mekanism 规范）
            if (recipeJson instanceof JsonObject jsonObject) {
                jsonObject.addProperty("type", MEKANISM_RECIPE_TYPE_PREFIX + recipeType);
            }
            // 步骤6：JSON 转字节数组（复用 ResourceFarm 工具方法）
            byte[] recipeBytes = jsonToBytes(recipeJson);
            // 步骤7：本地转储（复用 ResourceFarm 配置与方法）
            Path dumpParent = ResourceFarm.getGameDir().resolve("resource_farm/dumped/data");
            if (DUMP_DATA) {
                writeJson(targetRecipeLoc, null, dumpParent, recipeBytes);
            }
            // 步骤8：添加到动态数据包缓存（核心生效逻辑）
            addToData(targetRecipeLoc, recipeBytes);
        } catch (Exception e) {
            ResourceFarm.LOGGER.error("Failed to add Mekanism recipe ({}) for {}", recipeType, recipeId, e);
        }
    }

    /** 粉碎配方（对应原版注册名：crushing） */
    public static void addCrushingRecipe(ResourceLocation recipeId, BasicCrushingRecipe crushingRecipe,
                                         HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, crushingRecipe, provider, "crushing");
    }

    /** 富集配方（对应原版注册名：enriching） */
    public static void addEnrichingRecipe(ResourceLocation recipeId, BasicEnrichingRecipe enrichingRecipe,
                                          HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, enrichingRecipe, provider, "enriching");
    }

    /** 熔炼配方（对应原版注册名：smelting） */
    public static void addSmeltingRecipe(ResourceLocation recipeId, BasicSmeltingRecipe smeltingRecipe,
                                         HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, smeltingRecipe, provider, "smelting");
    }

    /** 清洗配方（对应原版注册名：washing） */
    public static void addWashingRecipe(ResourceLocation recipeId, BasicWashingRecipe washingRecipe,
                                        HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, washingRecipe, provider, "washing");
    }

    /** 压缩配方（对应原版注册名：compressing） */
    public static void addCompressingRecipe(ResourceLocation recipeId, BasicCompressingRecipe compressingRecipe,
                                            HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, compressingRecipe, provider, "compressing");
    }

    /** 合成配方（组合器，对应原版注册名：combining） */
    public static void addCombinerRecipe(ResourceLocation recipeId, BasicCombinerRecipe combinerRecipe,
                                         HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, combinerRecipe, provider, "combining");
    }

    /** 锯木配方（锯木机，对应原版注册名：sawing） */
    public static void addSawmillRecipe(ResourceLocation recipeId, BasicSawmillRecipe sawmillRecipe,
                                        HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, sawmillRecipe, provider, "sawing");
    }

    /** 染色配方（涂装机，对应原版注册名：painting） */
    public static void addPaintingRecipe(ResourceLocation recipeId, BasicPaintingRecipe paintingRecipe,
                                         HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, paintingRecipe, provider, "painting");
    }

    /** 化学注入配方（化学注入器，对应原版注册名：chemical_infusing） */
    public static void addChemicalInfuserRecipe(ResourceLocation recipeId, BasicChemicalInfuserRecipe chemicalInfuserRecipe,
                                                HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, chemicalInfuserRecipe, provider, "chemical_infusing");
    }

    /** 化学结晶配方（化学结晶器，对应原版注册名：crystallizing） */
    public static void addCrystallizerRecipe(ResourceLocation recipeId, BasicChemicalCrystallizerRecipe crystallizerRecipe,
                                             HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, crystallizerRecipe, provider, "crystallizing");
    }

    /** 化学溶解配方（化学溶解器，对应原版注册名：dissolution） */
    public static void addDissolutionRecipe(ResourceLocation recipeId, BasicChemicalDissolutionRecipe dissolutionRecipe,
                                            HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, dissolutionRecipe, provider, "dissolution");
    }

    /** 化学氧化配方（化学氧化器，对应原版注册名：oxidizing） */
    public static void addOxidizerRecipe(ResourceLocation recipeId, BasicChemicalOxidizerRecipe oxidizerRecipe,
                                         HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, oxidizerRecipe, provider, "oxidizing");
    }

    /** 化学转换配方（化学转换器，对应原版注册名：chemical_conversion） */
    public static void addChemicalConversionRecipe(ResourceLocation recipeId, BasicChemicalConversionRecipe chemicalConversionRecipe,
                                                   HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, chemicalConversionRecipe, provider, "chemical_conversion");
    }

    /** 活化配方（活化机，对应原版注册名：activating） */
    public static void addActivatingRecipe(ResourceLocation recipeId, BasicActivatingRecipe activatingRecipe,
                                           HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, activatingRecipe, provider, "activating");
    }

    /** 离心配方（离心机，对应原版注册名：centrifuging） */
    public static void addCentrifugingRecipe(ResourceLocation recipeId, BasicCentrifugingRecipe centrifugingRecipe,
                                             HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, centrifugingRecipe, provider, "centrifuging");
    }

    /** 电解配方（分离器，对应原版注册名：separating，注意配方类与注册名的特殊映射） */
    public static void addElectrolysisRecipe(ResourceLocation recipeId, BasicElectrolysisRecipe electrolysisRecipe,
                                             HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, electrolysisRecipe, provider, "separating");
    }

    /** 注入配方（注入机，对应原版注册名：injecting） */
    public static void addInjectingRecipe(ResourceLocation recipeId, BasicInjectingRecipe injectingRecipe,
                                          HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, injectingRecipe, provider, "injecting");
    }

    /** 提纯配方（提纯机，对应原版注册名：purifying） */
    public static void addPurifyingRecipe(ResourceLocation recipeId, BasicPurifyingRecipe purifyingRecipe,
                                          HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, purifyingRecipe, provider, "purifying");
    }

    /** 旋转加工配方（旋转加工机，对应原版注册名：rotary） */
    public static void addRotaryRecipe(ResourceLocation recipeId, BasicRotaryRecipe rotaryRecipe,
                                       HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, rotaryRecipe, provider, "rotary");
    }

    /** 加压反应配方（加压反应器，对应原版注册名：reaction） */
    public static void addReactionRecipe(ResourceLocation recipeId, BasicPressurizedReactionRecipe reactionRecipe,
                                         HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, reactionRecipe, provider, "reaction");
    }

    /** 冶金注入配方（冶金注入器，对应原版注册名：metallurgic_infusing） */
    public static void addMetallurgicInfuserRecipe(ResourceLocation recipeId, BasicMetallurgicInfuserRecipe metallurgicInfuserRecipe,
                                                   HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, metallurgicInfuserRecipe, provider, "metallurgic_infusing");
    }

    /** 流体转流体配方（蒸发器，对应原版注册名：evaporating，注意配方类与注册名的特殊映射） */
    public static void addFluidToFluidRecipe(ResourceLocation recipeId, BasicFluidToFluidRecipe fluidToFluidRecipe,
                                             HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, fluidToFluidRecipe, provider, "evaporating");
    }

    /** 物品转能量配方（能量转换器，对应原版注册名：energy_conversion） */
    public static void addEnergyConversionRecipe(ResourceLocation recipeId, BasicItemStackToEnergyRecipe energyConversionRecipe,
                                                 HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, energyConversionRecipe, provider, "energy_conversion");
    }

    /** 色素提取配方（色素提取器，对应原版注册名：pigment_extracting） */
    public static void addPigmentExtractingRecipe(ResourceLocation recipeId, BasicPigmentExtractingRecipe pigmentExtractingRecipe,
                                                  HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, pigmentExtractingRecipe, provider, "pigment_extracting");
    }

    /** 色素混合配方（色素混合器，对应原版注册名：pigment_mixing） */
    public static void addPigmentMixingRecipe(ResourceLocation recipeId, BasicPigmentMixingRecipe pigmentMixingRecipe,
                                              HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, pigmentMixingRecipe, provider, "pigment_mixing");
    }

    /** 核合成配方（核合成机，对应原版注册名：nucleosynthesizing） */
    public static void addNucleosynthesizingRecipe(ResourceLocation recipeId, BasicNucleosynthesizingRecipe nucleosynthesizingRecipe,
                                                   HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, nucleosynthesizingRecipe, provider, "nucleosynthesizing");
    }

    /** 营养液化器（营养液化器，对应原版注册名：nutritional_liquifier） */
    public static void addNutritionalLiquifierIRecipe(ResourceLocation recipeId, NutritionalLiquifierIRecipe nutritionalLiquifierIRecipe,
                                                      HolderLookup.Provider provider) {
        addMekanismGenericRecipe(recipeId, nutritionalLiquifierIRecipe, provider, "nutritional_liquifier");
    }
}
