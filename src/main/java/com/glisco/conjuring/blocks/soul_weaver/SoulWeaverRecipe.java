package com.glisco.conjuring.blocks.soul_weaver;

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

import java.util.ArrayList;
import java.util.List;

public class SoulWeaverRecipe implements Recipe<Inventory> {

    private final DefaultedList<Ingredient> inputs;
    private final ItemStack result;
    private final Identifier id;
    public final boolean transferTag;

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    public SoulWeaverRecipe(Identifier id, ItemStack result, DefaultedList<Ingredient> inputs, boolean transferTag) {
        this.id = id;
        this.result = result;
        this.inputs = inputs;
        this.transferTag = transferTag;
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
    public ItemStack createIcon() {
        return new ItemStack(ConjuringCommon.SOUL_WEAVER_BLOCK);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SoulWeaverRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<SoulWeaverRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();

        public static final Identifier ID = new Identifier("conjuring", "soul_weaving");
    }
}
