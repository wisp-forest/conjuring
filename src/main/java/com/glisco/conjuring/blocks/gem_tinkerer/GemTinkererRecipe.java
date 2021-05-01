package com.glisco.conjuring.blocks.gem_tinkerer;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.blocks.soulfireForge.SoulfireForgeRecipeSerializer;
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

import java.util.ArrayList;
import java.util.List;

public class GemTinkererRecipe implements Recipe<Inventory> {

    private final DefaultedList<Ingredient> inputs;
    private final ItemStack result;
    private final Identifier id;

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    public GemTinkererRecipe(Identifier id, ItemStack result, DefaultedList<Ingredient> inputs) {
        this.id = id;
        this.result = result;
        this.inputs = inputs;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        List<ItemStack> testList = new ArrayList<>();

        for (int i = 0; i < inventory.size(); i++) {
            testList.add(inventory.getStack(i));
        }

        return inputs.stream().allMatch(ingredient -> {

            for (int i = 0; i < testList.size(); i++) {
                if (ingredient.test(testList.get(i))) {
                    testList.remove(i);
                    return true;
                }
            }
            return false;
        });
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
        return new ItemStack(ConjuringCommon.GEM_TINKERER_BLOCK);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SoulfireForgeRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<GemTinkererRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();

        public static final Identifier ID = new Identifier("conjuring", "gem_tinkering");
    }
}
