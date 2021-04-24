package com.glisco.conjuring.blocks.soul_weaver;

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

public class SoulWeaverRecipeSerializer implements RecipeSerializer<SoulWeaverRecipe> {

    private SoulWeaverRecipeSerializer() {
    }

    public static final SoulWeaverRecipeSerializer INSTANCE = new SoulWeaverRecipeSerializer();
    public static final Identifier ID = new Identifier("conjuring:soul_weaving");


    @Override
    public SoulWeaverRecipe read(Identifier id, JsonObject json) {
        SoulWeaverRecipeJson recipe = new Gson().fromJson(json, SoulWeaverRecipeJson.class);

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

        Item resultItem = Registry.ITEM.getOrEmpty(Identifier.tryParse(recipe.result.get("item").getAsString())).orElseThrow(() -> new JsonSyntaxException("No such item \'" + recipe.result.get("item").getAsString() + "\'"));
        ItemStack result = new ItemStack(resultItem, recipe.result.get("count").getAsInt());

        return new SoulWeaverRecipe(id, result, inputs);
    }

    @Override
    public SoulWeaverRecipe read(Identifier id, PacketByteBuf buf) {
        ItemStack result = buf.readItemStack();

        DefaultedList<Ingredient> inputs = DefaultedList.ofSize(5, Ingredient.EMPTY);

        for (int i = 0; i < 5; i++) {
            inputs.set(i, Ingredient.fromPacket(buf));
        }

        return new SoulWeaverRecipe(id, result, inputs);
    }

    @Override
    public void write(PacketByteBuf buf, SoulWeaverRecipe recipe) {
        buf.writeItemStack(recipe.getOutput());

        for (Ingredient ingredient : recipe.getInputs()) {
            ingredient.write(buf);
        }
    }
}
