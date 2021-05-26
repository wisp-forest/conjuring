package com.glisco.conjuringforgery.entities;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.items.soul_alloy_tools.BlockCrawler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class SoulFellerEntity extends SoulEntity {

    private int maxBlocks = 8;
    private static final DataParameter<ItemStack> STACK;

    static {
        STACK = EntityDataManager.createKey(SoulFellerEntity.class, DataSerializers.ITEMSTACK);
    }

    public SoulFellerEntity(World world, LivingEntity owner) {
        super(ConjuringForgery.SOUL_FELLER.get(), world);
        setShooter(owner);
    }

    public SoulFellerEntity(EntityType<SoulFellerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerData() {
        this.getDataManager().register(STACK, ItemStack.EMPTY);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.put("Item", getDataManager().get(STACK).write(new CompoundNBT()));
    }

    @Override
    protected void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        ItemStack stack = ItemStack.read(tag.getCompound("Item"));
        this.setItem(stack.copy());
    }

    public void setItem(ItemStack stack) {
        this.getDataManager().set(STACK, stack);
    }

    @Override
    public void setDirectionAndMovement(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        super.setDirectionAndMovement(user, pitch, yaw, roll, modifierZ, modifierXYZ);
        this.setMotion(this.getMotion().scale(0.65f));
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult blockHitResult) {
        if (BlockTags.LOGS.contains(world.getBlockState(blockHitResult.getPos()).getBlock())) {
            BlockCrawler.crawl(world, blockHitResult.getPos(), getDataManager().get(STACK), maxBlocks);
        }
        this.remove();
    }

    public void setMaxBlocks(int maxBlocks) {
        this.maxBlocks = maxBlocks;
    }

}
