package com.glisco.conjuringforgery.mixin;

import com.glisco.conjuringforgery.WorldHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(WorldRenderer.class)
public class SpawnerParticleMixin {

    @Inject(method = "playEvent", at = @At("TAIL"))
    public void processCustomWorldEvent(PlayerEntity source, int eventId, BlockPos pos, int data, CallbackInfo ci) {
        Random random = Minecraft.getInstance().world.rand;
        int i;
        double x;
        double y;
        double z;
        double vx;
        double vy;
        double vz;

        if (eventId == 9004) {
            for (i = 0; i < 20; ++i) {
                x = (double) pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
                y = (double) pos.getY() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
                z = (double) pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
                Minecraft.getInstance().world.addParticle(ParticleTypes.ENCHANTED_HIT, x, y, z, 0.0D, 1.0D, 0.0D);
                Minecraft.getInstance().world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
            }
        } else if (eventId == 9005) {
            IParticleData particle = data == 0 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.LAVA;

            for (i = 0; i < 60; ++i) {
                x = (double) pos.getX() + 0.5 + (random.nextDouble() - 0.5D) * 1.5D;
                y = (double) pos.getY() + 0.5 + (random.nextDouble() - 0.5D) * 1.5D;
                z = (double) pos.getZ() + 0.5 + (random.nextDouble() - 0.5D) * 1.5D;

                vx = (random.nextDouble() - 0.5D) * 0.25d;
                vy = (random.nextDouble() - 0.2D) * 0.25d;
                vz = (random.nextDouble() - 0.5D) * 0.25d;

                Minecraft.getInstance().world.addParticle(particle, x, y, z, vx, vy, vz);
                Minecraft.getInstance().world.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, 0, 0, 0);
            }
        } else if (eventId == 9001) {
            if (random.nextDouble() > 0.90) {
                x = (double) pos.getX() + 0.5 + (random.nextDouble() - 0.5D) * 0.45d;
                y = (double) pos.getY() + 0.75;
                z = (double) pos.getZ() + 0.5 + (random.nextDouble() - 0.5D) * 0.45d;
                Minecraft.getInstance().world.addParticle(ParticleTypes.SOUL, x, y, z, 0.0D, 0.01D, 0.0D);
            }
        } else if (eventId == 9007) {
            SoundEvent sound = data == 0 ? SoundEvents.ITEM_TOTEM_USE : SoundEvents.ENTITY_WITHER_HURT;
            Minecraft.getInstance().world.playSound(pos.getX(), pos.getY(), pos.getZ(), sound, SoundCategory.BLOCKS, 1, 0, false);
        } else if (eventId == 9010) {

            float offsetX = 0.5f + data / 8f;
            float offsetY = 0.35f;

            for (i = 0; i < 20; i++) {
                WorldHelper.spawnParticle(ParticleTypes.SMOKE, Minecraft.getInstance().world, pos, offsetX, offsetY, 0.5f, 0, 0, 0, 0, 0.1f, data / 12f);
            }
        } else if (eventId == 9011) {

            float offsetZ = 0.5f + data / 8f;
            float offsetY = 0.35f;

            for (i = 0; i < 20; i++) {
                WorldHelper.spawnParticle(ParticleTypes.SMOKE, Minecraft.getInstance().world, pos, 0.5f, offsetY, offsetZ, 0, 0, 0, data / 12f, 0.1f, 0);
            }
        }
    }

}
