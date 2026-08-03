package com.maple.resource_farm.resourceTree.data.conditions;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ConditionRegister {

//    public static void init() {}

    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, "resource_farm");

    public static final Supplier<MapCodec<ModLoadedCondition>> MOD_LOADED_SINGLE =
            CONDITION_CODECS.register("mod_loaded", () -> ModLoadedCondition.CODEC);

    public static final Supplier<MapCodec<? extends ICondition>> TREE_EXISTS =
            CONDITION_CODECS.register("tree_exists", () -> TreeExistsCondition.CODEC);

    public static final Supplier<MapCodec<? extends ICondition>> GenerateStrippedLog =
            CONDITION_CODECS.register("generate_stripped_Log", () -> GenerateStrippedLogCondition.CODEC);

    public static final Supplier<MapCodec<? extends ICondition>> GenerateWood =
            CONDITION_CODECS.register("generate_wood", () -> GenerateWoodCondition.CODEC);

    public static void init(final IEventBus modBus) {
        CONDITION_CODECS.register(modBus);
    }
}
