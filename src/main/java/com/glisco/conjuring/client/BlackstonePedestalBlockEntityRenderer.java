package com.glisco.conjuring.client;

import com.glisco.conjuring.WorldHelper;
import com.glisco.conjuring.blocks.BlackstonePedestalBlockEntity;
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

public class BlackstonePedestalBlockEntityRenderer extends BlockEntityRenderer<BlackstonePedestalBlockEntity> {

    public BlackstonePedestalBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void render(BlackstonePedestalBlockEntity blockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        if (!blockEntity.getItem().isEmpty()) {

            ItemStack item = blockEntity.getItem();
            BakedModel itemModel = MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(item, null, null);

            int lightAbove = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(blockEntity.getWorld()), blockEntity.getPos().up());

            matrixStack.push();
            matrixStack.translate(0.5, 1.25, 0.5);
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) (System.currentTimeMillis() / 20d % 360d)));
            MinecraftClient.getInstance().getItemRenderer().renderItem(item, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumerProvider, lightAbove, OverlayTexture.DEFAULT_UV, itemModel);
            matrixStack.pop();

        }

        if (!blockEntity.getItem().isEmpty() || blockEntity.isActive()) {
            if (blockEntity.getWorld().random.nextDouble() > (blockEntity.isActive() ? 0.85 : 0.95)) {
                BlockPos pos = blockEntity.getPos();
                ParticleEffect particle = blockEntity.isActive() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMOKE;

                WorldHelper.spawnParticle(particle, blockEntity.getWorld(), pos, 0.5f, 1.35f, 0.5f, 0.25f);
            }
        }

    }
}
