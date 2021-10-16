package com.glisco.conjuring.entities;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.items.soul_alloy_tools.BlockCrawler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoulDiggerEntity extends SoulEntity {

    private static final TrackedData<ItemStack> STACK;

    static {
        STACK = DataTracker.registerData(SoulDiggerEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }

    public SoulDiggerEntity(World world, LivingEntity owner) {
        super(Conjuring.SOUL_DIGGER, world);
        setOwner(owner);
    }

    public SoulDiggerEntity(EntityType<SoulDiggerEntity> entityType, World world) {
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
    public void setVelocity(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        super.setVelocity(user, pitch, yaw, roll, modifierZ, modifierXYZ);
        this.setVelocity(this.getVelocity().multiply(0.65f));
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {

        if (getOwner() == null) return;

        BlockPos pos = blockHitResult.getBlockPos();

        if (world.getBlockState(pos).getBlock().getClass().getSimpleName().contains("OreBlock")) {
            BlockCrawler.crawl(world, pos, getDataTracker().get(STACK), Conjuring.CONFIG.tools_config.pickaxe_veinmine_max_blocks);
        }

        this.remove(RemovalReason.KILLED);
    }

}
