package com.glisco.conjuring.items.soul_alloy_tools;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class SoulAlloyToolMaterial implements ToolMaterial {

    public static SoulAlloyToolMaterial INSTANCE = new SoulAlloyToolMaterial();

    private SoulAlloyToolMaterial() {

    }

    @Override
    public int getDurability() {
        return 2500;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 10.0f;
    }

    @Override
    public float getAttackDamage() {
        return 5.0f;
    }

    @Override
    public int getMiningLevel() {
        return 4;
    }

    @Override
    public int getEnchantability() {
        return 18;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofStacks(new ItemStack(ConjuringCommon.SOUL_ALLOY));
    }
}
