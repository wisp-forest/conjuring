package com.glisco.conjuring.blocks.soulfireForge;

import net.minecraft.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class SoulfireForgeRecipeHelper {

    private static final List<SoulfireForgeRecipe> recipes = new ArrayList<>();

    public static void register(SoulfireForgeRecipe recipe) {
        recipes.add(recipe);
    }

    public static SoulfireForgeRecipe getMatchingRecipe(Inventory craftingInventory) {
        for (SoulfireForgeRecipe recipe : recipes) {
            if (recipe.matches(craftingInventory)) return recipe;
        }
        return null;
    }
}
