package com.glisco.conjuringforgery.client;

import com.glisco.conjuringforgery.blocks.soul_weaver.SoulWeaverTileEntity;
import com.glisco.owo.client.ClientParticles;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class SoulWeaverTileEntityRenderer extends TileEntityRenderer<SoulWeaverTileEntity> {

    public SoulWeaverTileEntityRenderer(TileEntityRendererDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void render(SoulWeaverTileEntity blockEntity, float tickDelta, MatrixStack matrixStack, IRenderTypeBuffer vertexConsumerProvider, int light, int overlay) {
        if (blockEntity.getItem() != null) {

            ItemStack item = blockEntity.getItem();
            IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(item, null, null);

            matrixStack.push();

            double scale = blockEntity.getShakeScaleFactor();

            double yOffset = (blockEntity.getWorld().rand.nextDouble() - 0.5d) * scale;
            double xOffset = (blockEntity.getWorld().rand.nextDouble() - 0.5d) * scale;
            double zOffset = (blockEntity.getWorld().rand.nextDouble() - 0.5d) * scale;

            matrixStack.translate(0.5 + xOffset, 1.25 + yOffset + scale, 0.5 + zOffset);
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            matrixStack.rotate(Vector3f.YP.rotationDegrees((float) (System.currentTimeMillis() / (60d) % 360d)));
            matrixStack.rotate(Vector3f.YP.rotationDegrees((float) Math.pow(scale * 100, 3)));
            Minecraft.getInstance().getItemRenderer().renderItem(item, ItemCameraTransforms.TransformType.GROUND, false, matrixStack, vertexConsumerProvider, light, OverlayTexture.NO_OVERLAY, itemModel);
            matrixStack.pop();
        }

        if (blockEntity.isLit()) {
            if (blockEntity.getWorld().rand.nextDouble() > 0.85f) {

                ClientParticles.setParticleCount(2);
                ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.SOUL_FIRE_FLAME, blockEntity.getWorld(), blockEntity.getPos(), new Vector3d(0.5, 0.35, 0.5), 0.15);
            }
        }

    }
}
