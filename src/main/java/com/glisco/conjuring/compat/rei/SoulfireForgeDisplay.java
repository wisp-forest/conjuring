package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.blocks.soulfire_forge.SoulfireForgeRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SoulfireForgeDisplay implements Display {

    protected SoulfireForgeRecipe display;
    protected List<EntryIngredient> input;
    protected List<EntryIngredient> output;

    public SoulfireForgeDisplay(SoulfireForgeRecipe recipe) {
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
        return ConjuringPlugin.SOULFIRE_FORGE;
    }

    @Override
    public @NotNull List<EntryIngredient> getOutputEntries() {
        return output;
    }

    public int getSmeltTime() {
        return display.getSmeltTime();
    }
}
