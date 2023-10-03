package com.glisco.conjuring.blocks.gem_tinkerer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeCodecs;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;

public class GemTinkererRecipeSerializer implements RecipeSerializer<GemTinkererRecipe> {

    private GemTinkererRecipeSerializer() {
    }

    public static final GemTinkererRecipeSerializer INSTANCE = new GemTinkererRecipeSerializer();
    public static final Identifier ID = GemTinkererRecipe.Type.ID;

    public static final Codec<GemTinkererRecipe> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                RecipeCodecs.CRAFTING_RESULT.fieldOf("result").forGetter(o -> ItemStack.EMPTY),
                Codecs.validate(Ingredient.DISALLOW_EMPTY_CODEC.listOf(), ingredients -> {
                    if (ingredients.size() <= 5) {
                        return DataResult.success(ingredients);
                    } else {
                        return DataResult.error(() -> "Gem tinkerer recipes cannot have more than 5 inputs");
                    }
                }).fieldOf("inputs").forGetter(o -> List.of())
        ).apply(instance, (stack, ingredients) -> {
            var inputs = DefaultedList.ofSize(5, Ingredient.EMPTY);
            for (int i = 0; i < ingredients.size(); i++) {
                inputs.set(i, ingredients.get(i));
            }

            return new GemTinkererRecipe(stack, inputs);
        });
    });

    @Override
    public Codec<GemTinkererRecipe> codec() {
        return CODEC;
    }

    @Override
    public GemTinkererRecipe read(PacketByteBuf buf) {
        ItemStack result = buf.readItemStack();

        var inputs = DefaultedList.ofSize(5, Ingredient.EMPTY);
        for (int i = 0; i < 5; i++) {
            inputs.set(i, Ingredient.fromPacket(buf));
        }

        return new GemTinkererRecipe(result, inputs);
    }

    @Override
    public void write(PacketByteBuf buf, GemTinkererRecipe recipe) {
        buf.writeItemStack(recipe.getResult(null));

        for (Ingredient ingredient : recipe.getInputs()) {
            ingredient.write(buf);
        }
    }
}
