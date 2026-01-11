package com.resource_farm.common.pack.crossover;

import com.resource_farm.ResourceFarm;
import com.resource_farm.config.ResourceFarmConfigHolder;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;

import java.nio.file.Path;

import static com.resource_farm.common.pack.ResourceFarmDynamicDataPack.*;

public class CrossoverCreateDynamicDataPack {

    /** 统一Create配方注册入口 */
    public static void addCreateRecipe(ResourceLocation recipeId, Object recipeObj, HolderLookup.Provider provider) {
        if (recipeObj == null) {
            ResourceFarm.LOGGER.error("Failed to register Create recipe: {} - recipe object is null", recipeId);
            return;
        }
        switch (recipeObj) {
            // 动力合成 | 序列装配
            case MechanicalCraftingRecipe mcr -> addCreateMechanicalCraftingRecipe(recipeId, mcr, provider);
            case SequencedAssemblyRecipe sar -> addCreateSequencedAssemblyRecipe(recipeId, sar, provider);
            // 手动使用 | 机械手使用
            case ManualApplicationRecipe mar -> addCreateManualApplicationRecipe(recipeId, mar, provider);
            case DeployerApplicationRecipe dar -> addCreateDeployerApplicationRecipe(recipeId, dar, provider);
            // 粉碎 | 研磨
            case CrushingRecipe cr -> addCreateCrushingRecipe(recipeId, cr, provider);
            case MillingRecipe mr -> addCreateMillingRecipe(recipeId, mr, provider);
            // 塑形 | 混合搅拌
            case CompactingRecipe cpr -> addCreateCompactingRecipe(recipeId, cpr, provider);
            case MixingRecipe mir -> addCreateMixingRecipe(recipeId, mir, provider);
            // 分液 | 注液
            case EmptyingRecipe er -> addCreateEmptyingRecipe(recipeId, er, provider);
            case FillingRecipe fr -> addCreateFillingRecipe(recipeId, fr, provider);
            // 缠魂 | 洗涤
            case HauntingRecipe hr -> addCreateHauntingRecipe(recipeId, hr, provider);
            case SplashingRecipe sr -> addCreateSplashingRecipe(recipeId, sr, provider);
            // 砂纸抛光 | 冲压 | 切割
            case SandPaperPolishingRecipe sppr -> addCreateSandPaperPolishingRecipe(recipeId, sppr, provider);
            case PressingRecipe pr -> addCreatePressingRecipe(recipeId, pr, provider);
            case CuttingRecipe cr -> addCreateCuttingRecipe(recipeId, cr, provider);
            // 未识别具体配方
            default -> {
                ResourceFarm.LOGGER.error("Failed to register Create recipe: {} - unsupported concrete recipe type: {}",
                        recipeId, recipeObj.getClass().getName());
                ResourceFarm.LOGGER.warn("Supported concrete recipe types: " +
                        "MechanicalCraftingRecipe, SequencedAssemblyRecipe, ManualApplicationRecipe, DeployerApplicationRecipe, " +
                        "CrushingRecipe, MillingRecipe, CompactingRecipe, MixingRecipe, EmptyingRecipe, FillingRecipe, " +
                        "HauntingRecipe, SplashingRecipe, SandPaperPolishingRecipe, PressingRecipe, CuttingRecipe");
            }
        }
    }

    /** 注册Create动力合成配方 */
    @SuppressWarnings("unchecked")
    public static void addCreateMechanicalCraftingRecipe(
                                                         ResourceLocation recipeId,
                                                         MechanicalCraftingRecipe mechanicalRecipe,
                                                         HolderLookup.Provider provider) {
        try {
            ResourceLocation targetRecipeLoc = RECIPE_CONVERTER.idToFile(recipeId);

            RecipeSerializer<MechanicalCraftingRecipe> recipeSerializer = (RecipeSerializer<MechanicalCraftingRecipe>) mechanicalRecipe.getSerializer();
            MapCodec<MechanicalCraftingRecipe> mapCodec = recipeSerializer.codec();
            Codec<MechanicalCraftingRecipe> standardCodec = mapCodec.codec();
            JsonElement recipeJson = standardCodec
                    .encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), mechanicalRecipe)
                    .getOrThrow(unchecked -> new IllegalArgumentException("Failed to serialize Create MechanicalCraftingRecipe: " + unchecked));

            if (recipeJson instanceof JsonObject jsonObject) {
                jsonObject.addProperty("type", "create:mechanical_crafting");
            }

