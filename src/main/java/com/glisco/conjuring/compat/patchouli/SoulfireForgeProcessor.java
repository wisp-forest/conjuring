package com.glisco.conjuring.compat.patchouli;

public class SoulfireForgeProcessor /*implements IComponentProcessor*/ {
//
//    private SoulfireForgeRecipe recipe;
//    private String text;
//
//    @Override
//    public void setup(IVariableProvider variables) {
//        String recipeId = variables.get("recipe").asString();
//        recipe = (SoulfireForgeRecipe) MinecraftClient.getInstance().world.getRecipeManager().get(new Identifier(recipeId)).orElseThrow(() -> new IllegalArgumentException("Unkown recipe: " + recipeId));
//
//        if (variables.has("text")) text = variables.get("text").asString();
//    }
//
//    @Override
//    public IVariable process(String key) {
//        if (key.startsWith("input")) {
//            int index = Integer.parseInt(key.substring(5));
//            Ingredient ingredient = recipe.getInputs().get(index);
//            ItemStack[] stacks = ingredient.getMatchingStacks();
//            return IVariable.from(stacks);
//        } else if (key.equals("time")) {
//            return IVariable.from(Text.of(recipe.getSmeltTime() / 20 + "s"));
//        } else if (key.equals("output")) {
//            return IVariable.from(new ItemStack[]{recipe.getOutput(null)});
//        } else if (key.equals("iname")) {
//            return IVariable.from(recipe.getOutput(null).getName());
//        } else if (key.equals("text")) {
//            return IVariable.from(Text.of(text));
//        } else {
//            return null;
//        }
//    }
}
