package com.glisco.conjuring.blocks.soulfireForge;

import com.glisco.conjuring.blocks.soul_weaver.SoulWeaverRecipeSerializer;
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

import java.util.HashMap;
import java.util.Map;

public class SoulfireForgeRecipeSerializer implements RecipeSerializer<SoulfireForgeRecipe> {

    private SoulfireForgeRecipeSerializer() {
    }

    public static final SoulfireForgeRecipeSerializer INSTANCE = new SoulfireForgeRecipeSerializer();
    public static final Identifier ID = SoulfireForgeRecipe.Type.ID;


    @Override
    public SoulfireForgeRecipe read(Identifier id, JsonObject json) {
        SoulfireForgeRecipeJson recipe = new Gson().fromJson(json, SoulfireForgeRecipeJson.class);

        if (recipe.key == null || recipe.pattern == null || recipe.result == null || recipe.smeltTime == 0) {
            throw new JsonSyntaxException("Missing recipe attributes");
        }

        HashMap<Character, Ingredient> keys = new HashMap<>();
        for (Map.Entry<String, JsonElement> key : recipe.key.entrySet()) {
            if (key.getKey().length() != 1) throw new JsonSyntaxException("Invalid key \'" + key.getKey() + " \', must be 1 char in length");
            Ingredient ingredient = Ingredient.fromJson(key.getValue());
            if (ingredient.isEmpty()) throw new JsonSyntaxException("Invalid key \'" + key.getKey() + " \', no item found");
            keys.put(key.getKey().charAt(0), ingredient);
        }


        DefaultedList<Ingredient> inputs = DefaultedList.ofSize(9, Ingredient.EMPTY);
        int rowCounter = 0;
        for (JsonElement e : recipe.pattern) {
            String row = e.getAsString();
            if (row.length() != 3) throw new JsonSyntaxException("Wrong pattern length");
            int columnCounter = 0;

            for (char c : row.toCharArray()) {
                if (c == ' ') {
                    inputs.set(rowCounter * 3 + columnCounter, Ingredient.EMPTY);
                } else {
                    inputs.set(rowCounter * 3 + columnCounter, keys.get(c));
                }
                columnCounter++;
            }
            rowCounter++;
        }

        Item resultItem = Registry.ITEM.getOrEmpty(Identifier.tryParse(recipe.result.get("item").getAsString())).orElseThrow(() -> new JsonSyntaxException("No such item \'" + recipe.result.get("item").getAsString() + "\'"));
        ItemStack result = new ItemStack(resultItem, recipe.result.get("count").getAsInt());

        return new SoulfireForgeRecipe(id, result, recipe.smeltTime, inputs);
    }

    @Override
    public SoulfireForgeRecipe read(Identifier id, PacketByteBuf buf) {
        int smeltTime = buf.readInt();
        ItemStack result = buf.readItemStack();

        DefaultedList<Ingredient> inputs = DefaultedList.ofSize(9, Ingredient.EMPTY);

        for (int i = 0; i < 9; i++) {
            inputs.set(i, Ingredient.fromPacket(buf));
        }


        return new SoulfireForgeRecipe(id, result, smeltTime, inputs);
    }

    @Override
    public void write(PacketByteBuf buf, SoulfireForgeRecipe recipe) {
        buf.writeInt(recipe.getSmeltTime());
        buf.writeItemStack(recipe.getOutput());

        for (Ingredient ingredient : recipe.getInputs()) {
            ingredient.write(buf);
        }
    }
}
