package com.glisco.conjuringforgery.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SoulProjectileEntityRenderer extends EntityRenderer<SoulProjectile> {

    public SoulProjectileEntityRenderer(EntityRendererManager dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SoulProjectile entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

    }

    @Override
    public ResourceLocation getEntityTexture(SoulProjectile entity) {
        return new ResourceLocation("");
    }
}
