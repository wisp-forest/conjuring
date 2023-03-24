package com.glisco.conjuring.entities;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.items.ConjuringItems;
import com.glisco.conjuring.util.ConjuringParticleEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;

public class SoulProjectileEntity extends SoulEntity {

    private static final RegistryKey<DamageType> DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Conjuring.id("soul_projectile"));

    private float damage = 1.5f;
    private static final HashMap<SoulProjectileEntity, Entity> TARGET_ENTITIES = new HashMap<>();
    private final TargetPredicate UNIQUE_CLOSEST;

    public SoulProjectileEntity(World world, LivingEntity owner) {
        super(Conjuring.SOUL_PROJECTILE, world);
        setOwner(owner);
        UNIQUE_CLOSEST = TargetPredicate.createAttackable().setBaseMaxDistance(8).setPredicate(livingEntity -> livingEntity.isAlive() && (!TARGET_ENTITIES.containsValue(livingEntity) || TARGET_ENTITIES.get(this) == livingEntity));
    }

    public SoulProjectileEntity(EntityType<SoulProjectileEntity> entityType, World world) {
        super(entityType, world);
        UNIQUE_CLOSEST = TargetPredicate.createAttackable().setBaseMaxDistance(8).setPredicate(livingEntity -> livingEntity.isAlive() && (!TARGET_ENTITIES.containsValue(livingEntity) || TARGET_ENTITIES.get(this) == livingEntity));
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putFloat("Damage", damage);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        damage = tag.getFloat("Damage");
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!(entityHitResult.getEntity() instanceof LivingEntity e) || entityHitResult.getEntity() instanceof EnderDragonEntity || entityHitResult.getEntity() instanceof WitherEntity || entityHitResult.getEntity() instanceof PlayerEntity)
            return;
        this.remove(RemovalReason.KILLED);

        e.damage(createDamageSource(), damage);

        if (!e.isAlive() && damage == 1.5f) {
            e.dropItem(ConjuringItems.CONJURATION_ESSENCE);
            ConjuringParticleEvents.EXTRACTION_RITUAL_FINISHED.spawn(world, Vec3d.of(entityHitResult.getEntity().getBlockPos()), true);

            if (!e.world.isClient) {
                BlockPos pos = e.getBlockPos();
                World world = e.world;

                world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 0.5f, 0);
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        TARGET_ENTITIES.remove(this);
    }

    @Override
    public void tick() {
        Entity closest = world.getClosestEntity(MobEntity.class, UNIQUE_CLOSEST, null, getX(), getY(), getZ(), getBoundingBox().expand(3, 2, 3));
        if (closest == null && TARGET_ENTITIES.containsKey(this)) closest = TARGET_ENTITIES.get(this);
        if (closest != null) {
            Vec3d targetVector = closest.getPos().add(0, closest.getHeight() * 0.5, 0).subtract(getPos());
            setVelocity(targetVector.multiply(0.25f));
            TARGET_ENTITIES.put(this, closest);
        }

        super.tick();
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.remove(RemovalReason.KILLED);
    }

    public DamageSource createDamageSource() {
        return new DamageSource(this.world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getEntry(DAMAGE_TYPE).get(), this.getOwner());
    }

}
