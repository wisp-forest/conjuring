package com.glisco.conjuringforgery.blocks;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class BlackstonePedestalTileEntity extends TileEntity {

    private ItemStack renderedItem;
    private boolean active = false;
    private BlockPos linkedFunnel = null;

    public BlackstonePedestalTileEntity() {
        super(ConjuringForgery.BLACKSTONE_PEDESTAL_TILE.get());
    }

    //Data Logic
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        CompoundNBT item;
        if (renderedItem != null) {
            item = renderedItem.serializeNBT();
            tag.put("Item", item);
        }
        if (linkedFunnel == null) {
            tag.putIntArray("LinkedFunnel", new int[]{});
        } else {
            tag.putIntArray("LinkedFunnel", new int[]{linkedFunnel.getX(), linkedFunnel.getY(), linkedFunnel.getZ()});
        }
        tag.putBoolean("Active", active);
        return tag;
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        CompoundNBT item = tag.getCompound("Item");
        if (!item.isEmpty()) {
            this.renderedItem = ItemStack.read(tag.getCompound("Item"));
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
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(null, pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getPos(), 0, this.write(new CompoundNBT()));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
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
