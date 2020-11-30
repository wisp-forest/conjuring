package com.glisco.conjuring.client;

import com.glisco.conjuring.blocks.SoulFunnelBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class SoulFunnelBlockEntityRenderer extends BlockEntityRenderer<SoulFunnelBlockEntity> {

    public SoulFunnelBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void render(SoulFunnelBlockEntity blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        if (blockEntity.getItem() != null) {
            ItemStack item = blockEntity.getItem();
            BakedModel itemModel = MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(item, null, null);

            int lightAbove = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(blockEntity.getWorld()), blockEntity.getPos().up());

            matrixStack.push();
            matrixStack.translate(0.5, 0.45 + (float) 1.5 * Math.sin(Math.PI * ((blockEntity.getWorld().getTime() % 24000.0f) * 3f) / 100f) / 50f, 0.405);
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90f));
            MinecraftClient.getInstance().getItemRenderer().renderItem(item, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumerProvider, lightAbove, OverlayTexture.DEFAULT_UV, itemModel);
            matrixStack.pop();

            if (blockEntity.getWorld().random.nextDouble() > 0.95) {
                BlockPos pos = blockEntity.getPos();

                ParticleEffect particle = blockEntity.onCooldown() ? ParticleTypes.SMOKE : ParticleTypes.WITCH;

                blockEntity.getWorld().addParticle(particle, pos.getX() + 0.5, pos.getY() + blockEntity.getItemHeight(), pos.getZ() + 0.5, 0, 0, 0);
            }
        }

    }
}
