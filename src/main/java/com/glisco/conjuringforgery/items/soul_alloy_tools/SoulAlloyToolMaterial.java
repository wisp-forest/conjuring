package com.glisco.conjuringforgery.items.soul_alloy_tools;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class SoulAlloyToolMaterial implements IItemTier {

    public static SoulAlloyToolMaterial INSTANCE = new SoulAlloyToolMaterial();

    private SoulAlloyToolMaterial() {

    }

    @Override
    public int getMaxUses() {
        return 2500;
    }

    @Override
    public float getEfficiency() {
        return 10.0f;
    }

    @Override
    public float getAttackDamage() {
        return 5.0f;
    }

    @Override
    public int getHarvestLevel() {
        return 4;
    }

    @Override
    public int getEnchantability() {
        return 18;
    }

    @Override
    public Ingredient getRepairMaterial() {
        return Ingredient.fromStacks(new ItemStack(ConjuringForgery.SOUL_ALLOY.get()));
    }
}
