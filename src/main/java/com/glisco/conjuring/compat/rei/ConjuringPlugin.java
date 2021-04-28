package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.blocks.gem_tinkerer.GemTinkererRecipe;
import com.glisco.conjuring.blocks.soul_weaver.SoulWeaverRecipe;
import com.glisco.conjuring.blocks.soulfireForge.SoulfireForgeRecipe;
import me.shedaniel.rei.api.*;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.util.Identifier;

public class ConjuringPlugin implements REIPluginV0 {

    public static final Identifier SOULFIRE_FORGE = new Identifier("conjuring", "soulfire_forge");
    public static final Identifier GEM_TINKERING = new Identifier("conjuring", "gem_tinkering");
    public static final Identifier SOUL_WEAVING = new Identifier("conjuring", "soul_weaving");

    private static final RecipeCategory<SoulfireForgeDisplay> SOULFIRE_FORGE_CATEGORY = new SoulfireForgeCategory();

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier("conjuring", "conjuring_plugin");
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new GemTinkeringCategory());
        recipeHelper.registerCategory(new SoulWeavingCategory());
        recipeHelper.registerCategory(SOULFIRE_FORGE_CATEGORY);
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        recipeHelper.registerRecipes(SOULFIRE_FORGE, SoulfireForgeRecipe.class, SoulfireForgeDisplay::new);
        recipeHelper.registerRecipes(GEM_TINKERING, GemTinkererRecipe.class, GemTinkeringDisplay::new);
        recipeHelper.registerRecipes(SOUL_WEAVING, SoulWeaverRecipe.class, SoulWeavingDisplay::new);
    }

    //TODO pizza
    @Override
    public void registerEntries(EntryRegistry entryRegistry) {
        entryRegistry.removeEntryIf(entryStack -> entryStack.getItem() == ConjuringCommon.PIZZA);
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerWorkingStations(SOULFIRE_FORGE, EntryStack.create(ConjuringCommon.SOULFIRE_FORGE_BLOCK));
        recipeHelper.registerWorkingStations(GEM_TINKERING, EntryStack.create(ConjuringCommon.GEM_TINKERER_BLOCK));
        recipeHelper.registerWorkingStations(SOUL_WEAVING, EntryStack.create(ConjuringCommon.SOUL_WEAVER_BLOCK));
        recipeHelper.registerWorkingStations(SOUL_WEAVING, EntryStack.create(ConjuringCommon.BLACKSTONE_PEDESTAL_BLOCK));
    }
}
