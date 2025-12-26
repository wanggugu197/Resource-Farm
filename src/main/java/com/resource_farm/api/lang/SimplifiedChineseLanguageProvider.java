package com.resource_farm.api.lang;

import net.minecraft.data.PackOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.data.LanguageProvider;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;
import org.jetbrains.annotations.NotNull;

public final class SimplifiedChineseLanguageProvider extends LanguageProvider implements RegistrateProvider {

    public static final ProviderType<SimplifiedChineseLanguageProvider> LANG = ProviderType.register("cn_lang",
            (p, e) -> new SimplifiedChineseLanguageProvider(p, e.getGenerator().getPackOutput()));

    private final AbstractRegistrate<?> owner;

    private SimplifiedChineseLanguageProvider(AbstractRegistrate<?> owner, PackOutput packOutput) {
        super(packOutput, owner.getModid(), "zh_cn");
        this.owner = owner;
    }

    @Override
    public @NotNull LogicalSide getSide() {
        return LogicalSide.CLIENT;
    }

    @Override
    public @NotNull String getName() {
        return "Lang (zh_cn)";
    }

    @Override
    protected void addTranslations() {
        this.owner.genData(LANG, this);
    }
}
