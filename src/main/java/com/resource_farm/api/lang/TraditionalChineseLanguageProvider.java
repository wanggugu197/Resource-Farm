package com.resource_farm.api.lang;

import net.minecraft.data.PackOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.data.LanguageProvider;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;
import org.jetbrains.annotations.NotNull;

public final class TraditionalChineseLanguageProvider extends LanguageProvider implements RegistrateProvider {

    public static final ProviderType<TraditionalChineseLanguageProvider> LANG = ProviderType.register("tw_lang",
            (p, e) -> new TraditionalChineseLanguageProvider(p, e.getGenerator().getPackOutput()));

    private final AbstractRegistrate<?> owner;

    private TraditionalChineseLanguageProvider(AbstractRegistrate<?> owner, PackOutput packOutput) {
        super(packOutput, owner.getModid(), "zh_tw");
        this.owner = owner;
    }

    @Override
    public @NotNull LogicalSide getSide() {
        return LogicalSide.CLIENT;
    }

    @Override
    public @NotNull String getName() {
        return "Lang (zh_tw)";
    }

    @Override
    protected void addTranslations() {
        this.owner.genData(LANG, this);
    }
}
