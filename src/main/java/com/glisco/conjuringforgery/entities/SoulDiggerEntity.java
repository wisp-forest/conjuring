package com.glisco.conjuringforgery.entities;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.items.soul_alloy_tools.BlockCrawler;
import net.minecraft.block.OreBlock;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class SoulDiggerEntity extends SoulEntity {

    private static final DataParameter<ItemStack> STACK;

    static {
        STACK = EntityDataManager.createKey(SoulDiggerEntity.class, DataSerializers.ITEMSTACK);
    }

    public SoulDiggerEntity(World world, LivingEntity owner) {
        super(ConjuringForgery.SOUL_DIGGER.get(), world);
        setShooter(owner);
    }

    public SoulDiggerEntity(EntityType<SoulDiggerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerData() {
        this.getDataManager().register(STACK, ItemStack.EMPTY);
    }

    @Override
    protected void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        tag.put("Item", getDataManager().get(STACK).write(new CompoundNBT()));
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

        if (getShooter() == null) return;

        BlockPos pos = blockHitResult.getPos();

        if (world.getBlockState(pos).getBlock() instanceof OreBlock || world.getBlockState(pos).getBlock() instanceof RedstoneOreBlock) {
            BlockCrawler.crawl(world, pos, getDataManager().get(STACK), ConjuringForgery.CONFIG.tools_config.pickaxe_veinmine_max_blocks);
        }

        this.remove();
    }

}
