package com.glisco.conjuringforgery.blocks.soulfireForge;

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

public class SoulfireForgeRecipe implements IRecipe<Inventory> {

    private final NonNullList<Ingredient> inputs;
    private final ItemStack result;
    private final ResourceLocation id;
    private final int smeltTime;

    public SoulfireForgeRecipe(ResourceLocation id, ItemStack result, int smeltTime, NonNullList<Ingredient> inputs) {
        this.id = id;
        this.result = result;
        this.smeltTime = smeltTime;
        this.inputs = inputs;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int index = r * 3 + c;
                //System.out.println("Testing: " + inputs.get(index).toJson());
                if (!inputs.get(index).test(inventory.getStackInSlot(index))) return false;
            }
        }

        return true;
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
    public ItemStack getIcon() {
        return new ItemStack(ConjuringForgery.SOULFIRE_FORGE.get());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SoulfireForgeRecipeSerializer.INSTANCE;
    }

    @Override
    public IRecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public int getSmeltTime() {
        return smeltTime;
    }

    public static class Type implements IRecipeType<SoulfireForgeRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();

        public static final ResourceLocation ID = new ResourceLocation("conjuring", "soulfire_forge");
    }
}
