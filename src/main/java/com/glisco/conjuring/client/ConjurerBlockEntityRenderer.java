package com.glisco.conjuring.client;

import com.glisco.conjuring.blocks.conjurer.ConjurerBlockEntity;
import com.glisco.conjuring.blocks.conjurer.ModifiedMobSpawnerLogic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ConjurerBlockEntityRenderer extends BlockEntityRenderer<ConjurerBlockEntity> {

    public ConjurerBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void render(ConjurerBlockEntity conjurerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        matrixStack.push();

        if (conjurerBlockEntity.isActive()) {
            matrixStack.translate(0.5D, 0.0D, 0.5D);
            ModifiedMobSpawnerLogic mobSpawnerLogic = conjurerBlockEntity.getLogic();
            Entity entity = mobSpawnerLogic.getRenderedEntity();
            if (entity != null) {
                float g = 0.53125F;
                float h = Math.max(entity.getWidth(), entity.getHeight());
                if ((double) h > 1.0D) {
                    g /= h;
                }

                matrixStack.translate(0.0D, 0.4000000059604645D, 0.0D);
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) MathHelper.lerp((double) f, mobSpawnerLogic.method_8279(), mobSpawnerLogic.method_8278()) * 10.0F));
                matrixStack.translate(0.0D, -0.20000000298023224D, 0.0D);
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-30.0F));
                matrixStack.scale(g, g, g);
                MinecraftClient.getInstance().getEntityRenderManager().render(entity, 0.0D, 0.0D, 0.0D, 0.0F, f, matrixStack, vertexConsumerProvider, i);

            }
        }

        matrixStack.pop();
    }
}
