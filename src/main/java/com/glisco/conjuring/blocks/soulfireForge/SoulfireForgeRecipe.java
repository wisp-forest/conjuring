package com.glisco.conjuring.blocks.soulfireForge;

import com.glisco.conjuring.ConjuringCommon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class SoulfireForgeRecipe implements Recipe<Inventory> {

    private final DefaultedList<Ingredient> inputs;
    private final ItemStack result;
    private final Identifier id;
    private final int smeltTime;

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    public SoulfireForgeRecipe(Identifier id, ItemStack result, int smeltTime, DefaultedList<Ingredient> inputs) {
        this.id = id;
        this.result = result;
        this.smeltTime = smeltTime;
        this.inputs = inputs;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int index = r * 3 + c;
                if (!inputs.get(index).test(inventory.getStack(index))) return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int var1, int var2) {
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return result.copy();
    }

    public DefaultedList<Ingredient> getInputs() {
        return inputs;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(ConjuringCommon.SOULFIRE_FORGE_BLOCK);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SoulfireForgeRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public int getSmeltTime() {
        return smeltTime;
    }

    public static class Type implements RecipeType<SoulfireForgeRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();

        public static final Identifier ID = new Identifier("conjuring", "soulfire_forge");
    }
}
