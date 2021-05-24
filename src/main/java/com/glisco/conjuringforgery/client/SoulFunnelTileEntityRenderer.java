package com.glisco.conjuringforgery.client;

import com.glisco.conjuringforgery.blocks.SoulFunnelTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Objects;

public class SoulFunnelTileEntityRenderer extends TileEntityRenderer<SoulFunnelTileEntity> {

    public SoulFunnelTileEntityRenderer(TileEntityRendererDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void render(SoulFunnelTileEntity blockEntity, float f, MatrixStack matrixStack, IRenderTypeBuffer vertexConsumerProvider, int i, int j) {

        if (blockEntity.getItem() != null) {
            ItemStack item = blockEntity.getItem();
            IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(item, null, null);

            int lightAbove = WorldRenderer.getCombinedLight(Objects.requireNonNull(blockEntity.getWorld()), blockEntity.getPos().up());

            matrixStack.push();
            matrixStack.translate(0.5, 0.45 + (float) 1.5 * Math.sin(Math.PI * ((float) (System.currentTimeMillis() / 20d % 200d)) / 100f) / 50f, 0.405);
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(90f));
            Minecraft.getInstance().getItemRenderer().renderItem(item, ItemCameraTransforms.TransformType.GROUND, false, matrixStack, vertexConsumerProvider, lightAbove, OverlayTexture.NO_OVERLAY, itemModel);
            matrixStack.pop();

            if (blockEntity.getWorld().rand.nextDouble() > 0.95) {
                BlockPos pos = blockEntity.getPos();

                IParticleData particle = blockEntity.onCooldown() ? ParticleTypes.SMOKE : ParticleTypes.WITCH;

                blockEntity.getWorld().addParticle(particle, pos.getX() + 0.5, pos.getY() + blockEntity.getItemHeight(), pos.getZ() + 0.5, 0, 0, 0);
            }
        }

    }
}
