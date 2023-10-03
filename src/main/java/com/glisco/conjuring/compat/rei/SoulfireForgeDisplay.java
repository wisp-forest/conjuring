package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.blocks.soulfire_forge.SoulfireForgeRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoulfireForgeDisplay implements Display {

    protected final int smeltTime;
    protected final List<EntryIngredient> input;
    protected final List<EntryIngredient> output;

    public SoulfireForgeDisplay(RecipeEntry<SoulfireForgeRecipe> recipeEntry) {
        var recipe = recipeEntry.value();
        this.smeltTime = recipe.getSmeltTime();
        this.input = EntryIngredients.ofIngredients(recipe.getIngredients());
        this.output = Collections.singletonList(EntryIngredients.of(recipe.getResult(null)));
    }

    public SoulfireForgeDisplay(int smeltTime, List<EntryIngredient> input, List<EntryIngredient> output) {
        this.smeltTime = smeltTime;
        this.input = input;
        this.output = output;
    }

    @Override
    public @NotNull List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ConjuringCommonPlugin.SOULFIRE_FORGE;
    }

    @Override
    public @NotNull List<EntryIngredient> getOutputEntries() {
        return output;
    }

    public int getSmeltTime() {
        return this.smeltTime;
    }

    public static class Serializer implements DisplaySerializer<SoulfireForgeDisplay> {

        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {}

        @Override
        public NbtCompound save(NbtCompound tag, SoulfireForgeDisplay display) {
            // Store the smelt time
            tag.putInt("smeltTime", display.getSmeltTime());

            // Store the recipe inputs
            NbtList input = new NbtList();
            display.input.forEach(entryStacks -> input.add(entryStacks.saveIngredient()));
            tag.put("input", input);

            // Store the recipe outputs
            NbtList output = new NbtList();
            display.output.forEach(entryStacks -> output.add(entryStacks.saveIngredient()));
            tag.put("output", output);

            return tag;
        }

        @Override
        public SoulfireForgeDisplay read(NbtCompound tag) {
            // Get the smelt time
            int smeltTime = tag.getInt("smeltTime");

            // We get a list of all the recipe inputs
            List<EntryIngredient> input = new ArrayList<>();
            tag.getList("input", NbtType.LIST).forEach(nbtElement -> input.add(EntryIngredient.read((NbtList) nbtElement)));

            // We get a list of all the recipe outputs
            List<EntryIngredient> output = new ArrayList<>();
            tag.getList("output", NbtType.LIST).forEach(nbtElement -> output.add(EntryIngredient.read((NbtList) nbtElement)));

            return new SoulfireForgeDisplay(smeltTime, input, output);
        }
    }
}
