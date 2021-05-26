package com.glisco.conjuringforgery.entities;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.HashMap;

public class SoulProjectileEntity extends SoulEntity {

    private float damage = 1.5f;
    private static final HashMap<SoulProjectileEntity, Entity> TARGET_ENTITIES = new HashMap<>();
    private final EntityPredicate UNIQUE_CLOSEST;

    public SoulProjectileEntity(World world, LivingEntity owner) {
        super(ConjuringForgery.SOUL_PROJECTILE.get(), world);
        setShooter(owner);
        UNIQUE_CLOSEST = new EntityPredicate().setDistance(8).setCustomPredicate(livingEntity -> livingEntity.isAlive() && (!TARGET_ENTITIES.containsValue(livingEntity) || TARGET_ENTITIES.get(this) == livingEntity));
    }

    public SoulProjectileEntity(EntityType<SoulProjectileEntity> entityType, World world) {
        super(entityType, world);
        UNIQUE_CLOSEST = new EntityPredicate().setDistance(8).setCustomPredicate(livingEntity -> livingEntity.isAlive() && (!TARGET_ENTITIES.containsValue(livingEntity) || TARGET_ENTITIES.get(this) == livingEntity));
    }

    @Override
    protected void registerData() {

    }

    @Override
    protected void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        tag.putFloat("Damage", damage);
    }

    @Override
    protected void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        damage = tag.getFloat("Damage");
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!(entityHitResult.getEntity() instanceof LivingEntity) || entityHitResult.getEntity() instanceof EnderDragonEntity || entityHitResult.getEntity() instanceof WitherEntity || entityHitResult.getEntity() instanceof PlayerEntity)
            return;
        this.remove();

        LivingEntity e = (LivingEntity) entityHitResult.getEntity();

        e.attackEntityFrom(createDamageSource(), damage);

        if (!e.isAlive() && damage == 1.5f) {
            e.entityDropItem(ConjuringForgery.CONJURATION_ESSENCE.get());
            e.world.playEvent(9005, entityHitResult.getEntity().getPosition(), 0);

            if (!e.world.isRemote) {
                BlockPos pos = e.getPosition();
                World world = e.world;

                world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 0.5f, 0);
            }
        }
    }

    @Override
    public void remove() {
        super.remove();
        TARGET_ENTITIES.remove(this);
    }

    @Override
    public void tick() {
        Entity closest = world.getClosestEntity(MobEntity.class, UNIQUE_CLOSEST, null, getPosX(), getPosY(), getPosZ(), getBoundingBox().grow(3, 2, 3));
        if(closest == null && TARGET_ENTITIES.containsKey(this)) closest = TARGET_ENTITIES.get(this);
        if (closest != null) {
            Vector3d targetVector = closest.getPositionVec().add(0, closest.getHeight() * 0.5, 0).subtract(getPositionVec());
            setMotion(targetVector.scale(0.25f));
            TARGET_ENTITIES.put(this, closest);
        }

        super.tick();
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult blockHitResult) {
        super.func_230299_a_(blockHitResult);
        this.remove();
    }

    public DamageSource createDamageSource() {
        return new IndirectEntityDamageSource("soul_projectile", this, getShooter()).setProjectile();
    }

}
