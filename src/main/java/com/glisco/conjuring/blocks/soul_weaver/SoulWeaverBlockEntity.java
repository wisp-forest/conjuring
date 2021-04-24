package com.glisco.conjuring.blocks.soul_weaver;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.WorldHelper;
import com.glisco.conjuring.blocks.BlackstonePedestalBlockEntity;
import com.glisco.conjuring.blocks.RitualCore;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SoulWeaverBlockEntity extends BlockEntity implements RitualCore, BlockEntityClientSerializable, Tickable {

    List<BlockPos> pedestals = new ArrayList<>();
    private ItemStack item = null;
    private int ritualTick = 0;

    SoulWeaverRecipe cachedRecipe = null;

    public SoulWeaverBlockEntity() {
        super(ConjuringCommon.SOUL_WEAVER_BLOCK_ENTITY);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        loadPedestals(tag, pedestals);
        CompoundTag item = tag.getCompound("Item");
        if (!item.isEmpty()) {
            this.item = ItemStack.fromTag(tag.getCompound("Item"));
        } else {
            this.item = null;
        }
        ritualTick = tag.getInt("RitualTick");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        savePedestals(tag, pedestals);
        CompoundTag itemTag = new CompoundTag();
        if (item != null) item.toTag(itemTag);
        tag.put("Item", itemTag);
        tag.putInt("RitualTick", ritualTick);
        return super.toTag(tag);
    }

    public boolean linkPedestal(BlockPos pedestal) {
        if (pedestals.size() >= 4) return false;

        if (!pedestals.contains(pedestal)) pedestals.add(pedestal);
        if (world.isClient) {
            BlockPos offset = pedestal.subtract(pos);

            float offsetX = 0.5f + offset.getX() / 8f;
            float offsetY = 0.35f;
            float offsetZ = 0.5f + offset.getZ() / 8f;

            for (int i = 0; i < 20; i++) {
                WorldHelper.spawnParticle(ParticleTypes.WITCH, world, pos, offsetX, offsetY, offsetZ, 0, 0, 0, offset.getZ() / 12f, 0.1f, offset.getX() / 12f);
            }
        }
        this.markDirty();
        return true;
    }

    public boolean removePedestal(BlockPos pedestal, boolean pedestalActive) {
        boolean returnValue = pedestals.remove(pedestal);
        this.markDirty();

        BlockPos offset = pedestal.subtract(pos);
        if (offset.getX() != 0) {
            world.syncWorldEvent(9010, pos, offset.getX());
        } else {
            world.syncWorldEvent(9011, pos, offset.getZ());
        }

        return returnValue;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack stack) {
        item = stack;
        markDirty();
    }

    @Override
    public List<BlockPos> getPedestalPositions() {
        return pedestals;
    }

    @Override
    public boolean tryStartRitual() {

        if (!verifyRecipe()) return false;

        ritualTick = 1;
        markDirty();
        return true;
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (!world.isClient()) {
            sync();
        }
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(getCachedState(), compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }

    public boolean verifyRecipe() {

        if (item == null) return false;

        Inventory testInventory = new SimpleInventory(5);
        testInventory.setStack(0, item);

        int index = 1;
        for (BlockPos pedestal : pedestals) {
            BlackstonePedestalBlockEntity entity = (BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal);
            if (entity.getRenderedItem() == null) return false;
            testInventory.setStack(index, entity.getRenderedItem());
            index++;
        }

        Optional<SoulWeaverRecipe> recipeOptional = world.getRecipeManager().getFirstMatch(SoulWeaverRecipe.Type.INSTANCE, testInventory, world);

        if (!recipeOptional.isPresent()) {
            this.cachedRecipe = null;
            return false;
        }

        this.cachedRecipe = recipeOptional.get();

        return true;
    }

    @Override
    public void tick() {
        if (ritualTick > 0 && ritualTick < 5) {

            if (world.isClient) {
                for (BlockPos pos : pedestals) {
                    if (!(world.getBlockEntity(pos) instanceof BlackstonePedestalBlockEntity)) continue;
                    BlockPos pVector = pos.subtract(this.pos);
                    WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, this.pos, 0.5f, 0.4f, 0.5f, pVector.getX() * 0.115f, 0, pVector.getZ() * 0.115f, 10);
                }

            }

        } else if (ritualTick == 10) {

            for (BlockPos pedestal : pedestals) {
                if (world.isClient) {
                    for (int i = 0; i < 8; i++) {
                        WorldHelper.spawnParticle(ParticleTypes.LAVA, world, pedestal, 0.5f, 1.25f, 0.5f, 0.1f);
                    }
                } else {
                    ((BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal)).setActive(true);
                    ((BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal)).setRenderedItem(null);
                }
            }
        } else if (ritualTick > 10 && ritualTick < 165) {
            if (world.isClient && ritualTick % 2 == 0) {
                if (ritualTick < 140) {
                    for (BlockPos pos : pedestals) {
                        if (!(world.getBlockEntity(pos) instanceof BlackstonePedestalBlockEntity)) continue;
                        if (!((BlackstonePedestalBlockEntity) world.getBlockEntity(pos)).isActive()) continue;

                        BlockPos p = pos.add(0, 1, 0);
                        BlockPos pVector = pos.subtract(this.pos);

                        WorldHelper.spawnParticle(ParticleTypes.SOUL, world, p, 0.5f, 0.3f, 0.5f, pVector.getX() * -0.055f, -0.05f, pVector.getZ() * -0.05f, 30);
                    }
                }

                if (ritualTick > 33) {
                    WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 1.35f, 0.5f, 0.5f);
                    WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 1.35f, 0.5f, 0.5f);

                    WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 0.3f, 0.5f, 0.05f, 0.03f, 0, 0.1f);
                    WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 0.3f, 0.5f, -0.05f, 0.03f, 0, 0.1f);
                    WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 0.3f, 0.5f, 0f, 0.03f, 0.05f, 0.1f);
                    WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 0.3f, 0.5f, 0f, 0.03f, -0.05f, 0.1f);
                }
            }
        } else if (ritualTick > 165) {
            for (BlockPos pedestal : pedestals) {
                if (!world.isClient) {
                    ((BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal)).setActive(false);
                } else {

                    ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.GILDED_BLACKSTONE.getDefaultState());

                    for (int i = 0; i < 30; i++) {
                        WorldHelper.spawnParticle(particle, world, pos, 0.5f, 1.3f, 0.5f, 0.2f);
                    }

                    for (int i = 0; i < 5; i++) {
                        WorldHelper.spawnParticle(ParticleTypes.LAVA, world, pos, 0.5f, 1.25f, 0.5f, 0.15f);
                    }
                }
            }

            if (!world.isClient) {
                setItem(cachedRecipe.getOutput());
                cachedRecipe = null;
            }
            ritualTick = 0;
        }

        if (ritualTick > 0) ritualTick++;
    }

    public double getShakeScaleFactor() {
        return ritualTick > 40 ? ritualTick * 0.001 : 0;
    }
}
