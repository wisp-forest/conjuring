package com.glisco.conjuring.blocks;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.WorldHelper;
import com.glisco.conjuring.items.ConjuringFocus;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SoulFunnelBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable {

    private ItemStack item;
    private float itemHeight = 0;
    private int slownessCooldown = 0;

    private int ritualTick = 0;
    private boolean ritualRunning = false;
    private int ritualEntity = -69420;
    private final List<BlockPos> pedestalPositions;

    public SoulFunnelBlockEntity() {
        super(ConjuringCommon.SOUL_FUNNEL_BLOCK_ENTITY);
        pedestalPositions = new ArrayList<>();
    }


    //Data Logic
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        CompoundTag item = new CompoundTag();
        if (this.item != null) {
            this.item.toTag(item);
        }
        tag.put("Item", item);
        tag.putInt("Cooldown", slownessCooldown);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        CompoundTag item = tag.getCompound("Item");
        if (!item.isEmpty()) {
            this.item = ItemStack.fromTag(tag.getCompound("Item"));
        }
        slownessCooldown = tag.getInt("Cooldown");
        pedestalPositions.add(pos.add(2, 0, 0));
        pedestalPositions.add(pos.add(0, 0, 2));
        pedestalPositions.add(pos.add(-2, 0, 0));
        pedestalPositions.add(pos.add(0, 0, -2));
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(null, tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (this.world instanceof ServerWorld) {
            this.sync();
        }
    }


    //Tick Logic
    @Override
    public void tick() {
        //Ritual tick logic
        if (ritualRunning) {
            ritualTick++;
            if (ritualTick == 1) {
                PathAwareEntity e = (PathAwareEntity) world.getEntityById(ritualEntity);
                e.setVelocity(0, 0.05, 0);
                e.setNoGravity(true);
                initPedestals();
                if (world.isClient) world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 1, false);
            } else if (ritualTick == 20) {
                world.getEntityById(ritualEntity).setVelocity(0, 0, 0);
            } else if (ritualTick > 20 && ritualTick <= 80) {
                if (world.isClient) {
                    for (BlockPos pos : pedestalPositions) {
                        if (world.getBlockState(pos).getBlock() != ConjuringCommon.BLACKSTONE_PEDESTAL) continue;
                        BlockPos p = pos.add(0, 1, 0);
                        BlockPos pVector = pos.subtract(this.pos);

                        ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, world.getBlockState(pos));
                        WorldHelper.spawnParticle(particle, world, p, 0.5f, 0.25f, 0.5f, 0.1f);

                        WorldHelper.spawnParticle(ParticleTypes.SOUL, world, p, 0.5f, 0.25f, 0.5f, pVector.getX() * -0.05f, 0.05f, pVector.getZ() * -0.05f, 0.1f);
                    }
                    for (int i = 0; i < 5; i++) WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 2.5f, 0.5f, 0, -0.5f, 0f, 0.25f);
                }
                if (ritualTick % 10 == 0) {
                    PathAwareEntity e = (PathAwareEntity) world.getEntityById(ritualEntity);
                    e.damage(DamageSource.OUT_OF_WORLD, 0.01f);
                }
            } else if (ritualTick > 80) {
                PathAwareEntity e = (PathAwareEntity) world.getEntityById(ritualEntity);
                if (!world.isClient()) {
                    world.syncWorldEvent(9005, e.getBlockPos(), 0);
                } else {
                    world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.BLOCKS, 1, 0, false);
                }

                world.setBlockState(pos, world.getBlockState(pos).with(SoulFunnelBlock.FILLED, false));
                this.setItem(null);
                ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1.25, pos.getZ(), ConjuringFocus.create(e.getType()));

                e.kill();
                disablePedestals();
                this.ritualTick = 0;
                this.ritualRunning = false;
                this.ritualEntity = -69420;
            }
        }

        //Item bouncing and slowness logic
        itemHeight = itemHeight >= 100 ? 0 : itemHeight + 1;
        if (slownessCooldown > 0) slownessCooldown--;

        if (slownessCooldown == 0) {
            if (world.getOtherEntities(null, new Box(pos)).isEmpty()) return;

            Entity e = world.getOtherEntities(null, new Box(pos)).get(0);
            if (e instanceof PlayerEntity || e instanceof EnderDragonEntity || e instanceof WitherEntity || !(e instanceof LivingEntity) || e.getScoreboardTags().contains("affected"))
                return;

            ((LivingEntity) e).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 15 * 20, 20));
            slownessCooldown = 30 * 20;
        }
    }


    //Actual Logic
    public void setItem(@Nullable ItemStack item) {
        this.item = item == null ? null : item.copy();
        this.markDirty();
    }

    @Nullable
    public ItemStack getItem() {
        if (item == null) {
            return null;
        }
        return item.copy();
    }

    public void startRitual(int ritualEntity) {
        this.ritualRunning = true;
        this.ritualEntity = ritualEntity;
        this.markDirty();
    }

    public boolean isRitualRunning() {
        return ritualRunning;
    }

    public float getItemHeight() {
        return (float) Math.sin(2 * Math.PI * itemHeight / 100) / 25f;
    }

    public boolean onCooldown() {
        return slownessCooldown > 0;
    }

    private void initPedestals() {
        for (BlockPos p : pedestalPositions) {
            BlockEntity blockEntity = world.getBlockEntity(p);
            if (!(blockEntity instanceof BlackstonePedestalBlockEntity)) continue;

            ((BlackstonePedestalBlockEntity) blockEntity).setActive(true);
            ((BlackstonePedestalBlockEntity) blockEntity).setActiveFunnel(pos);
        }
    }

    private void disablePedestals() {
        for (BlockPos p : pedestalPositions) {
            BlockEntity blockEntity = world.getBlockEntity(p);
            if (!(blockEntity instanceof BlackstonePedestalBlockEntity)) continue;

            ((BlackstonePedestalBlockEntity) blockEntity).setActive(false);
            ((BlackstonePedestalBlockEntity) blockEntity).setActiveFunnel(null);
            ((BlackstonePedestalBlockEntity) blockEntity).setRenderedItem(null);
        }
    }

}
