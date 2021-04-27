package com.glisco.conjuring.entities;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoulProjectileEntity extends SoulEntity {

    private float damage = 1.5f;

    public SoulProjectileEntity(World world, LivingEntity owner) {
        super(ConjuringCommon.SOUL_PROJECTILE, world);
        setOwner(owner);
    }

    public SoulProjectileEntity(EntityType<SoulProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putFloat("Damage", damage);
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        damage = tag.getFloat("Damage");
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!(entityHitResult.getEntity() instanceof LivingEntity) || entityHitResult.getEntity() instanceof EnderDragonEntity || entityHitResult.getEntity() instanceof WitherEntity || entityHitResult.getEntity() instanceof PlayerEntity)
            return;
        this.remove();

        LivingEntity e = (LivingEntity) entityHitResult.getEntity();

        if (e.getHealth() - damage <= 0) {
            e.dropItem(ConjuringCommon.CONJURATION_ESSENCE);
            e.world.syncWorldEvent(9005, entityHitResult.getEntity().getBlockPos(), 0);

            if (!e.world.isClient) {
                BlockPos pos = e.getBlockPos();
                World world = e.world;

                world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 2, 0);
            }
        }

        e.damage(createDamageSource(), damage);
    }

    public void setDamage(float damage) {
        this.damage = damage;
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
