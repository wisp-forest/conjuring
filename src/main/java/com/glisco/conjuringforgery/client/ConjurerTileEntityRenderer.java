package com.glisco.conjuringforgery.client;

import com.glisco.conjuringforgery.blocks.conjurer.ConjurerTileEntity;
import com.glisco.conjuringforgery.blocks.conjurer.ModifiedAbstractSpawner;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class ConjurerTileEntityRenderer extends TileEntityRenderer<ConjurerTileEntity> {

    public ConjurerTileEntityRenderer(TileEntityRendererDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void render(ConjurerTileEntity conjurerBlockEntity, float tickDelta, MatrixStack matrixStack, IRenderTypeBuffer vertexConsumerProvider, int i, int j) {
        matrixStack.push();

        if (conjurerBlockEntity.isActive()) {
            matrixStack.translate(0.5D, 0.0D, 0.5D);
            ModifiedAbstractSpawner mobSpawnerLogic = conjurerBlockEntity.getLogic();
            Entity entity = mobSpawnerLogic.getCachedEntity();
            if (entity != null) {
                float g = 0.53125F;
                float h = Math.max(entity.getWidth(), entity.getHeight());
                if ((double) h > 1.0D) {
                    g /= h;
                }

                if (!conjurerBlockEntity.getLogic().isPlayerInRange()) tickDelta = 0;

                matrixStack.translate(0.0D, 0.4000000059604645D, 0.0D);
                matrixStack.rotate(Vector3f.YP.rotationDegrees((float) MathHelper.lerp((double) tickDelta, mobSpawnerLogic.getPrevMobRotation(), mobSpawnerLogic.getMobRotation()) * 10.0F));
                matrixStack.translate(0.0D, -0.20000000298023224D, 0.0D);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(-30.0F));
                matrixStack.scale(g, g, g);
                Minecraft.getInstance().getRenderManager().renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, tickDelta, matrixStack, vertexConsumerProvider, i);

            }
        }

        matrixStack.pop();
    }
}
