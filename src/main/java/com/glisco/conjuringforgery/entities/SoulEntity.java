package com.glisco.conjuringforgery.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class SoulEntity extends ProjectileEntity {

    protected int maxAge = 60;

    protected SoulEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void setDirectionAndMovement(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        super.setDirectionAndMovement(user, pitch, yaw, roll, modifierZ, modifierXYZ);
        this.setMotion(this.getMotion().subtract(0, user.isOnGround() ? 0.0D : user.getMotion().y, 0));

        //Offset so its not in your face
        Vector3d newPos = this.getPositionVec().add(this.getMotion().scale(0.5));
        this.setPosition(newPos.x, newPos.y, newPos.z);
    }

    @Override
    public void tick() {
        super.tick();

        RayTraceResult hitResult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
        if (hitResult.getType() != RayTraceResult.Type.MISS) {
            this.onImpact(hitResult);
        }

        if (ticksExisted > maxAge) this.remove();

        Vector3d newPos = this.getPositionVec().add(this.getMotion());
        this.setPositionAndUpdate(newPos.getX(), newPos.getY(), newPos.getZ());
    }
}

