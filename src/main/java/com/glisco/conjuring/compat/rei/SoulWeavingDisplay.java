package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.blocks.soul_weaver.SoulWeaverRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SoulWeavingDisplay implements Display {

    protected final SoulWeaverRecipe display;
    protected final List<EntryIngredient> input;
    protected final List<EntryIngredient> output;

    public SoulWeavingDisplay(SoulWeaverRecipe recipe) {
        this.display = recipe;

        this.input = EntryIngredients.ofIngredients(recipe.getInputs());

        this.output = Collections.singletonList(EntryIngredients.of(recipe.getOutput()));
    }

    @Override
    public @NotNull List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ConjuringPlugin.SOUL_WEAVING;
    }

    @Override
    public @NotNull List<EntryIngredient> getOutputEntries() {
        return output;
    }
}
