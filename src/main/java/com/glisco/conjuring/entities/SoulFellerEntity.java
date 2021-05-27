package com.glisco.conjuring.entities;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.items.soul_alloy_tools.BlockCrawler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class SoulFellerEntity extends SoulEntity {

    private int maxBlocks = 8;
    private static final TrackedData<ItemStack> STACK;

    static {
        STACK = DataTracker.registerData(SoulDiggerEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }

    public SoulFellerEntity(World world, LivingEntity owner) {
        super(ConjuringCommon.SOUL_FELLER, world);
        setOwner(owner);
    }

    public SoulFellerEntity(EntityType<SoulFellerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(STACK, ItemStack.EMPTY);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.put("Item", getDataTracker().get(STACK).writeNbt(new NbtCompound()));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        ItemStack stack = ItemStack.fromNbt(tag.getCompound("Item"));
        this.setItem(stack.copy());
    }

    public void setItem(ItemStack stack) {
        this.getDataTracker().set(STACK, stack);
    }

    @Override
    public void setProperties(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        super.setProperties(user, pitch, yaw, roll, modifierZ, modifierXYZ);
        this.setVelocity(this.getVelocity().multiply(0.65f));
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (BlockTags.LOGS.contains(world.getBlockState(blockHitResult.getBlockPos()).getBlock())) {
            BlockCrawler.crawl(world, blockHitResult.getBlockPos(), getDataTracker().get(STACK), maxBlocks);
        }
        this.remove(RemovalReason.KILLED);
    }

    public void setMaxBlocks(int maxBlocks) {
        this.maxBlocks = maxBlocks;
    }

}
