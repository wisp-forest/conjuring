package com.glisco.conjuring.blocks;

import com.glisco.conjuring.ConjuringCommon;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BlackstonePedestalBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    private ItemStack renderedItem;
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
        if (renderedItem != null) renderedItem.toTag(item);
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
        CompoundTag item = tag.getCompound("Item");
        if (!item.isEmpty()) {
            this.renderedItem = ItemStack.fromTag(tag.getCompound("Item"));
        } else {
            this.renderedItem = null;
        }
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

    public void setRenderedItem(@Nullable ItemStack renderedItem) {
        this.renderedItem = renderedItem == null ? null : renderedItem.copy();
        this.markDirty();
    }

    @Nullable
    public ItemStack getRenderedItem() {
        if (renderedItem == null) {
            return null;
        }
        return renderedItem.copy();
    }
}
