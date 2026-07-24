package com.maple.resource_farm.api.item;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.Nullable;

/**
 * 可着色物品的核心接口
 * 定义：支持多图层着色的物品需实现的规范
 */
public interface TintableItem {

    Identifier TINT_SOURCE_ID = Identifier.fromNamespaceAndPath("resource_farm", "tintable_item");

    /**
     * 获取标记哪些图层需要着色的数组（返回拷贝，避免外部修改内部状态）
     *
     * @return boolean数组（索引=图层号，值=是否着色）
     */
    boolean[] getTintLayers();

    /**
     * 获取对应图层的着色颜色数组（返回拷贝，避免外部修改内部状态）
     *
     * @return int数组（索引=图层号，值=对应颜色）
     */
    int[] getColors();

    /**
     * 26.1+ 物品着色源：从 {@link TintableItem} 读取指定图层颜色。
     * 在物品模型的 tints 中引用 {@code resource_farm:tintable_item}。
     */
    record TintableItemTintSource(int index) implements ItemTintSource {

        public static final MapCodec<TintableItemTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(
                i -> i.group(
                        com.mojang.serialization.Codec.INT.optionalFieldOf("index", 0)
                                .forGetter(TintableItemTintSource::index))
                        .apply(i, TintableItemTintSource::new));

        @Override
        public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
            if (!(itemStack.getItem() instanceof TintableItem tintableItem)) {
                return -1;
            }
            boolean[] layers = tintableItem.getTintLayers();
            int[] colors = tintableItem.getColors();
            if (index < 0 || index >= layers.length || index >= colors.length) {
                return -1;
            }
            if (!layers[index]) {
                return -1;
            }
            return ARGB.opaque(colors[index]);
        }

        @Override
        public MapCodec<? extends ItemTintSource> type() {
            return MAP_CODEC;
        }
    }
}
