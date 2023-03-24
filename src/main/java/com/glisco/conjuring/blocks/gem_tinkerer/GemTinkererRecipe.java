package com.glisco.conjuring.blocks.gem_tinkerer;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.blocks.ConjuringBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
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
    public ItemStack craft(Inventory inventory, DynamicRegistryManager drm) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int var1, int var2) {
        return false;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager drm) {
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
        return new ItemStack(ConjuringBlocks.GEM_TINKERER);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GemTinkererRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<GemTinkererRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();

        public static final Identifier ID = Conjuring.id("gem_tinkering");
    }
}
