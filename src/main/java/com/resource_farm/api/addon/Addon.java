package com.resource_farm.api.addon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Addon {

    /**
     * The unique mod identifier for this addon.
     * <p>
     * This is required to be the same as the one in your {@link net.neoforged.fml.common.Mod @Mod} annotation.
     */
    String value();
}
