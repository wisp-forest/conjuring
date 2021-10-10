package com.glisco.conjuring.blocks;

import com.glisco.owo.blockentity.SimpleSerializableBlockEntity;
import com.glisco.owo.ops.ItemOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class BlackstonePedestalBlockEntity extends BlockEntity implements SimpleSerializableBlockEntity {

    @NotNull
    private ItemStack renderedItem = ItemStack.EMPTY;
    private boolean active = false;
    private BlockPos linkedFunnel = null;

    public BlackstonePedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ConjuringBlocks.Entities.BLACKSTONE_PEDESTAL, pos, state);
    }

    //Data Logic
    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        ItemOps.store(this.renderedItem, tag, "Item");

        if (linkedFunnel == null) {
            tag.putIntArray("LinkedFunnel", new int[0]);
        } else {
            tag.putIntArray("LinkedFunnel", new int[]{linkedFunnel.getX(), linkedFunnel.getY(), linkedFunnel.getZ()});
        }

        tag.putBoolean("Active", active);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.renderedItem = ItemOps.get(tag, "Item");

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
