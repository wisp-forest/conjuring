package com.glisco.conjuring;

import com.glisco.conjuring.mixin.ParticleManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
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

    public static void spawnEnchantParticle(World world, BlockPos origin, BlockPos destination, float offsetX, float offsetY, float offsetZ, float deviation) {
        Random r = world.getRandom();
        BlockPos particleVector = origin.subtract(destination);

        double originX = particleVector.getX() + offsetX + (r.nextDouble() - 0.5) * deviation;
        double originY = particleVector.getY() + offsetY + (r.nextDouble() - 0.5) * deviation;
        double originZ = particleVector.getZ() + offsetZ + (r.nextDouble() - 0.5) * deviation;

        world.addParticle(ParticleTypes.ENCHANT, destination.getX() + 0.5f, destination.getY(), destination.getZ() + 0.5f, originX, originY, originZ);
    }

    public static void spawnParticle(ParticleEffect particle, World world, BlockPos pos, float offsetX, float offsetY, float offsetZ, float velocityX, float velocityY, float velocityZ, float deviationX, float deviationY, float deviationZ) {
        Random r = world.getRandom();

        double x = pos.getX() + offsetX + (r.nextDouble() - 0.5) * deviationX;
        double y = pos.getY() + offsetY + (r.nextDouble() - 0.5) * deviationY;
        double z = pos.getZ() + offsetZ + (r.nextDouble() - 0.5) * deviationZ;

        world.addParticle(particle, x, y, z, velocityX, velocityY, velocityZ);
    }

    public static <T extends ParticleEffect> void spawnParticle(T particleType, World world, BlockPos pos, float offsetX, float offsetY, float offsetZ, float velocityX, float velocityY, float velocityZ, int maxAge) {

        ParticleFactory<T> particleFactory = (ParticleFactory<T>) ((ParticleManagerAccessor) MinecraftClient.getInstance().particleManager).getFactories().get(Registry.PARTICLE_TYPE.getRawId(particleType.getType()));

        Particle particle = particleFactory.createParticle(particleType, (ClientWorld) world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, velocityX, velocityY, velocityZ);
        particle.setMaxAge(maxAge);
        MinecraftClient.getInstance().particleManager.addParticle(particle);
    }

    public static void playSound(World world, BlockPos pos, double range, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        world.getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), range, world.getRegistryKey(), new PlaySoundS2CPacket(sound, category, pos.getX(), pos.getY(), pos.getZ(), volume, pitch));
    }

}
