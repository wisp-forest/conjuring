package com.glisco.conjuring.client.ber;

import com.glisco.conjuring.blocks.SoulFunnelBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

public class SoulFunnelBlockEntityRenderer implements BlockEntityRenderer<SoulFunnelBlockEntity> {

    public SoulFunnelBlockEntityRenderer(BlockEntityRendererFactory.Context context) {}

    public void render(SoulFunnelBlockEntity blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int uv) {
        if (blockEntity.getItem().isEmpty()) return;
        ItemStack item = blockEntity.getItem();
        BakedModel itemModel = MinecraftClient.getInstance().getItemRenderer().getModel(item, null, null, 0);

        matrixStack.push();

        matrixStack.translate(0.5, 0.45 + (float) 1.5 * Math.sin(Math.PI * ((float) (System.currentTimeMillis() / 20d % 200d)) / 100f) / 50f, 0.405);
        matrixStack.scale(0.75f, 0.75f, 0.75f);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));

        MinecraftClient.getInstance().getItemRenderer().renderItem(item, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV, itemModel);

        matrixStack.pop();

        if (blockEntity.getWorld().random.nextDouble() <= 0.95) return;

        BlockPos pos = blockEntity.getPos();
        ParticleEffect particle = blockEntity.onCooldown() ? ParticleTypes.SMOKE : ParticleTypes.WITCH;

        blockEntity.getWorld().addParticle(particle, pos.getX() + 0.5, pos.getY() + blockEntity.getItemHeight(), pos.getZ() + 0.5, 0, 0, 0);

    }
}
