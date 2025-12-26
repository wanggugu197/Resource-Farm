package com.resource_farm.api.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 可发光方块的核心接口
 * 定义：支持自定义光照等级的方块需实现的规范
 */
public interface LightEmittingBlock {

    /**
     * 获取方块的光照等级（0-15，0为不发光）
     * 
     * @return 光照等级
     */
    int getLightLevel();

    /**
     * 校验光照等级合法性（0-15）
     */
    static void validateLightLevel(int lightLevel) {
        if (lightLevel < 0 || lightLevel > 15) {
            throw new IllegalArgumentException("Light level must be in range 0-15 (inclusive)!");
        }
    }

    /**
     * 构建带光照等级的方块属性（复用逻辑）
     */
    static BlockBehaviour.Properties applyLightLevel(BlockBehaviour.Properties properties, int lightLevel) {
        validateLightLevel(lightLevel);
        return lightLevel == 0 ? properties : properties.lightLevel(state -> lightLevel);
    }
}
