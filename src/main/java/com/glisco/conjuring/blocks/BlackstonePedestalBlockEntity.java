package com.glisco.conjuring.blocks;

import com.glisco.conjuring.ConjuringCommon;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class BlackstonePedestalBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    @NotNull
    private ItemStack renderedItem = ItemStack.EMPTY;
    private boolean active = false;
    private BlockPos linkedFunnel = null;

    public BlackstonePedestalBlockEntity() {
        super(ConjuringCommon.BLACKSTONE_PEDESTAL_BLOCK_ENTITY);
    }

    //Data Logic
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        CompoundTag item = new CompoundTag();
        renderedItem.toTag(item);
        tag.put("Item", item);
        if (linkedFunnel == null) {
            tag.putIntArray("LinkedFunnel", new int[]{});
        } else {
            tag.putIntArray("LinkedFunnel", new int[]{linkedFunnel.getX(), linkedFunnel.getY(), linkedFunnel.getZ()});
        }
        tag.putBoolean("Active", active);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.renderedItem = ItemStack.fromTag(tag.getCompound("Item"));
        int[] funnelPos = tag.getIntArray("LinkedFunnel");
        if (funnelPos.length > 0) {
            this.linkedFunnel = new BlockPos(funnelPos[0], funnelPos[1], funnelPos[2]);
        }
        active = tag.getBoolean("Active");
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (this.world instanceof ServerWorld) {
            this.sync();
        }
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(null, tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.markDirty();
    }

    public void setLinkedFunnel(BlockPos linkedFunnel) {
        this.linkedFunnel = linkedFunnel;
        this.markDirty();
    }

    public BlockPos getLinkedFunnel() {
        return linkedFunnel;
    }

    public boolean isLinked() {
        return linkedFunnel != null;
    }

    public void setItem(@NotNull ItemStack item) {
        this.renderedItem = item;
        this.markDirty();
    }

    @NotNull
    public ItemStack getItem() {
        return renderedItem;
    }

}
