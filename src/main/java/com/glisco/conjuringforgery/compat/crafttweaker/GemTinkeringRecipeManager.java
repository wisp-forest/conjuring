package com.glisco.conjuringforgery.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.glisco.conjuringforgery.blocks.gem_tinkerer.GemTinkererRecipe;
import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;

@ZenRegister
@ZenCodeType.Name("mods.conjuring.gemTinkering")
public class GemTinkeringRecipeManager implements IRecipeManager {

    @ZenCodeType.Method
    public void addRecipe(String name, IItemStack output, IIngredient[] inputs) throws Exception {

        if (inputs.length != 5) {
            CraftTweakerAPI.logError("Gem Tinkering Recipe %s: input array size not 5", output);
            return;
        }

        name = fixRecipeName(name);
        ResourceLocation resourceLocation = new ResourceLocation("crafttweaker", name);
        NonNullList<Ingredient> inputList =
                NonNullList.from(Ingredient.EMPTY, Arrays.stream(inputs).map(IIngredient::asVanillaIngredient).toArray(
                        Ingredient[]::new));

        CraftTweakerAPI.apply(new ActionAddRecipe(this,
                new GemTinkererRecipe(resourceLocation,
                        output.getInternal(),
                        inputList),
                ""));
    }

    @Override
    public IRecipeType<GemTinkererRecipe> getRecipeType() {
        return GemTinkererRecipe.Type.INSTANCE;
    }
}
