package com.glisco.conjuring.client.ber;

import com.glisco.conjuring.blocks.BlackstonePedestalBlockEntity;
import com.glisco.owo.particles.ClientParticles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Objects;

public class BlackstonePedestalBlockEntityRenderer implements BlockEntityRenderer<BlackstonePedestalBlockEntity> {

    public BlackstonePedestalBlockEntityRenderer(BlockEntityRendererFactory.Context context) {}

    public void render(BlackstonePedestalBlockEntity blockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        if (!blockEntity.getItem().isEmpty()) {

            ItemStack item = blockEntity.getItem();
            BakedModel itemModel = MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(item, null, null, 0);

            int lightAbove = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(blockEntity.getWorld()), blockEntity.getPos().up());

            matrixStack.push();
            matrixStack.translate(0.5, 1.25, 0.5);
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) (System.currentTimeMillis() / 20d % 360d)));
            MinecraftClient.getInstance().getItemRenderer().renderItem(item, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumerProvider, lightAbove, OverlayTexture.DEFAULT_UV, itemModel);
            matrixStack.pop();

        }

        if (!blockEntity.getItem().isEmpty() || blockEntity.isActive()) {
            if (blockEntity.getWorld().random.nextDouble() > (blockEntity.isActive() ? 0.85 : 0.95)) {
                BlockPos pos = blockEntity.getPos();
                ParticleEffect particle = blockEntity.isActive() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMOKE;

                ClientParticles.spawnWithOffsetFromBlock(particle, blockEntity.getWorld(), pos, new Vec3d(0.5, 1.35, 0.5), 0.25);
            }
        }

    }
}
