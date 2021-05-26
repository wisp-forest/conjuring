package com.glisco.conjuringforgery.blocks.gem_tinkerer;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class GemTinkererRecipe implements IRecipe<Inventory> {

    private final NonNullList<Ingredient> inputs;
    private final ItemStack result;
    private final ResourceLocation id;

    @Override
    public boolean isDynamic() {
        return true;
    }

    public GemTinkererRecipe(ResourceLocation id, ItemStack result, NonNullList<Ingredient> inputs) {
        this.id = id;
        this.result = result;
        this.inputs = inputs;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        List<ItemStack> testList = new ArrayList<>();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            testList.add(inventory.getStackInSlot(i));
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
    public ItemStack getCraftingResult(Inventory inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int var1, int var2) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return result.copy();
    }

    public NonNullList<Ingredient> getInputs() {
        return inputs;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @OnlyIn(Dist.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(ConjuringForgery.GEM_TINKERER.get());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return GemTinkererRecipeSerializer.INSTANCE;
    }

    @Override
    public IRecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements IRecipeType<GemTinkererRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();

        public static final ResourceLocation ID = new ResourceLocation("conjuring", "gem_tinkering");
    }
}
