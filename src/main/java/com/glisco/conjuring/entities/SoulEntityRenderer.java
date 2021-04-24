package com.glisco.conjuring.entities;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SoulEntityRenderer extends EntityRenderer<SoulEntity> {

    public SoulEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SoulEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        World world = MinecraftClient.getInstance().world;

        double lastX = entity.lastRenderX + world.random.nextDouble() * 0.3 - 0.15;
        double lastY = entity.lastRenderY + world.random.nextDouble() * 0.3 - 0.15;
        double lastZ = entity.lastRenderZ + world.random.nextDouble() * 0.3 - 0.15;

        double targetX = entity.getX();
        double targetY = entity.getY();
        double targetZ = entity.getZ();

        Vec3d last = new Vec3d(lastX, lastY, lastZ);
        Vec3d current = new Vec3d(targetX, targetY, targetZ);

        Vec3d direction = current.subtract(last);
        Vec3d increment = direction.multiply(1 / (float) Math.round(direction.length() * 4));

        Vec3d currentRenderPosition = last.add(increment);
        for (int j = 0; j < Math.round(direction.length() * 4); j++) {
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, currentRenderPosition.x, currentRenderPosition.y, currentRenderPosition.z, 0, 0, 0);
            currentRenderPosition = currentRenderPosition.add(increment);
        }

    }

    @Override
    public Identifier getTexture(SoulEntity entity) {
        return new Identifier("");
    }
}
