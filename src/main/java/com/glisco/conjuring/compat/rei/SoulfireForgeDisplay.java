package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.blocks.soulfireForge.SoulfireForgeRecipe;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SoulfireForgeDisplay implements RecipeDisplay {

    protected SoulfireForgeRecipe display;
    protected List<List<EntryStack>> input;
    protected List<EntryStack> output;

    public SoulfireForgeDisplay(SoulfireForgeRecipe recipe) {
        this.display = recipe;

        this.input = recipe.getInputs().stream().map((i) -> {
            List<EntryStack> entries = new ArrayList();
            ItemStack[] var2 = i.getMatchingStacksClient();

            for (ItemStack stack : var2)
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
        return ConjuringPlugin.SOULFIRE_FORGE;
    }

    @Override
    public @NotNull List<EntryStack> getOutputEntries() {
        return output;
    }

    public int getSmeltTime() {
        return display.getSmeltTime();
    }
}
