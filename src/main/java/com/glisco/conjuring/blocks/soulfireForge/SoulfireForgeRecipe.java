package com.glisco.conjuring.blocks.soulfireForge;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SoulfireForgeRecipe {

    private final int smeltTime;

    private final ItemStack result;
    private final Item[] matrix;

    public SoulfireForgeRecipe(ItemStack result, Item[] matrix, int smeltTime) {
        this.smeltTime = smeltTime;
        this.result = result;
        this.matrix = matrix;
    }

    public boolean matches(Inventory craftingInventory) {
        for (int i = 0; i < 9; i++) {
            if (!craftingInventory.getStack(i).getItem().equals(matrix[i])) return false;
        }
        return true;
    }

    public ItemStack getResult() {
        return result.copy();
    }

    public int getSmeltTime() {
        return smeltTime;
    }
}
