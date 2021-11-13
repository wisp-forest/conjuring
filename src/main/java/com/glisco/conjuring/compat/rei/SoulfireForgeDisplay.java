package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.blocks.soulfire_forge.SoulfireForgeRecipe;
import dev.architectury.utils.NbtType;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class SoulfireForgeDisplay implements Display {

    protected final int smeltTime;
    protected final List<EntryIngredient> input;
    protected final List<EntryIngredient> output;

    public SoulfireForgeDisplay(SoulfireForgeRecipe recipe) {
        this.smeltTime = recipe.getSmeltTime();

        this.input = EntryIngredients.ofIngredients(recipe.getInputs());

        this.output = Collections.singletonList(EntryIngredients.of(recipe.getOutput()));
    }

    private SoulfireForgeDisplay(int smeltTime, List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        this.smeltTime = smeltTime;

        this.input = inputs;

        this.output = outputs;
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
        return smeltTime;
    }

    public static class SoulfireForgeDisplaySerializer implements DisplaySerializer<SoulfireForgeDisplay> {

        @Override
        public NbtCompound save(NbtCompound tag, SoulfireForgeDisplay display) {
            // Store the smelt time
            tag.putInt("smeltTime", display.getSmeltTime());

            // Store the recipe inputs
            NbtList input = new NbtList();
            display.input.forEach(entryStacks -> input.add(entryStacks.save()));
            tag.put("input", input);

            // Store the recipe outputs
            NbtList output = new NbtList();
            display.output.forEach(entryStacks -> output.add(entryStacks.save()));
            tag.put("output", output);

            return tag;
        }

        @Override
        public SoulfireForgeDisplay read(NbtCompound tag) {
            // Get the smelt time
            int smeltTime = tag.getInt("smeltTime");

            // We get a list of all the recipe inputs (9)
            List<EntryIngredient> input = new ArrayList<>();
            tag.getList("input", NbtType.LIST).forEach(nbtElement -> input.add(EntryIngredient.read((NbtList) nbtElement)));

            // We get a list of all the recipe outputs (9)
            List<EntryIngredient> ouput = new ArrayList<>();
            tag.getList("output", NbtType.LIST).forEach(nbtElement -> ouput.add(EntryIngredient.read((NbtList) nbtElement)));

            return new SoulfireForgeDisplay(smeltTime, input, ouput);
        }
    }
}
