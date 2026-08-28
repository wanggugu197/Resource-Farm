package com.maple.resource_farm.plantPot.block;

import com.maple.resource_farm.utils.renderer.CubeBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

public class HoppingBonsaiPotBlockEntityRenderer implements BlockEntityRenderer<HoppingBonsaiPotBlockEntity> {

    private final ItemRenderer itemRenderer;
    private final BlockRenderDispatcher blockRenderDispatcher;

    public HoppingBonsaiPotBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(HoppingBonsaiPotBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = be.getLevel();
        ItemStack soilStack = be.getStack(1);
        ItemStack plantStack = be.getStack(0);
        boolean isCrop = be.isCrop();
        int growthProgress = be.getGrowthProgress();
        int growthStage = be.getGrowthStage();
        boolean soilIsFluid = !soilStack.isEmpty() && (soilStack.getItem() instanceof BucketItem);

        // 1. 渲染土壤
        if (!soilStack.isEmpty()) {
            if (soilIsFluid) {
                BucketItem bucket = (BucketItem) soilStack.getItem();
                Fluid fluid = bucket.content;
                FluidStack fluidStack = new FluidStack(fluid, 1000);
                ResourceLocation stillTexture = IClientFluidTypeExtensions.of(fluid).getStillTexture(fluidStack);
                if (stillTexture != null) {
                    TextureAtlasSprite fluidSprite = Minecraft.getInstance().getModelManager()
                            .getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(stillTexture);
                    int fluidColor = IClientFluidTypeExtensions.of(fluid).getTintColor(fluidStack);

                    poseStack.pushPose();
                    poseStack.translate(0.125, 0.3125, 0.125);
                    poseStack.scale(0.75f, 0.25f, 0.75f);

                    VertexConsumer consumer = bufferSource.getBuffer(Sheets.translucentItemSheet());
                    CubeBuilder builder = new CubeBuilder(bakedQuad -> consumer.putBulkData(
                            poseStack.last(), bakedQuad, 1.0F, 1.0F, 1.0F, 1.0F, packedLight, packedOverlay, true));
                    builder.setColor(fluidColor);
                    builder.setTexture(fluidSprite);
                    builder.addCube(0, 0, 0, 16, 16, 16);
                    poseStack.popPose();
                }
            } else {
                poseStack.pushPose();
                poseStack.translate(0.5, 0.5, 0.5);
                if (!(soilStack.getItem() instanceof BlockItem)) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                    poseStack.scale(0.75f, 0.75f, 0.75f);
                } else {
                    poseStack.scale(1.5f, 0.25f, 1.5f);
                }
                itemRenderer.renderStatic(soilStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, level, 0);
                poseStack.popPose();
            }
        }

        // 2. 渲染植物
        if (!plantStack.isEmpty() && !soilStack.isEmpty()) {
            if (plantStack.getItem() instanceof BlockItem) {
                BlockState plantState;
                if (isCrop) {
                    plantState = getCropBlockState(plantStack, growthStage);
                } else {
                    plantState = ((BlockItem) plantStack.getItem()).getBlock().defaultBlockState();
                }
                if (plantState != null) {
                    poseStack.pushPose();
                    if (isCrop) {
                        poseStack.translate(0.125, 0.6, 0.125);
                        poseStack.scale(0.75f, 0.75f, 0.75f);
                    } else {
                        float scale = 0.475f + 0.4f * growthProgress / 100f;
                        poseStack.translate(0.5, 0.5625, 0.5);
                        poseStack.scale(scale, scale, scale);
                        poseStack.translate(-0.5, 0.0, -0.5);
                    }
                    blockRenderDispatcher.renderSingleBlock(plantState, poseStack, bufferSource, packedLight, packedOverlay, ModelData.EMPTY, null);
                    poseStack.popPose();
                }
            } else {
                poseStack.pushPose();
                float scale = 0.475f + 0.4f * growthProgress / 100f;
                poseStack.translate(0.5, 0.875, 0.5);
                poseStack.scale(scale, scale, scale);
                itemRenderer.renderStatic(plantStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, level, 0);
                poseStack.popPose();
            }
        }

        // 3. 渲染玻璃罩
        ItemStack cloched = ClocheHelper.getClocheItem(be.getClochedTier());
        if (!cloched.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.1875, 0.5);
            poseStack.scale(2, 2, 2);
            itemRenderer.renderStatic(cloched, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, level, 0);
            poseStack.popPose();
        }
    }

    // ---------- 辅助方法 ----------
    private static BlockState getCropBlockState(ItemStack stack, int age) {
        if (!(stack.getItem() instanceof BlockItem bi)) return null;
        BlockState def = bi.getBlock().defaultBlockState();
        for (Property<?> prop : def.getProperties()) {
            if (prop instanceof IntegerProperty ip && prop.getName().equals("age")) {
                int max = ip.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(7);
                return def.setValue(ip, Math.min(age, max));
            }
        }
        if (def.hasProperty(BlockStateProperties.AGE_7)) return def.setValue(BlockStateProperties.AGE_7, Math.min(age, 7));
        if (def.hasProperty(BlockStateProperties.AGE_3)) return def.setValue(BlockStateProperties.AGE_3, Math.min(age, 3));
        if (def.hasProperty(BlockStateProperties.AGE_5)) return def.setValue(BlockStateProperties.AGE_5, Math.min(age, 5));
        if (def.hasProperty(BlockStateProperties.AGE_15)) return def.setValue(BlockStateProperties.AGE_15, Math.min(age, 15));
        if (def.hasProperty(BlockStateProperties.AGE_25)) return def.setValue(BlockStateProperties.AGE_25, Math.min(age, 25));
        return def;
    }
}
