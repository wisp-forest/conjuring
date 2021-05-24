package com.glisco.conjuringforgery;

import com.glisco.conjuringforgery.blocks.conjurer.ConjurerTileEntity;
import com.glisco.conjuringforgery.items.ConjuringFocus;
import com.glisco.conjuringforgery.items.charms.HasteCharm;
import com.glisco.conjuringforgery.items.charms.IgnoranceCharm;
import com.glisco.conjuringforgery.items.charms.PlentifulnessCharm;
import com.glisco.conjuringforgery.items.charms.ScopeCharm;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ConjurerContainer extends Container {

    IItemHandler inventory;
    TileEntity tile;

    public ConjurerContainer(int windowId, PlayerInventory playerInventory, World in, BlockPos at) {
        super(ConjuringForgery.CONJURER_CONTAINER_TYPE.get(), windowId);
        tile = in.getTileEntity(at);

        if(tile != null) this.inventory = ((ConjurerTileEntity)tile).getInventory();

        this.addSlot(new SlotItemHandler(this.inventory, 0, 77, 41) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() instanceof ConjuringFocus && stack.getOrCreateTag().contains("Entity");
            }
        });

        this.addSlot(new SlotItemHandler(this.inventory, 1, 77, 2) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() instanceof HasteCharm;
            }
        });

        this.addSlot(new SlotItemHandler(this.inventory, 2, 117, 41) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() instanceof PlentifulnessCharm;
            }
        });

        this.addSlot(new SlotItemHandler(this.inventory, 3, 77, 80) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() instanceof ScopeCharm;
            }
        });

        this.addSlot(new SlotItemHandler(this.inventory, 4, 38, 41) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() instanceof IgnoranceCharm;
            }
        });

        int m;
        int l;

        //Player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 110 + m * 18));
            }
        }
        //Player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 168));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true; //TODO fix this shit
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (index < this.inventory.getSlots()) {
                if (!this.mergeItemStack(originalStack, this.inventory.getSlots(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(originalStack, 0, this.inventory.getSlots(), false)) {
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
        super.onContainerClosed(player);
    }


}
