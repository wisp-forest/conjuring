package com.glisco.conjuring.client;

import com.glisco.conjuring.blocks.soul_weaver.SoulWeaverBlockEntity;
import com.glisco.owo.client.ClientParticles;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class SoulWeaverBlockEntityRenderer extends BlockEntityRenderer<SoulWeaverBlockEntity> {

    public SoulWeaverBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void render(SoulWeaverBlockEntity blockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        if (blockEntity.getItem() != null) {

            ItemStack item = blockEntity.getItem();
            BakedModel itemModel = MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(item, null, null);

            int lightAbove = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(blockEntity.getWorld()), blockEntity.getPos().up());

            matrixStack.push();

            double scale = blockEntity.getShakeScaleFactor();

            double xOffset = (blockEntity.getWorld().random.nextDouble() - 0.5d) * scale;
            double yOffset = (blockEntity.getWorld().random.nextDouble() - 0.5d) * scale;
            double zOffset = (blockEntity.getWorld().random.nextDouble() - 0.5d) * scale;

            matrixStack.translate(0.5 + xOffset, 1.25 + yOffset + scale, 0.5 + zOffset);
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) (System.currentTimeMillis() / (60d) % 360d)));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) Math.pow(scale * 100, 3)));
            MinecraftClient.getInstance().getItemRenderer().renderItem(item, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumerProvider, lightAbove, OverlayTexture.DEFAULT_UV, itemModel);
            matrixStack.pop();
        }

        if (blockEntity.isLit()) {
            if (blockEntity.getWorld().random.nextDouble() > 0.85f) {

                ClientParticles.setParticleCount(2);
                ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.SOUL_FIRE_FLAME, blockEntity.getWorld(), blockEntity.getPos(), new Vec3d(0.5, 0.35, 0.5), 0.15);
            }
        }

    }
}
