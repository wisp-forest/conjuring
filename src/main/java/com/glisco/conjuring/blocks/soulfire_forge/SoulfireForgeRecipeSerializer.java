package com.glisco.conjuring.blocks.soulfire_forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashMap;

public class SoulfireForgeRecipeSerializer implements RecipeSerializer<SoulfireForgeRecipe> {

    private SoulfireForgeRecipeSerializer() {}

    public static final SoulfireForgeRecipeSerializer INSTANCE = new SoulfireForgeRecipeSerializer();
    public static final Identifier ID = SoulfireForgeRecipe.Type.ID;

    public static final Codec<SoulfireForgeRecipe> CODEC = SoulfireForgeRecipeModel.CODEC.flatXmap(model -> {
        var keys = new HashMap<Character, Ingredient>();
        for (var key : model.key().entrySet()) {
            keys.put(key.getKey().charAt(0), key.getValue());
        }

        var inputs = DefaultedList.ofSize(9, Ingredient.EMPTY);

        int rowIdx = 0;
        for (var row : model.pattern()) {
            int columnIdx = 0;
            for (char c : row.toCharArray()) {
                if (c == ' ') {
                    inputs.set(rowIdx * 3 + columnIdx, Ingredient.EMPTY);
                } else {
                    var ingredient = keys.get(c);
                    if (ingredient == null) {
                        return DataResult.error(() -> "Pattern references symbol '" + c + "' which was not defined in key");
                    }

                    inputs.set(rowIdx * 3 + columnIdx, ingredient);
                }
                columnIdx++;
            }

            rowIdx++;
        }

        return DataResult.success(new SoulfireForgeRecipe(model.result(), model.smeltTime(), inputs));
    }, soulfireForgeRecipe -> DataResult.error(() -> "don't serialize recipes"));


    @Override
    public Codec<SoulfireForgeRecipe> codec() {
        return CODEC;
    }

    @Override
    public SoulfireForgeRecipe read(PacketByteBuf buf) {
        int smeltTime = buf.readInt();
        var result = buf.readItemStack();

        var inputs = DefaultedList.ofSize(9, Ingredient.EMPTY);
        for (int i = 0; i < 9; i++) {
            inputs.set(i, Ingredient.fromPacket(buf));
        }

        return new SoulfireForgeRecipe(result, smeltTime, inputs);
    }

    @Override
    public void write(PacketByteBuf buf, SoulfireForgeRecipe recipe) {
        buf.writeInt(recipe.getSmeltTime());
        buf.writeItemStack(recipe.getResult(null));

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.write(buf);
        }
    }
}
