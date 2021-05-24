package com.glisco.conjuringforgery.entities;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.WorldHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class SoulProjectile extends ProjectileItemEntity {

    public SoulProjectile(World world, double x, double y, double z, LivingEntity owner) {
        super(ConjuringForgery.SOUL_PROJECTILE.get(), x, y, z, world);
        this.setNoGravity(true);
        this.setShooter(owner);
    }

    //This is just here so Lint leaves me alone. It's probably not used. Probably
    public SoulProjectile(World world) {
        super(ConjuringForgery.SOUL_PROJECTILE.get(), world);
    }

    public SoulProjectile(EntityType<SoulProjectile> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
    }

    @Override
    protected Item getDefaultItem() {
        return ConjuringForgery.SOUL_ROD.get();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isRemote()) {
            for (int i = 0; i < 5; i++) {
                double x = getPosX() + world.rand.nextDouble() * 0.3 - 0.15;
                double y = getPosY() + world.rand.nextDouble() * 0.3 - 0.15;
                double z = getPosZ() + world.rand.nextDouble() * 0.3 - 0.15;
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, 0, 0);
                world.addParticle(ParticleTypes.SOUL, getPosX(), getPosY(), getPosZ(), 0, 0, 0);
            }
        }
        if (ticksExisted > 60) this.remove();

    }

    @Override
    protected void onEntityHit(EntityRayTraceResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!(entityHitResult.getEntity() instanceof LivingEntity) || entityHitResult.getEntity() instanceof EnderDragonEntity || entityHitResult.getEntity() instanceof WitherEntity || entityHitResult.getEntity() instanceof PlayerEntity)
            return;
        this.remove();

        LivingEntity e = (LivingEntity) entityHitResult.getEntity();

        if (e.getHealth() - 1.5f <= 0) {
            e.entityDropItem(ConjuringForgery.CONJURATION_ESSENCE.get());
            e.world.playEvent(9005, entityHitResult.getEntity().getPosition(), 0);

            if (!e.world.isRemote) {
                BlockPos pos = e.getPosition();
                World world = e.world;

                WorldHelper.playSound(world, pos, 40, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 2, 0);
            }
        }

        e.attackEntityFrom(createDamageSource(), 1.5f);
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        super.func_230299_a_(p_230299_1_);
        this.remove();
    }

    public DamageSource createDamageSource() {
        return new IndirectEntityDamageSource("soul_projectile", this, func_234616_v_()).setProjectile();
    }
}

