package com.glisco.conjuringforgery;

import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoulfireForgeContainer extends Container {

    Inventory inventory;
    IIntArray propertyDelegate;
    SoulfireForgeTileEntity tile;

    public SoulfireForgeContainer(int syncId, PlayerInventory playerInventory, World in, BlockPos at) {
        super(ConjuringForgery.SOULFIRE_FORGE_CONTAINER_TYPE.get(), syncId);

        tile = (SoulfireForgeTileEntity) in.getTileEntity(at);

        if (tile != null) {
            this.inventory = tile.getInventory();
            this.propertyDelegate = tile.getProperties();
        }
        this.trackIntArray(propertyDelegate);

        int m;
        int l;

        //Crafting Inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 3; ++l) {
                this.addSlot(new Slot(inventory, l + m * 3, 26 + l * 18, 16 + m * 18));
            }
        }

        //Result Slot
        this.addSlot(new Slot(inventory, 9, 134, 34) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });

        //Player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        //Player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    public int getProgress() {
        return propertyDelegate.get(0);
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return this.inventory.isUsableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (index < this.inventory.getSizeInventory()) {
                if (!this.mergeItemStack(originalStack, this.inventory.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(originalStack, 0, this.inventory.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return newStack;
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        //super.onContainerClosed(player);
    }
}
