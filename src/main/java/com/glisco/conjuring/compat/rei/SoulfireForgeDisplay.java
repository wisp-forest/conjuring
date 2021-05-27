package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.blocks.soulfire_forge.SoulfireForgeRecipe;

public class SoulfireForgeDisplay /*implements RecipeDisplay */{

    protected SoulfireForgeRecipe display;
//    protected List<List<EntryStack>> input;
//    protected List<EntryStack> output;
//
//    public SoulfireForgeDisplay(SoulfireForgeRecipe recipe) {
//
//        this.display = recipe;
//
//        this.input = recipe.getInputs().stream().map((i) -> {
//            List<EntryStack> entries = new ArrayList<>();
//            ItemStack[] var2 = i.getMatchingStacksClient();
//
//            for (ItemStack stack : var2)
//                entries.add(EntryStack.create(stack));
//
//            return entries;
//        }).collect(Collectors.toList());
//
//        this.output = Collections.singletonList(EntryStack.create(recipe.getOutput()));
//    }
//
//    @Override
//    public @NotNull List<List<EntryStack>> getInputEntries() {
//        return input;
//    }
//
//    @Override
//    public @NotNull Identifier getRecipeCategory() {
//        return ConjuringPlugin.SOULFIRE_FORGE;
//    }
//
//    @Override
//    public @NotNull List<List<EntryStack>> getResultingEntries() {
//        return Collections.singletonList(output);
//    }
//
//    public int getSmeltTime() {
//        return display.getSmeltTime();
//    }
}
