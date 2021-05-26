package com.glisco.conjuringforgery.compat.jei;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.blocks.gem_tinkerer.GemTinkererRecipe;
import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeRecipe;
import com.glisco.conjuringforgery.mixin.RecipeManagerAccessor;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class ConjuringJEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ConjuringForgery.MODID, "conjuring_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SoulfireForgeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new GemTinkeringCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManagerAccessor manager = ((RecipeManagerAccessor) Minecraft.getInstance().world.getRecipeManager());

        registration.addRecipes(manager.exposeRecipes(SoulfireForgeRecipe.Type.INSTANCE).values(), SoulfireForgeRecipeCategory.ID);
        registration.addRecipes(manager.exposeRecipes(GemTinkererRecipe.Type.INSTANCE).values(), GemTinkeringCategory.ID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ConjuringForgery.SOULFIRE_FORGE_ITEM.get()), SoulfireForgeRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ConjuringForgery.GEM_TINKERER_ITEM.get()), GemTinkeringCategory.ID);
    }
}
