package com.glisco.conjuring;

import com.glisco.conjuring.items.ConjuringFocus;
import com.glisco.conjuring.items.charms.IgnoranceCharm;
import com.glisco.conjuring.items.charms.PlentifulnessCharm;
import com.glisco.conjuring.items.charms.ScopeCharm;
import com.glisco.conjuring.items.charms.HasteCharm;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class ConjurerScreenHandler extends ScreenHandler {

    Inventory inventory;

    public ConjurerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(5));
    }

    public ConjurerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ConjuringCommon.CONJURER_SCREEN_HANDLER_TYPE, syncId);
        this.inventory = inventory;

        this.addSlot(new Slot(this.inventory, 0, 77, 41) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof ConjuringFocus && stack.getOrCreateTag().contains("Entity");
            }
        });

        this.addSlot(new Slot(this.inventory, 1, 77, 2) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof HasteCharm;
            }
        });

        this.addSlot(new Slot(this.inventory, 2, 117, 41) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof PlentifulnessCharm;
            }
        });

        this.addSlot(new Slot(this.inventory, 3, 77, 80) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof ScopeCharm;
            }
        });

        this.addSlot(new Slot(this.inventory, 4, 38, 41) {
            @Override
            public boolean canInsert(ItemStack stack) {
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
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        System.out.println(i + " : " + j + " : " + actionType);
        return super.onSlotClick(i, j, actionType, playerEntity);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
    }
}
