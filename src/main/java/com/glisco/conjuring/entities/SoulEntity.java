package com.glisco.conjuring.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.Packet;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class SoulEntity extends ProjectileEntity {

    protected int maxAge = 60;

    protected SoulEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return EntityCreatePacket.create(this);
    }

    @Override
    public void setProperties(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        super.setProperties(user, pitch, yaw, roll, modifierZ, modifierXYZ);

        //Offset so its not in your face
        Vec3d newPos = this.getPos().add(this.getVelocity().multiply(0.5));
        this.setPos(newPos.x, newPos.y, newPos.z);
    }

    @Override
    public void tick() {
        super.tick();

        HitResult hitResult = ProjectileUtil.getCollision(this, this::method_26958);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
        }

        if (age > maxAge) this.remove();

        Vec3d newPos = this.getPos().add(this.getVelocity());
        this.updatePosition(newPos.getX(), newPos.getY(), newPos.getZ());
    }
}

