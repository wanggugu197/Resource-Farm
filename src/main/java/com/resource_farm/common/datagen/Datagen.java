package com.resource_farm.common.datagen;

import com.resource_farm.api.lang.SimplifiedChineseLanguageProvider;
import com.resource_farm.api.lang.TraditionalChineseLanguageProvider;
import com.resource_farm.data.lang.LangHandler;

import com.tterrag.registrate.providers.ProviderType;

import static com.resource_farm.ResourceFarm.isDataGen;
import static com.resource_farm.api.registry.ResourceFarmRegistration.REGISTRATE;

public final class Datagen {

    public static void init() {
        if (isDataGen()) {
            // REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, ResourceFarmBlockTags::init);
            // REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, ResourceFarmItemTags::init);
            REGISTRATE.addDataGenerator(ProviderType.LANG, LangHandler::enInitialize);
            REGISTRATE.addDataGenerator(SimplifiedChineseLanguageProvider.LANG, LangHandler::cnInitialize);
            REGISTRATE.addDataGenerator(TraditionalChineseLanguageProvider.LANG, LangHandler::twInitialize);
        }
    }
}
