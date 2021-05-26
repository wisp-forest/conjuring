package com.glisco.conjuringforgery.entities;

import com.glisco.owo.client.ClientParticles;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.sun.javafx.geom.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class SoulEntityRenderer extends EntityRenderer<SoulEntity> {

    public SoulEntityRenderer(EntityRendererManager dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SoulEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        RedstoneParticleData dust = new RedstoneParticleData(0.5f, 1f, 1f, 0.75f);

        World world = Minecraft.getInstance().world;
        boolean allParticles = Minecraft.getInstance().gameSettings.particles == ParticleStatus.ALL;

        double lastX = entityIn.prevPosX + world.rand.nextDouble() * 0.3 - 0.15;
        double lastY = entityIn.prevPosY + world.rand.nextDouble() * 0.3 - 0.15;
        double lastZ = entityIn.prevPosZ + world.rand.nextDouble() * 0.3 - 0.15;

        double targetX = entityIn.getPosX();
        double targetY = entityIn.getPosY();
        double targetZ = entityIn.getPosZ();

        Vector3d last = new Vector3d(lastX, lastY, lastZ);
        Vector3d current = new Vector3d(targetX, targetY, targetZ);

        Vector3d direction = current.subtract(last);
        Vector3d increment = direction.scale(1 / (float) Math.round(direction.length() * 4));

        Vector3d currentRenderPosition = last.add(increment);
        for (int j = 0; j < Math.round(direction.length() * 4); j++) {

            ClientParticles.spawnWithMaxAge(ParticleTypes.SOUL_FIRE_FLAME, world, currentRenderPosition, world.rand.nextInt((10 + entityIn.ticksExisted)));
            if (allParticles) ClientParticles.spawn(dust, world, currentRenderPosition, 0.35);

            currentRenderPosition = currentRenderPosition.add(increment);
        }

    }

    @Override
    public ResourceLocation getEntityTexture(SoulEntity entity) {
        return new ResourceLocation("");
    }
}
