package com.glisco.conjuringforgery.blocks.conjurer;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class ConjurerTileEntity extends TileEntity implements ITickableTileEntity {

    private ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };

    private final ModifiedAbstractSpawner logic = new ModifiedAbstractSpawner() {
        public void broadcastEvent(int id) {
            ConjurerTileEntity.this.world.addBlockEvent(ConjurerTileEntity.this.pos, ConjuringForgery.CONJURER.get(), id, 0);
        }

        public World getWorld() {
            return ConjurerTileEntity.this.world;
        }

        @Override
        public BlockPos getSpawnerPosition() {
            return ConjurerTileEntity.this.pos;
        }
    };

    public ConjurerTileEntity() {
        super(ConjuringForgery.CONJURER_TILE.get());
    }

    //Conjurer Logic
    public void tick() {
        this.logic.tick();
    }

    public ModifiedAbstractSpawner getLogic() {
        return logic;
    }

    @Override
    public void markDirty() {
        ConjurerHelper.updateConjurerProperties(this);
        super.markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }

    public boolean isActive() {
        return logic.isActive();
    }

    public void setActive(boolean active) {
        this.logic.setActive(active);
    }

    //NBT Logic
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        this.logic.read(tag);
        inventory.deserializeNBT(tag.getCompound("Items"));
    }

    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        this.logic.write(tag);
        tag.put("Items", inventory.serializeNBT());
        return tag;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(null, pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();
        this.write(tag);
        tag.remove("SpawnPotentials");
        return new SUpdateTileEntityPacket(this.getPos(), 0, tag);
    }

    //Interface Logic
    public ItemStackHandler getInventory() {
        return inventory;
    }

    public void setRequiresPlayer(boolean requiresPlayer) {
        this.logic.setRequiresPlayer(requiresPlayer);
    }
}
