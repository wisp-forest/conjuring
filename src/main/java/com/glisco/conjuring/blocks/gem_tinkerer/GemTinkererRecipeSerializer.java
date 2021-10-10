package com.glisco.conjuring.blocks.gem_tinkerer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public class GemTinkererRecipeSerializer implements RecipeSerializer<GemTinkererRecipe> {

    private GemTinkererRecipeSerializer() {
    }

    public static final GemTinkererRecipeSerializer INSTANCE = new GemTinkererRecipeSerializer();
    public static final Identifier ID = GemTinkererRecipe.Type.ID;


    @Override
    public GemTinkererRecipe read(Identifier id, JsonObject json) {
        GemTinkererRecipeJson recipe = new Gson().fromJson(json, GemTinkererRecipeJson.class);

        if (recipe.inputs == null || recipe.result == null) {
            throw new JsonSyntaxException("Missing recipe attributes");
        }

        DefaultedList<Ingredient> inputs = DefaultedList.ofSize(5, Ingredient.EMPTY);

        int index = 0;
        for (JsonElement element : recipe.inputs) {
            if (!element.isJsonObject()) continue;
            inputs.set(index, Ingredient.fromJson(element.getAsJsonObject()));
            index++;
        }

        Item resultItem = Registry.ITEM.getOrEmpty(Identifier.tryParse(recipe.result.get("item").getAsString())).orElseThrow(() -> new JsonSyntaxException("No such item '" + recipe.result.get("item").getAsString() + "'"));
        ItemStack result = new ItemStack(resultItem, recipe.result.get("count").getAsInt());

        return new GemTinkererRecipe(id, result, inputs);
    }

    @Override
    public GemTinkererRecipe read(Identifier id, PacketByteBuf buf) {
        ItemStack result = buf.readItemStack();

        DefaultedList<Ingredient> inputs = DefaultedList.ofSize(5, Ingredient.EMPTY);

        for (int i = 0; i < 5; i++) {
            inputs.set(i, Ingredient.fromPacket(buf));
        }

        return new GemTinkererRecipe(id, result, inputs);
    }

    @Override
    public void write(PacketByteBuf buf, GemTinkererRecipe recipe) {
        buf.writeItemStack(recipe.getOutput());

        for (Ingredient ingredient : recipe.getInputs()) {
            ingredient.write(buf);
        }
    }
}
