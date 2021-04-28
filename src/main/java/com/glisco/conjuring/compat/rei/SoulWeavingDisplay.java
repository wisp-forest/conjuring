package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.blocks.soul_weaver.SoulWeaverRecipe;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SoulWeavingDisplay implements RecipeDisplay {

    protected SoulWeaverRecipe display;
    protected List<List<EntryStack>> input;
    protected List<EntryStack> output;

    public SoulWeavingDisplay(SoulWeaverRecipe recipe) {
        this.display = recipe;

        this.input = recipe.getInputs().stream().map((i) -> {
            List<EntryStack> entries = new ArrayList<>();

            for (ItemStack stack : i.getMatchingStacksClient())
                entries.add(EntryStack.create(stack));

            return entries;
        }).collect(Collectors.toList());

        this.output = Collections.singletonList(EntryStack.create(recipe.getOutput()));
    }

    @Override
    public @NotNull List<List<EntryStack>> getInputEntries() {
        return input;
    }

    @Override
    public @NotNull Identifier getRecipeCategory() {
        return ConjuringPlugin.SOUL_WEAVING;
    }

    @Override
    public @NotNull List<EntryStack> getOutputEntries() {
        return output;
    }
}
