package com.glisco.conjuring.entities;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class SoulDiggerEntity extends SoulEntity {

    public SoulDiggerEntity(World world, LivingEntity owner) {
        super(ConjuringCommon.SOUL_DIGGER, world);
        setOwner(owner);
    }

    public SoulDiggerEntity(EntityType<SoulDiggerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public void setProperties(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        super.setProperties(user, pitch, yaw, roll, modifierZ, modifierXYZ);
        this.setVelocity(this.getVelocity().multiply(0.65f));
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        world.breakBlock(blockHitResult.getBlockPos(), true);
    }

}
