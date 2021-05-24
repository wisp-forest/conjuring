package com.glisco.conjuringforgery.client;

import com.glisco.conjuringforgery.WorldHelper;
import com.glisco.conjuringforgery.blocks.BlackstonePedestalTileEntity;
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

public class BlackstonePedestalTileEntityRenderer extends TileEntityRenderer<BlackstonePedestalTileEntity> {

    public BlackstonePedestalTileEntityRenderer(TileEntityRendererDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void render(BlackstonePedestalTileEntity blockEntity, float tickDelta, MatrixStack matrixStack, IRenderTypeBuffer vertexConsumerProvider, int i, int j) {
        if (blockEntity.getRenderedItem() != null) {

            ItemStack item = blockEntity.getRenderedItem();
            IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(item, null, null);

            int lightAbove = WorldRenderer.getCombinedLight(Objects.requireNonNull(blockEntity.getWorld()), blockEntity.getPos().up());

            matrixStack.push();
            matrixStack.translate(0.5, 1.25, 0.5);
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            matrixStack.rotate(Vector3f.YP.rotationDegrees((float) (System.currentTimeMillis() / 20d % 360d)));
            Minecraft.getInstance().getItemRenderer().renderItem(item, ItemCameraTransforms.TransformType.GROUND, false, matrixStack, vertexConsumerProvider, lightAbove, OverlayTexture.NO_OVERLAY, itemModel);
            matrixStack.pop();

            if (blockEntity.getWorld().rand.nextDouble() > 0.975) {
                BlockPos pos = blockEntity.getPos();
                IParticleData particle = blockEntity.isActive() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMOKE;

                WorldHelper.spawnParticle(particle, blockEntity.getWorld(), pos, 0.5f, 1.35f, 0.5f, 0.25f);
            }
        }

    }
}
