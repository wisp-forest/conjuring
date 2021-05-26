package com.glisco.conjuringforgery.client;

import com.glisco.conjuringforgery.blocks.gem_tinkerer.GemTinkererBlockEntity;
import com.glisco.owo.client.ClientParticles;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class GemTinkererBlockEntityRenderer extends TileEntityRenderer<GemTinkererBlockEntity> {

    public static final RenderMaterial MODEL_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("conjuring", "block/gem_tinkerer"));

    private static final ModelRenderer columnModel = new ModelRenderer(32, 32, 0, 0);
    private static final ModelRenderer mainModel = new ModelRenderer(32, 32, 8, 0);

    static {
        columnModel.addBox(0, 0, 0, 2, 6, 2);
        mainModel.addBox(0, 0, 0, 4, 12, 4);
    }

    private static final double twoPi = Math.PI * 2;
    private double scalar = 800;

    public GemTinkererBlockEntityRenderer(TileEntityRendererDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void render(GemTinkererBlockEntity blockEntity, float tickDelta, MatrixStack matrixStack, IRenderTypeBuffer vertexConsumerProvider, int i, int j) {

        scalar = blockEntity.getScalar();

        final World world = blockEntity.getWorld();
        final BlockPos pos = blockEntity.getPos();
        IVertexBuilder vertexConsumer = MODEL_TEXTURE.getBuffer(vertexConsumerProvider, RenderType::getEntitySolid);

        NonNullList<ItemStack> items = blockEntity.getInventory();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        float itemRotation = (float) (System.currentTimeMillis() / 30d % 360);
        final float scaledRotation = (float) (itemRotation + Math.pow(scalar * 2, 1.5));
        final boolean particles = blockEntity.particles();

        // ---

        matrixStack.push();

        matrixStack.translate(0.375, 0.2 + getHeight(0) * 0.25, 0.375);
        mainModel.render(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        matrixStack.translate(0.125, 0.85, 0.125);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(itemRotation));
        itemRenderer.renderItem(items.get(0), ItemCameraTransforms.TransformType.GROUND, i, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumerProvider);

        if (particles) {
            ClientParticles.setParticleCount(20);
            ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.LAVA, world, pos, new Vector3d(0.5, 1.05 + getHeight(0), 0.5), 0.15);
        }

        ClientParticles.setParticleCount(5);
        ClientParticles.persist();

        matrixStack.pop();

        // ---

        matrixStack.push();

        matrixStack.translate(0.1, 0.5 + getHeight(0.5 * twoPi), 0.45);
        columnModel.render(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        matrixStack.translate(0.0625, 0.425, 0.0625);
        matrixStack.scale(0.25f, 0.25f, 0.25f);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(scaledRotation));
        itemRenderer.renderItem(items.get(1), ItemCameraTransforms.TransformType.GROUND, i, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumerProvider);

        if (particles && !items.get(1).isEmpty()) spawnItemParticles(world, pos, 0.1, 0.45, 0.5 * twoPi);

        matrixStack.pop();

        // ---

        matrixStack.push();

        matrixStack.translate(0.45, 0.5 + getHeight(1.5 * twoPi), 0.1);
        columnModel.render(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        matrixStack.translate(0.0625, 0.425, 0.0625);
        matrixStack.scale(0.25f, 0.25f, 0.25f);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(scaledRotation));
        itemRenderer.renderItem(items.get(2), ItemCameraTransforms.TransformType.GROUND, i, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumerProvider);

        if (particles && !items.get(2).isEmpty()) spawnItemParticles(world, pos, 0.45, 0.1, 1.5 * twoPi);

        matrixStack.pop();

        // ---

        matrixStack.push();

        matrixStack.translate(0.45, 0.5 + getHeight(twoPi), 0.8);
        columnModel.render(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        matrixStack.translate(0.0625, 0.425, 0.0625);
        matrixStack.scale(0.25f, 0.25f, 0.25f);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(scaledRotation));
        itemRenderer.renderItem(items.get(3), ItemCameraTransforms.TransformType.GROUND, i, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumerProvider);

        if (particles && !items.get(3).isEmpty()) spawnItemParticles(world, pos, 0.45, 0.8, twoPi);

        matrixStack.pop();

        // ---

        matrixStack.push();

        matrixStack.translate(0.8, 0.5 + getHeight(0), 0.45);
        columnModel.render(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        matrixStack.translate(0.0625, 0.425, 0.0625);
        matrixStack.scale(0.25f, 0.25f, 0.25f);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(scaledRotation));
        itemRenderer.renderItem(items.get(4), ItemCameraTransforms.TransformType.GROUND, i, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumerProvider);

        if (particles && !items.get(4).isEmpty()) spawnItemParticles(world, pos, 0.8, 0.45, 0);

        matrixStack.pop();

        ClientParticles.reset();

    }

    private double getHeight(double offset) {
        return Math.sin((System.currentTimeMillis() / 800d + offset * twoPi) % (twoPi)) * 0.01 * scalar;
    }

    private void spawnItemParticles(World world, BlockPos pos, double x, double z, double offset) {
        ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.SOUL_FIRE_FLAME, world, pos, new Vector3d(x + 0.0625, 1 + getHeight(offset), z + 0.0625), 0.1);
    }
}

