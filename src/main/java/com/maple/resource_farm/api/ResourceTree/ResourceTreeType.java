package com.maple.resource_farm.api.ResourceTree;

import net.minecraft.resources.Identifier;

/**
 * 树木样式定义。
 * <p>
 * 方块侧存<strong>基底模型</strong>（vanilla / 其他模组 block model），由 multipart
 * 与着色叠加模型联合渲染，不再存各面纹理索引。
 * <p>
 * 物品叠加层 / 着色层仍用纹理 Identifier。
 */
public record ResourceTreeType(
                               String type,
                               // ---- 基底模型（models/block/...）----
                               Identifier saplingBase,
                               Identifier leavesBase,
                               Identifier logBase,
                               Identifier logHorizontalBase,
                               Identifier strippedLogBase,
                               Identifier strippedLogHorizontalBase,
                               Identifier woodBase,
                               Identifier strippedWoodBase,
                               Identifier planksBase,
                               // ---- 着色 / 物品叠加纹理 ----
                               Identifier saplingOverlay,
                               Identifier leavesOverlay,
                               Identifier resin,
                               Identifier resinOverlay,
                               Identifier fruit,
                               Identifier fruitOverlay,
                               Identifier clump,
                               Identifier clumpOverlay,
                               // ---- 翻译键 ----
                               String saplingTranslateKey,
                               String leavesTranslateKey,
                               String logTranslateKey,
                               String strippedLogTranslateKey,
                               String woodTranslateKey,
                               String strippedWoodTranslateKey,
                               String planksTranslateKey,
                               String resinTranslateKey,
                               String fruitTranslateKey,
                               String clumpTranslateKey) {

    public static ResourceTreeType of(String type,
                                      Identifier saplingBase,
                                      Identifier leavesBase,
                                      Identifier logBase,
                                      Identifier logHorizontalBase,
                                      Identifier strippedLogBase,
                                      Identifier strippedLogHorizontalBase,
                                      Identifier woodBase,
                                      Identifier strippedWoodBase,
                                      Identifier planksBase,
                                      Identifier saplingOverlay,
                                      Identifier leavesOverlay,
                                      Identifier resin,
                                      Identifier resinOverlay,
                                      Identifier fruit,
                                      Identifier fruitOverlay,
                                      Identifier clump,
                                      Identifier clumpOverlay,
                                      String saplingTranslateKey,
                                      String leavesTranslateKey,
                                      String logTranslateKey,
                                      String strippedLogTranslateKey,
                                      String woodTranslateKey,
                                      String strippedWoodTranslateKey,
                                      String planksTranslateKey,
                                      String resinTranslateKey,
                                      String fruitTranslateKey,
                                      String clumpTranslateKey) {
        return new ResourceTreeType(type,
                saplingBase, leavesBase,
                logBase, logHorizontalBase,
                strippedLogBase, strippedLogHorizontalBase,
                woodBase, strippedWoodBase, planksBase,
                saplingOverlay, leavesOverlay,
                resin, resinOverlay,
                fruit, fruitOverlay,
                clump, clumpOverlay,
                saplingTranslateKey,
                leavesTranslateKey,
                logTranslateKey,
                strippedLogTranslateKey,
                woodTranslateKey,
                strippedWoodTranslateKey,
                planksTranslateKey,
                resinTranslateKey,
                fruitTranslateKey,
                clumpTranslateKey);
    }

    /**
     * 树苗物品底层纹理：与基底模型同路径约定（vanilla 纹理 id = block/...）。
     */
    public Identifier saplingItemTexture() {
        return saplingBase;
    }

    /**
     * 树叶物品 / 粒子等：与基底模型同路径约定。
     */
    public Identifier leavesItemTexture() {
        return leavesBase;
    }
}
