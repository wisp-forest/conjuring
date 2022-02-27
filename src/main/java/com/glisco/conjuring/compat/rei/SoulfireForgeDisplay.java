package com.glisco.conjuring.compat.rei;

public class SoulfireForgeDisplay /*implements Display*/ {

//    protected final int smeltTime;
//    protected final List<EntryIngredient> input;
//    protected final List<EntryIngredient> output;
//
//    public SoulfireForgeDisplay(SoulfireForgeRecipe recipe) {
//        this.smeltTime = recipe.getSmeltTime();
//        this.input = EntryIngredients.ofIngredients(recipe.getInputs());
//        this.output = Collections.singletonList(EntryIngredients.of(recipe.getOutput()));
//    }
//
//    public SoulfireForgeDisplay(int smeltTime, List<EntryIngredient> input, List<EntryIngredient> output) {
//        this.smeltTime = smeltTime;
//        this.input = input;
//        this.output = output;
//    }
//
//    @Override
//    public @NotNull List<EntryIngredient> getInputEntries() {
//        return input;
//    }
//
//    @Override
//    public CategoryIdentifier<?> getCategoryIdentifier() {
//        return ConjuringCommonPlugin.SOULFIRE_FORGE;
//    }
//
//    @Override
//    public @NotNull List<EntryIngredient> getOutputEntries() {
//        return output;
//    }
//
//    public int getSmeltTime() {
//        return this.smeltTime;
//    }
//
//    public static class Serializer implements DisplaySerializer<SoulfireForgeDisplay> {
//
//        public static final Serializer INSTANCE = new Serializer();
//
//        private Serializer() {}
//
//        @Override
//        public NbtCompound save(NbtCompound tag, SoulfireForgeDisplay display) {
//            // Store the smelt time
//            tag.putInt("smeltTime", display.getSmeltTime());
//
//            // Store the recipe inputs
//            NbtList input = new NbtList();
//            display.input.forEach(entryStacks -> input.add(entryStacks.save()));
//            tag.put("input", input);
//
//            // Store the recipe outputs
//            NbtList output = new NbtList();
//            display.output.forEach(entryStacks -> output.add(entryStacks.save()));
//            tag.put("output", output);
//
//            return tag;
//        }
//
//        @Override
//        public SoulfireForgeDisplay read(NbtCompound tag) {
//            // Get the smelt time
//            int smeltTime = tag.getInt("smeltTime");
//
//            // We get a list of all the recipe inputs
//            List<EntryIngredient> input = new ArrayList<>();
//            tag.getList("input", NbtType.LIST).forEach(nbtElement -> input.add(EntryIngredient.read((NbtList) nbtElement)));
//
//            // We get a list of all the recipe outputs
//            List<EntryIngredient> output = new ArrayList<>();
//            tag.getList("output", NbtType.LIST).forEach(nbtElement -> output.add(EntryIngredient.read((NbtList) nbtElement)));
//
//            return new SoulfireForgeDisplay(smeltTime, input, output);
//        }
//    }
}
