package com.glisco.conjuringforgery.blocks.gem_tinkerer;

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

public class GemTinkererRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GemTinkererRecipe> {

    private GemTinkererRecipeSerializer() {
    }

    public static final GemTinkererRecipeSerializer INSTANCE = new GemTinkererRecipeSerializer();
    public static final ResourceLocation ID = GemTinkererRecipe.Type.ID;

    @Override
    public GemTinkererRecipe read(ResourceLocation id, JsonObject json) {
        GemTinkererRecipeJson recipe = new Gson().fromJson(json, GemTinkererRecipeJson.class);

        if (recipe.inputs == null || recipe.result == null) {
            throw new JsonSyntaxException("Missing recipe attributes");
        }

        NonNullList<Ingredient> inputs = NonNullList.withSize(5, Ingredient.EMPTY);

        int index = 0;
        for (JsonElement element : recipe.inputs) {
            if (!element.isJsonObject()) continue;
            inputs.set(index, Ingredient.deserialize(element.getAsJsonObject()));
            index++;
        }

        Item resultItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryCreate(recipe.result.get("item").getAsString()));
        ItemStack result = new ItemStack(resultItem, recipe.result.get("count").getAsInt());

        return new GemTinkererRecipe(id, result, inputs);
    }

    @Override
    public GemTinkererRecipe read(ResourceLocation id, PacketBuffer buf) {
        ItemStack result = buf.readItemStack();

        NonNullList<Ingredient> inputs = NonNullList.withSize(5, Ingredient.EMPTY);

        for (int i = 0; i < 5; i++) {
            inputs.set(i, Ingredient.read(buf));
        }

        return new GemTinkererRecipe(id, result, inputs);
    }

    @Override
    public void write(PacketBuffer buf, GemTinkererRecipe recipe) {
        buf.writeItemStack(recipe.getRecipeOutput());

        for (Ingredient ingredient : recipe.getInputs()) {
            ingredient.write(buf);
        }
    }
}
