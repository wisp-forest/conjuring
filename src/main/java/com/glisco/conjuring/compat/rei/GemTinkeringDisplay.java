package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.blocks.gem_tinkerer.GemTinkererRecipe;

public class GemTinkeringDisplay /*implements RecipeDisplay*/ {

    protected GemTinkererRecipe display;
//    protected List<List<EntryStack>> input;
//    protected List<EntryStack> output;
//
//    public GemTinkeringDisplay(GemTinkererRecipe recipe) {
//        this.display = recipe;
//
//        this.input = recipe.getInputs().stream().map((i) -> {
//            List<EntryStack> entries = new ArrayList<>();
//
//            for (ItemStack stack : i.getMatchingStacksClient()) {
//                entries.add(EntryStack.create(stack));
//            }
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
//        return ConjuringPlugin.GEM_TINKERING;
//    }
//
//    @Override
//    public @NotNull List<List<EntryStack>> getResultingEntries() {
//        return Collections.singletonList(output);
//    }
}
