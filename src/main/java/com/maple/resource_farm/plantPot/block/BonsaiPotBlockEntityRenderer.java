package com.maple.resource_farm.plantPot.block;

import com.maple.resource_farm.utils.renderer.CubeBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.math.Axis;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BonsaiPotBlockEntityRenderer implements BlockEntityRenderer<BonsaiPotBlockEntity, BonsaiPotBlockEntityRenderer.RenderState> {

    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();

    private final ItemModelResolver soilModelResolver;
    private final BlockModelResolver plantModelResolver;

    public BonsaiPotBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.soilModelResolver = context.itemModelResolver();
        this.plantModelResolver = context.blockModelResolver();
    }

    public static class RenderState extends BlockEntityRenderState {

        public boolean isCrop = false;
        public ItemStack soilStack = ItemStack.EMPTY;
        public ItemStack plantStack = ItemStack.EMPTY;
        public int growthProgress = 0;
        public int growthStage = 0;
        public boolean soilIsFluid = false;
        final ItemStackRenderState soilRenderState = new ItemStackRenderState();
        final BlockModelRenderState plantModel = new BlockModelRenderState();
        final ItemStackRenderState plantItemRenderState = new ItemStackRenderState();
        public TextureAtlasSprite fluidSprite;
        public int fluidColor = -1;
    }

    @Override
    public @NonNull RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(@NonNull BonsaiPotBlockEntity be, @NonNull RenderState state, float partialTick,
                                   @NonNull Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);

        state.isCrop = be.isCrop();
        state.soilStack = be.getStack(1).copy();
        state.plantStack = be.getStack(0).copy();
        state.growthProgress = be.getGrowthProgress();
        state.growthStage = be.getGrowthStage();

        state.soilIsFluid = !state.soilStack.isEmpty() && (state.soilStack.getItem() instanceof BucketItem);

        state.soilRenderState.clear();
        state.fluidSprite = null;
        state.fluidColor = -1;

        if (!state.soilStack.isEmpty()) {
            if (state.soilIsFluid) {
                BucketItem bucket = (BucketItem) state.soilStack.getItem();
                Fluid fluid = bucket.getContent();
                var fluidModel = Minecraft.getInstance().getModelManager()
                        .getFluidStateModelSet().get(fluid.defaultFluidState());
                state.fluidSprite = fluidModel.stillMaterial().sprite();
                var tintSource = fluidModel.fluidTintSource();
                if (tintSource != null) {
                    state.fluidColor = tintSource.colorAsStack(new FluidStack(fluid, 1000));
                } else {
                    state.fluidColor = -1;
                }
            } else {
                soilModelResolver.updateForTopItem(state.soilRenderState, state.soilStack,
                        ItemDisplayContext.FIXED, be.getLevel(), null, 0);
            }
        }

        state.plantModel.clear();
        if (!state.plantStack.isEmpty() && !state.soilStack.isEmpty()) {
            ItemStack plant = state.plantStack;
            if (plant.getItem() instanceof BlockItem) {
                if (state.isCrop) {
                    BlockState cropState = getCropBlockState(state.plantStack, state.growthStage);
                    if (cropState != null) {
                        plantModelResolver.update(state.plantModel, cropState, BLOCK_DISPLAY_CONTEXT);
                    }
                } else {
                    BlockState saplingState = ((BlockItem) state.plantStack.getItem()).getBlock().defaultBlockState();
                    plantModelResolver.update(state.plantModel, saplingState, BLOCK_DISPLAY_CONTEXT);
                }
            } else {
                soilModelResolver.updateForTopItem(state.plantItemRenderState, plant,
                        ItemDisplayContext.FIXED, be.getLevel(), null, 0);
            }
        }
    }

    @Override
    public void submit(RenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector,
                       @NonNull CameraRenderState cameraState) {
        int light = state.lightCoords;

        // 1. 渲染土壤
        if (!state.soilStack.isEmpty()) {
            if (state.soilIsFluid && state.fluidSprite != null) {
                poseStack.pushPose();
                poseStack.translate(0.0625, 0.125, 0.0625);
                poseStack.scale(0.875f, 0.125f, 0.875f);

                collector.submitCustomGeometry(
                        poseStack,
                        Sheets.translucentBlockItemSheet(),
                        (pose, consumer) -> {
                            QuadInstance qi = new QuadInstance();
                            qi.setColor(state.fluidColor);
                            qi.setLightCoords(light);
                            qi.setOverlayCoords(OverlayTexture.NO_OVERLAY);
                            CubeBuilder builder = new CubeBuilder(bakedQuad -> consumer.putBakedQuad(pose, bakedQuad, qi));
                            builder.setTexture(state.fluidSprite);
                            builder.addCube(0, 0, 0, 16, 16, 16);
                        });
                poseStack.popPose();
            } else {
                poseStack.pushPose();
                poseStack.translate(0.5, 0.2, 0.5);
                if (!(state.soilStack.getItem() instanceof BlockItem)) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                    poseStack.scale(0.875f, 0.875f, 0.875f);
                } else {
                    poseStack.scale(1.75f, 0.2f, 1.75f);
                }
                state.soilRenderState.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }
        }

        // 2. 渲染植物
        if (!state.plantModel.isEmpty()) {
            poseStack.pushPose();
            if (state.isCrop) {
                poseStack.translate(0.0625, 0.295, 0.0625);
                poseStack.scale(0.875f, 0.875f, 0.875f);
            } else {
                float scale = 0.475f + 0.4f * state.growthProgress / 100f;
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.scale(scale, scale, scale);
                poseStack.translate(-0.5, 0.0, -0.5);
            }
            state.plantModel.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        } else if (!state.plantItemRenderState.isEmpty()) {
            poseStack.pushPose();
            float scale = 0.475f + 0.4f * state.growthProgress / 100f;
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(0, 0, 0);
            state.plantItemRenderState.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, 0);
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
