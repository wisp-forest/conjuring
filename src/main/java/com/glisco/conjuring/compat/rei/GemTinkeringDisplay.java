package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.blocks.gem_tinkerer.GemTinkererRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class GemTinkeringDisplay implements Display {

    protected final GemTinkererRecipe display;
    protected final List<EntryIngredient> input;
    protected final List<EntryIngredient> output;

    public GemTinkeringDisplay(RecipeEntry<GemTinkererRecipe> recipeEntry) {
        var recipe = recipeEntry.value();
        this.display = recipe;
        this.input = EntryIngredients.ofIngredients(recipe.getInputs());
        this.output = Collections.singletonList(EntryIngredients.of(recipe.getResult(null)));
    }

    @Override
    public @NotNull List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ConjuringCommonPlugin.GEM_TINKERING;
    }

    @Override
    public @NotNull List<EntryIngredient> getOutputEntries() {
        return output;
    }
}
