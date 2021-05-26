package com.glisco.conjuringforgery.blocks.soulfireForge;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.HashMap;
import java.util.Map;

public class SoulfireForgeRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SoulfireForgeRecipe> {

    private SoulfireForgeRecipeSerializer() {
    }

    public static final SoulfireForgeRecipeSerializer INSTANCE = new SoulfireForgeRecipeSerializer();
    public static final ResourceLocation ID = new ResourceLocation("conjuring:soulfire_forge");

    @Override
    public SoulfireForgeRecipe read(ResourceLocation id, JsonObject json) {
        SoulfireForgeRecipeJson recipe = new Gson().fromJson(json, SoulfireForgeRecipeJson.class);

        if (recipe.key == null || recipe.pattern == null || recipe.result == null || recipe.smeltTime == 0) {
            throw new JsonSyntaxException("Missing recipe attributes");
        }

        HashMap<Character, Ingredient> keys = new HashMap<>();
        for (Map.Entry<String, JsonElement> key : recipe.key.entrySet()) {
            if (key.getKey().length() != 1) throw new JsonSyntaxException("Invalid key \'" + key.getKey() + " \', must be 1 char in length");
            Ingredient ingredient = Ingredient.deserialize(key.getValue());
            if (ingredient.hasNoMatchingItems()) throw new JsonSyntaxException("Invalid key \'" + key.getKey() + " \', no item found");
            keys.put(key.getKey().charAt(0), ingredient);
        }


        NonNullList<Ingredient> inputs = NonNullList.withSize(9, Ingredient.EMPTY);
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

        Item resultItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryCreate(recipe.result.get("item").getAsString()));
        ItemStack result = new ItemStack(resultItem, recipe.result.get("count").getAsInt());

        return new SoulfireForgeRecipe(id, result, recipe.smeltTime, inputs);
    }

    @Override
    public SoulfireForgeRecipe read(ResourceLocation id, PacketBuffer buf) {
        int smeltTime = buf.readInt();
        ItemStack result = buf.readItemStack();

        NonNullList<Ingredient> inputs = NonNullList.withSize(9, Ingredient.EMPTY);

        for (int i = 0; i < 9; i++) {
            inputs.set(i, Ingredient.read(buf));
        }


        return new SoulfireForgeRecipe(id, result, smeltTime, inputs);
    }

    @Override
    public void write(PacketBuffer buf, SoulfireForgeRecipe recipe) {
        buf.writeInt(recipe.getSmeltTime());
        buf.writeItemStack(recipe.getRecipeOutput());

        for (Ingredient ingredient : recipe.getInputs()) {
            ingredient.write(buf);
        }
    }
}
