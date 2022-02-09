package com.glisco.conjuring.util;

import com.glisco.conjuring.Conjuring;
import io.wispforest.owo.particles.ClientParticles;
import io.wispforest.owo.particles.systems.ParticleSystem;
import io.wispforest.owo.particles.systems.ParticleSystemController;
import io.wispforest.owo.util.VectorRandomUtils;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ConjuringParticleEvents {

    public static final ParticleSystemController CONTROLLER = new ParticleSystemController(Conjuring.id("particles"));

    public static final ParticleSystem<Line> LINE = CONTROLLER.register(Line.class, (world, pos, data) -> {
        ClientParticles.setParticleCount(15);
        ClientParticles.spawnLine(ParticleTypes.ENCHANTED_HIT, world, data.start, data.end, 0.1f);
    });

    public static final ParticleSystem<Void> BREAK_BLOCK = CONTROLLER.register(Void.class, (world, pos, data) -> {
        ClientParticles.setParticleCount(3);
        ClientParticles.spawnCubeOutline(ParticleTypes.SOUL_FIRE_FLAME, world, pos.add(0.175, 0.175, 0.175), 0.65f, 0f);
    });

    public static final ParticleSystem<BlockPos> UNLINK_WEAVER = CONTROLLER.register(BlockPos.class, (world, pos, pedestal) -> {
        ClientParticles.setParticleCount(20);
        ClientParticles.spawnLine(ParticleTypes.SMOKE, world, pos.add(0.5, 0.4, 0.5), Vec3d.of(pedestal).add(0.5, 0.5, 0.5), 0);

        ClientParticles.setParticleCount(30);
        ClientParticles.spawnWithinBlock(ParticleTypes.SMOKE, world, pedestal);
    });

    public static final ParticleSystem<Void> CONJURER_SUMMON = CONTROLLER.register(Void.class, (world, pos, data) -> {
        Vec3d loc = pos.add(.5, .5, .5);

        ClientParticles.setParticleCount(20);
        ClientParticles.spawn(ParticleTypes.ENCHANTED_HIT, world, loc, 2);

        ClientParticles.setParticleCount(20);
        ClientParticles.spawn(ParticleTypes.SOUL_FIRE_FLAME, world, loc, 2);
    });

    public static final ParticleSystem<Boolean> EXTRACTION_RITUAL_FINISHED = CONTROLLER.register(Boolean.class, (world, pos, successful) -> {
        Vec3d loc = pos.add(.5, .5, .5);
        ParticleEffect particle = successful ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.LAVA;

        for (int i = 0; i < 60; ++i) {
            ClientParticles.setVelocity(VectorRandomUtils.getRandomOffset(world, Vec3d.ZERO, .25));
            ClientParticles.spawn(particle, world, loc, 1.5);

            ClientParticles.spawn(ParticleTypes.LARGE_SMOKE, world, loc, 1.5);
        }
    });

    public static final ParticleSystem<Direction> PEDESTAL_REMOVED = CONTROLLER.register(Direction.class, (world, pos, direction) -> {
        final Vec3d loc = pos.add(.5, 0, .5);

        ClientParticles.setParticleCount(20);
        ClientParticles.spawnPrecise(ParticleTypes.SMOKE, world, loc.add(direction.getOffsetX() * .15, .35, direction.getOffsetZ() * .15), direction.getOffsetZ() / 4d, 0.1, direction.getOffsetX() / 4d);
    });

    public static final ParticleSystem<BlockPos> LINK_SOUL_FUNNEL = CONTROLLER.register(BlockPos.class, (world, pos, offset) -> {
        float offsetX = 0.5f + offset.getX() / 8f;
        float offsetY = 0.35f;
        float offsetZ = 0.5f + offset.getZ() / 8f;

        ClientParticles.setParticleCount(20);
        ClientParticles.spawnPrecise(ParticleTypes.WITCH, world, new Vec3d(offsetX, offsetY, offsetZ).add(pos), offset.getZ() / 12d, 0.1f, offset.getX() / 12d);
    });

    public static final ParticleSystem<Void> SOULFIRE_FORGE_SOULS = CONTROLLER.register(Void.class, (world, pos, data) -> {
        ClientParticles.setParticleCount(3);
        ClientParticles.spawnPrecise(ParticleTypes.SOUL, world, pos.add(.5, .75, .5), .45, 0, .45);
    });

    public static void register() {}

    public record Line(Vec3d start, Vec3d end) {}
}
