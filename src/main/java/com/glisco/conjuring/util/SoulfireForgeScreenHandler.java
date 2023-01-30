package com.glisco.conjuring.util;

import com.glisco.conjuring.Conjuring;
import io.wispforest.owo.client.screens.ScreenUtils;
import io.wispforest.owo.client.screens.SlotGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class SoulfireForgeScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public SoulfireForgeScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(10), new ArrayPropertyDelegate(1));
    }

    public SoulfireForgeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(Conjuring.SOULFIRE_FORGE_SCREEN_HANDLER_TYPE, syncId);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);

        //Crafting Inventory
        for (int m = 0; m < 3; ++m) {
            for (int l = 0; l < 3; ++l) {
                this.addSlot(new Slot(inventory, l + m * 3, 26 + l * 18, 16 + m * 18));
            }
        }

        //Result Slot
        this.addSlot(new Slot(inventory, 9, 134, 34) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });

        SlotGenerator.begin(this::addSlot, 8, 84).playerInventory(playerInventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getProgress() {
        return propertyDelegate.get(0);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        return ScreenUtils.handleSlotTransfer(this, invSlot, this.inventory.size());
    }

}