            byte[] recipeBytes = jsonToBytes(recipeJson);
            Path parent = ResourceFarm.getGameDir().resolve("resource_farm/dumped/data");
            if (ResourceFarmConfigHolder.FarmConfigHolder.dev.dumpData) {
                writeJson(targetRecipeLoc, null, parent, recipeBytes);
            }
            addToData(targetRecipeLoc, recipeBytes);
        } catch (Exception e) {
            ResourceFarm.LOGGER.error("Failed to add Create MechanicalCraftingRecipe for {}", recipeId, e);
        }
    }

    /** 注册Create序列组装配方 */
    public static void addCreateSequencedAssemblyRecipe(
                                                        ResourceLocation recipeId,
                                                        SequencedAssemblyRecipe sequencedAssemblyRecipe,
                                                        HolderLookup.Provider provider) {
        try {
            ResourceLocation targetRecipeLoc = RECIPE_CONVERTER.idToFile(recipeId);

            SequencedAssemblyRecipeSerializer recipeSerializer = (SequencedAssemblyRecipeSerializer) sequencedAssemblyRecipe.getSerializer();
            MapCodec<SequencedAssemblyRecipe> mapCodec = recipeSerializer.codec();
            Codec<SequencedAssemblyRecipe> standardCodec = mapCodec.codec();
            JsonElement recipeJson = standardCodec
                    .encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), sequencedAssemblyRecipe)
                    .getOrThrow(unchecked -> new IllegalArgumentException("Failed to serialize Create SequencedAssemblyRecipe: " + unchecked));

            if (recipeJson instanceof JsonObject jsonObject) {
                jsonObject.addProperty("type", "create:sequenced_assembly");
            }

            byte[] recipeBytes = jsonToBytes(recipeJson);
            Path parent = ResourceFarm.getGameDir().resolve("resource_farm/dumped/data");
            if (ResourceFarmConfigHolder.FarmConfigHolder.dev.dumpData) {
                writeJson(targetRecipeLoc, null, parent, recipeBytes);
            }
            addToData(targetRecipeLoc, recipeBytes);
        } catch (Exception e) {
            ResourceFarm.LOGGER.error("Failed to add Create SequencedAssemblyRecipe for {}", recipeId, e);
        }
    }

    /** 注册Create配方 */
    @SuppressWarnings("unchecked")
    public static <T extends ProcessingRecipe<?, ?>> void addCreateProcessingRecipe(
                                                                                    ResourceLocation recipeId,
                                                                                    T createRecipe,
                                                                                    HolderLookup.Provider provider,
                                                                                    String type) {
        try {
            ResourceLocation targetRecipeLoc = RECIPE_CONVERTER.idToFile(recipeId);

            RecipeSerializer<T> recipeSerializer = (RecipeSerializer<T>) createRecipe.getSerializer();
            MapCodec<T> mapCodec = recipeSerializer.codec();
            Codec<T> standardCodec = mapCodec.codec();
            JsonElement recipeJson = standardCodec
                    .encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), createRecipe)
                    .getOrThrow(unchecked -> new IllegalArgumentException("Failed to serialize Create recipe: " + unchecked));

            if (recipeJson instanceof JsonObject jsonObject) {
                jsonObject.addProperty("type", type);
            }

            byte[] recipeBytes = jsonToBytes(recipeJson);
            Path parent = ResourceFarm.getGameDir().resolve("resource_farm/dumped/data");
            if (ResourceFarmConfigHolder.FarmConfigHolder.dev.dumpData) {
                writeJson(targetRecipeLoc, null, parent, recipeBytes);
            }
            addToData(targetRecipeLoc, recipeBytes);
        } catch (Exception e) {
            ResourceFarm.LOGGER.error("Failed to add Create processing recipe for {}", recipeId, e);
        }
    }

    /** 注册Create标准配方 */
    public static <T extends StandardProcessingRecipe<?>> void addCreateStandardProcessingRecipe(
                                                                                                 ResourceLocation recipeId,
                                                                                                 T standardProcessingRecipe,
                                                                                                 HolderLookup.Provider provider,
                                                                                                 String type) {
        addCreateProcessingRecipe(recipeId, standardProcessingRecipe, provider, type);
    }

    /** 注册Create盆配方 */
    public static void addCreateBasinRecipe(
                                            ResourceLocation recipeId,
                                            BasinRecipe basinRecipe,
                                            HolderLookup.Provider provider,
                                            String type) {
        addCreateStandardProcessingRecipe(recipeId, basinRecipe, provider, type);
    }

    /** 注册Create塑形配方 */
    public static void addCreateCompactingRecipe(
                                                 ResourceLocation recipeId,
                                                 CompactingRecipe compactingRecipe,
                                                 HolderLookup.Provider provider) {
        addCreateBasinRecipe(recipeId, compactingRecipe, provider, "create:compacting");
    }

    /** 注册Create混合搅拌配方 */
    public static void addCreateMixingRecipe(
                                             ResourceLocation recipeId,
                                             MixingRecipe mixingRecipe,
                                             HolderLookup.Provider provider) {
        addCreateBasinRecipe(recipeId, mixingRecipe, provider, "create:mixing");
    }

    /** 注册Create分液配方 */
    public static void addCreateEmptyingRecipe(
                                               ResourceLocation recipeId,
                                               EmptyingRecipe emptyingRecipe,
                                               HolderLookup.Provider provider) {
        addCreateStandardProcessingRecipe(recipeId, emptyingRecipe, provider, "create:emptying");
    }

    /** 注册Create注液配方 */
    public static void addCreateFillingRecipe(
                                              ResourceLocation recipeId,
                                              FillingRecipe fillingRecipe,
                                              HolderLookup.Provider provider) {
        addCreateStandardProcessingRecipe(recipeId, fillingRecipe, provider, "create:filling");
    }

    /** 注册Create缠魂化配方 */
    public static void addCreateHauntingRecipe(
                                               ResourceLocation recipeId,
                                               HauntingRecipe hauntingRecipe,
                                               HolderLookup.Provider provider) {
        addCreateStandardProcessingRecipe(recipeId, hauntingRecipe, provider, "create:haunting");
    }

    /** 注册Create洗涤配方 */
    public static void addCreateSplashingRecipe(
                                                ResourceLocation recipeId,
                                                SplashingRecipe splashingRecipe,
                                                HolderLookup.Provider provider) {
        addCreateStandardProcessingRecipe(recipeId, splashingRecipe, provider, "create:splashing");
    }

    /** 注册Create砂纸抛光配方 */
    public static void addCreateSandPaperPolishingRecipe(
                                                         ResourceLocation recipeId,
                                                         SandPaperPolishingRecipe sandPaperPolishingRecipe,
                                                         HolderLookup.Provider provider) {
        addCreateStandardProcessingRecipe(recipeId, sandPaperPolishingRecipe, provider, "create:sandpaper_polishing");
    }

    /** 注册Create冲压配方 */
    public static void addCreatePressingRecipe(
                                               ResourceLocation recipeId,
                                               PressingRecipe pressingRecipe,
                                               HolderLookup.Provider provider) {
        addCreateStandardProcessingRecipe(recipeId, pressingRecipe, provider, "create:pressing");
    }

    /** 注册Create切削配方 */
    public static void addCreateCuttingRecipe(
                                              ResourceLocation recipeId,
                                              CuttingRecipe cuttingRecipe,
                                              HolderLookup.Provider provider) {
        addCreateStandardProcessingRecipe(recipeId, cuttingRecipe, provider, "create:cutting");
    }

    /** 注册Create抽象物品使用配方 */
    public static void addCreateItemApplicationRecipe(
                                                      ResourceLocation recipeId,
                                                      ItemApplicationRecipe itemApplicationRecipe,
                                                      HolderLookup.Provider provider,
                                                      String type) {
        addCreateProcessingRecipe(recipeId, itemApplicationRecipe, provider, type);
    }

    /** 注册Create机械手使用应用配方 */
    public static void addCreateDeployerApplicationRecipe(
                                                          ResourceLocation recipeId,
                                                          DeployerApplicationRecipe deployerApplicationRecipe,
                                                          HolderLookup.Provider provider) {
        addCreateItemApplicationRecipe(recipeId, deployerApplicationRecipe, provider, "create:deploying");
    }

    /** 注册Create手动应用配方 */
    public static void addCreateManualApplicationRecipe(ResourceLocation recipeId,
                                                        ManualApplicationRecipe manualApplicationRecipe,
                                                        HolderLookup.Provider provider) {
        addCreateItemApplicationRecipe(recipeId, manualApplicationRecipe, provider, "create:item_application");
    }

    /** 注册Create抽象粉碎配方 */
    public static <T extends AbstractCrushingRecipe> void addCreateAbstractCrushingRecipe(
                                                                                          ResourceLocation recipeId,
                                                                                          T abstractCrushingRecipe,
                                                                                          HolderLookup.Provider provider,
                                                                                          String type) {
        addCreateStandardProcessingRecipe(recipeId, abstractCrushingRecipe, provider, type);
    }

    /** 注册Create粉碎配方 */
    public static void addCreateCrushingRecipe(ResourceLocation recipeId,
                                               CrushingRecipe crushingRecipe,
                                               HolderLookup.Provider provider) {
        addCreateAbstractCrushingRecipe(recipeId, crushingRecipe, provider, "create:crushing");
    }

    /** 注册Create研磨配方 */
    public static void addCreateMillingRecipe(ResourceLocation recipeId,
                                              MillingRecipe millingRecipe,
                                              HolderLookup.Provider provider) {
        addCreateAbstractCrushingRecipe(recipeId, millingRecipe, provider, "create:milling");
    }
}
