package com.glisco.conjuringforgery.compat.patchouli;

import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class SoulfireForgeProcessor implements IComponentProcessor {

    private SoulfireForgeRecipe recipe;
    private String text;

    @Override
    public void setup(IVariableProvider variables) {
        String recipeId = variables.get("recipe").asString();
        recipe = (SoulfireForgeRecipe) Minecraft.getInstance().world.getRecipeManager().getRecipe(new ResourceLocation(recipeId)).orElseThrow(() -> new IllegalArgumentException("Unkown recipe: " + recipeId));

        if (variables.has("text")) text = variables.get("text").asString();
    }

    @Override
    public IVariable process(String key) {
        if (key.startsWith("input")) {
            int index = Integer.parseInt(key.substring(5));
            Ingredient ingredient = recipe.getInputs().get(index);
            ItemStack[] stacks = ingredient.getMatchingStacks();
            return IVariable.from(stacks);
        } else if (key.equals("time")) {
            return IVariable.from(ITextComponent.getTextComponentOrEmpty(recipe.getSmeltTime() / 20 + "s"));
        } else if (key.equals("output")) {
            return IVariable.from(new ItemStack[]{recipe.getRecipeOutput()});
        } else if (key.equals("iname")) {
            return IVariable.from(recipe.getRecipeOutput().getDisplayName());
        } else if (key.equals("text")) {
            return IVariable.from(ITextComponent.getTextComponentOrEmpty(text));
        } else {
            return null;
        }
    }
}
