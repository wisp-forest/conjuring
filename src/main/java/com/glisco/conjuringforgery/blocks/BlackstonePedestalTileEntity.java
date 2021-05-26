package com.glisco.conjuringforgery.blocks;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlackstonePedestalTileEntity extends TileEntity {

    private ItemStack item = ItemStack.EMPTY;
    private boolean active = false;
    private BlockPos linkedFunnel = null;

    public BlackstonePedestalTileEntity() {
        super(ConjuringForgery.BLACKSTONE_PEDESTAL_TILE.get());
    }

    //Data Logic
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        CompoundNBT itemTag = new CompoundNBT();
        item.write(itemTag);
        tag.put("Item", itemTag);
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
        this.item = ItemStack.read(tag.getCompound("Item"));
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

    public void setItem(@Nonnull ItemStack item) {
        this.item = item;
        this.markDirty();
    }

    @Nonnull
    public ItemStack getItem() {
        return item;
    }
}
