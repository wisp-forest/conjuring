package com.glisco.conjuring.entities;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.entity.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SoulMagnetEntity extends SoulEntity {

    int ticksInBlock = 0;
    boolean recalled = false;

    public SoulMagnetEntity(World world, LivingEntity owner) {
        super(ConjuringCommon.SOUL_MAGNET, world);
        setOwner(owner);
        maxAge = 100;
    }

    public SoulMagnetEntity(EntityType<SoulMagnetEntity> entityType, World world) {
        super(entityType, world);
        maxAge = 100;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public void tick() {

        if ((recalled || age > 40) && getOwner() != null) {
            Vec3d ownerVector = getOwner().getPos().add(0, 0.75, 0).subtract(getPos());
            if (ownerVector.length() < 1) this.remove();

            double scalar = ownerVector.length() > 3 ? 0.075 : 0.15d;
            setVelocity(ownerVector.multiply(scalar));
        }

        super.tick();

        if (world.getBlockState(getBlockPos()).isSolidBlock(world, getBlockPos())) {
            ticksInBlock++;
        } else {
            ticksInBlock = 0;
        }

        Box box = new Box(getPos().subtract(4, 4, 4), getPos().add(4, 4, 4));

        for (ItemEntity item : world.getEntitiesByType(EntityType.ITEM, box, ItemEntity::isAlive)) {
            Vec3d difference = getPos().subtract(item.getPos()).multiply(0.25d);
            item.setVelocity(difference);
        }

        for (ExperienceOrbEntity orb : world.getEntitiesByType(EntityType.EXPERIENCE_ORB, box, ExperienceOrbEntity::isAlive)) {
            Vec3d difference = getPos().subtract(orb.getPos()).multiply(0.25d);
            orb.setVelocity(difference);
        }


        if (ticksInBlock > 8) {
            if (recalled || age > 40) {
                this.remove();
            } else {
                recall();
                ticksInBlock = 0;
            }
        }
    }

    @Override
    public void setProperties(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        super.setProperties(user, pitch, yaw, roll, modifierZ, modifierXYZ);
        this.setVelocity(this.getVelocity().multiply(0.35));
    }

    public void recall() {
        this.recalled = true;
    }
}
