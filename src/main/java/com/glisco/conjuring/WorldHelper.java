package com.glisco.conjuring;

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class WorldHelper {

    public static void spawnParticle(ParticleEffect particle, World world, BlockPos pos, float offsetX, float offsetY, float offsetZ) {
        world.addParticle(particle, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, 0, 0, 0);
    }

    public static void spawnParticle(ParticleEffect particle, World world, BlockPos pos, float offsetX, float offsetY, float offsetZ, float velocityX, float velocityY, float velocityZ) {
        world.addParticle(particle, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, velocityX, velocityY, velocityZ);
    }

    public static void spawnParticle(ParticleEffect particle, World world, BlockPos pos, float offsetX, float offsetY, float offsetZ, float deviation) {
        Random r = world.getRandom();

        double x = pos.getX() + offsetX + (r.nextDouble() - 0.5) * deviation;
        double y = pos.getY() + offsetY + (r.nextDouble() - 0.5) * deviation;
        double z = pos.getZ() + offsetZ + (r.nextDouble() - 0.5) * deviation;

        world.addParticle(particle, x, y, z, 0, 0, 0);
    }

    public static void spawnParticle(ParticleEffect particle, World world, BlockPos pos, float offsetX, float offsetY, float offsetZ, float velocityX, float velocityY, float velocityZ, float deviation) {
        Random r = world.getRandom();

        double x = pos.getX() + offsetX + (r.nextDouble() - 0.5) * deviation;
        double y = pos.getY() + offsetY + (r.nextDouble() - 0.5) * deviation;
        double z = pos.getZ() + offsetZ + (r.nextDouble() - 0.5) * deviation;

        world.addParticle(particle, x, y, z, velocityX, velocityY, velocityZ);
    }

    public static void spawnParticle(ParticleEffect particle, World world, BlockPos pos, float offsetX, float offsetY, float offsetZ, float velocityX, float velocityY, float velocityZ, float deviationX, float deviationY, float deviationZ) {
        Random r = world.getRandom();

        double x = pos.getX() + offsetX + (r.nextDouble() - 0.5) * deviationX;
        double y = pos.getY() + offsetY + (r.nextDouble() - 0.5) * deviationY;
        double z = pos.getZ() + offsetZ + (r.nextDouble() - 0.5) * deviationZ;

        world.addParticle(particle, x, y, z, velocityX, velocityY, velocityZ);
    }

    public static void playSound(World world, BlockPos pos, double range, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        world.getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), range, world.getRegistryKey(), new PlaySoundS2CPacket(sound, category, pos.getX(), pos.getY(), pos.getZ(), volume, pitch));
    }
}
