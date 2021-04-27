package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.blocks.soulfireForge.SoulfireForgeRecipe;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.util.Identifier;

public class ConjuringPlugin implements REIPluginV0 {

    public static final Identifier SOULFIRE_FORGE = new Identifier("conjuring", "soulfire_forge");

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier("conjuring", "conjuring_plugin");
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new SoulfireForgeCategory());
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        recipeHelper.registerRecipes(SOULFIRE_FORGE, SoulfireForgeRecipe.class, SoulfireForgeDisplay::new);
    }

    //TODO pizza
    @Override
    public void registerEntries(EntryRegistry entryRegistry) {
        //entryRegistry.removeEntryIf(entryStack -> entryStack.getItem() == ConjuringCommon.SOUL_ALLOY);
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerWorkingStations(new Identifier("conjuring:soulfire_forge"), EntryStack.create(ConjuringCommon.SOULFIRE_FORGE_BLOCK));
    }
}
