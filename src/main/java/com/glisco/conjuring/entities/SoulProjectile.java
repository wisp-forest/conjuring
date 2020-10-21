package com.glisco.conjuring.entities;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.WorldHelper;
import net.minecraft.client.particle.EnchantGlyphParticle;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoulProjectile extends ThrownItemEntity {

    public SoulProjectile(World world, double x, double y, double z, LivingEntity owner) {
        super(ConjuringCommon.SOUL_PROJECTILE, x, y, z, world);
        this.setNoGravity(true);
        this.setOwner(owner);
    }

    public SoulProjectile(EntityType<SoulProjectile> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
    }

    //This is just here so IntelliJ leaves me alone. It's probably not used. Probably
    public SoulProjectile(World world){ super(ConjuringCommon.SOUL_PROJECTILE, world); }

    @Override
    protected Item getDefaultItem() {
        return ConjuringCommon.SOUL_ROD;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return EntityCreatePacket.create(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isClient()) {
            for (int i = 0; i < 5; i++) {
                double x = getX() + world.random.nextDouble() * 0.3 - 0.15;
                double y = getY() + world.random.nextDouble() * 0.3 - 0.15;
                double z = getZ() + world.random.nextDouble() * 0.3 - 0.15;
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, 0, 0);
                world.addParticle(ParticleTypes.SOUL, getX(), getY(), getZ(), 0, 0, 0);
            }
        }
        if (age > 60) this.remove();

    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!(entityHitResult.getEntity() instanceof LivingEntity) || entityHitResult.getEntity() instanceof EnderDragonEntity || entityHitResult.getEntity() instanceof WitherEntity || entityHitResult.getEntity() instanceof PlayerEntity)
            return;
        this.remove();

        LivingEntity e = (LivingEntity) entityHitResult.getEntity();

        if (e.getHealth() - 1.5f <= 0) {
            e.dropItem(ConjuringCommon.CONJURATION_ESSENCE);
            e.world.syncWorldEvent(9005, entityHitResult.getEntity().getBlockPos(), 0);

            if (!e.world.isClient) {
                BlockPos pos = e.getBlockPos();
                World world = e.world;

                WorldHelper.playSound(world, pos, 40, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 2, 0);
            }
        }

        e.damage(createDamageSource(), 1.5f);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.remove();
    }

    public DamageSource createDamageSource() {
        return new ProjectileDamageSource("soul_projectile", this, getOwner()).setProjectile();
    }
}

