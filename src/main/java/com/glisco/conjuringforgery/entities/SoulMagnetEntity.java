package com.glisco.conjuringforgery.entities;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.owo.client.ClientParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class SoulMagnetEntity extends SoulEntity {

    int ticksInBlock = 0;
    boolean recalled = false;

    public SoulMagnetEntity(World world, LivingEntity owner) {
        super(ConjuringForgery.SOUL_MAGNET.get(), world);
        setShooter(owner);
        maxAge = 100;
    }

    public SoulMagnetEntity(EntityType<SoulMagnetEntity> entityType, World world) {
        super(entityType, world);
        maxAge = 100;
    }

    @Override
    protected void registerData() {

    }

    @Override
    public void tick() {

        if ((recalled || ticksExisted > 40) && getShooter() != null) {
            Vector3d ownerVector = getShooter().getPositionVec().add(0, 0.75, 0).subtract(getPositionVec());
            if (ownerVector.length() < 1) this.remove();

            double scalar = ownerVector.length() > 3 ? 0.075 : 0.15d;
            setMotion(ownerVector.scale(scalar));
        }

        super.tick();

        if (world.getBlockState(getPosition()).isNormalCube(world, getPosition())) {
            ticksInBlock++;
        } else {
            ticksInBlock = 0;
        }

        int range = ConjuringForgery.CONFIG.tools_config.shovel_magnet_range;
        AxisAlignedBB box = new AxisAlignedBB(getPositionVec().subtract(range, range, range), getPositionVec().add(range, range, range));

        for (ItemEntity item : world.getEntitiesWithinAABB(EntityType.ITEM, box, ItemEntity::isAlive)) {
            Vector3d difference = getPositionVec().subtract(item.getPositionVec()).scale(0.25d);
            item.setMotion(difference);

            if (world.isRemote && difference.length() > 0.5) {
                RedstoneParticleData dust = new RedstoneParticleData(0.5f, 1f, 1f, 0.5f);
                ClientParticles.setParticleCount(45);
                ClientParticles.spawnLine(dust, world, getPositionVec(), item.getPositionVec(), 0);
            }
        }

        for (ExperienceOrbEntity orb : world.getEntitiesWithinAABB(EntityType.EXPERIENCE_ORB, box, ExperienceOrbEntity::isAlive)) {
            Vector3d difference = getPositionVec().subtract(orb.getPositionVec()).scale(0.25d);
            orb.setMotion(difference);
        }


        if (ticksInBlock > (recalled ? 30 : 8)) {
            if (recalled || ticksExisted > 40) {
                this.remove();
            } else {
                recall();
                ticksInBlock = 0;
            }
        }
    }

    @Override
    public void setDirectionAndMovement(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        super.setDirectionAndMovement(user, pitch, yaw, roll, modifierZ, modifierXYZ);
        this.setMotion(this.getMotion().scale(0.35));
    }

    public void recall() {
        this.recalled = true;
    }
}
