package com.glisco.conjuring.blocks;

import com.glisco.conjuring.ConjuringCommon;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;

public class GemTinkererBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);

    public GemTinkererBlockEntity() {
        super(ConjuringCommon.GEM_TINKERER_BLOCK_ENTITY);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        Inventories.fromTag(tag, inventory);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, inventory);
        return super.toTag(tag);
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(this.getCachedState(), compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }

    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (!world.isClient()) {
            sync();
        }
    }
}
