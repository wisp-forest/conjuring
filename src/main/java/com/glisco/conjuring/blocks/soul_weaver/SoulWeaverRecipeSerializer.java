package com.glisco.conjuring.blocks.soul_weaver;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

public class SoulWeaverRecipeSerializer implements RecipeSerializer<SoulWeaverRecipe> {

    private SoulWeaverRecipeSerializer() {}

    public static final SoulWeaverRecipeSerializer INSTANCE = new SoulWeaverRecipeSerializer();
    public static final Identifier ID = SoulWeaverRecipe.Type.ID;

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

        var resultItem = JsonHelper.getItem(recipe.result, "item");
        var result = new ItemStack(resultItem, recipe.result.get("count").getAsInt());

        return new SoulWeaverRecipe(id, result, inputs, recipe.transferTag);
    }

    @Override
    public SoulWeaverRecipe read(Identifier id, PacketByteBuf buf) {
        ItemStack result = buf.readItemStack();
        boolean transferTag = buf.readBoolean();

        DefaultedList<Ingredient> inputs = DefaultedList.ofSize(5, Ingredient.EMPTY);

        for (int i = 0; i < 5; i++) {
            inputs.set(i, Ingredient.fromPacket(buf));
        }

        return new SoulWeaverRecipe(id, result, inputs, transferTag);
    }

    @Override
    public void write(PacketByteBuf buf, SoulWeaverRecipe recipe) {
        buf.writeItemStack(recipe.getOutput());
        buf.writeBoolean(recipe.transferTag);

        for (Ingredient ingredient : recipe.getInputs()) {
            ingredient.write(buf);
        }
    }
}
