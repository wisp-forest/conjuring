package com.glisco.conjuring.blocks.soul_weaver;

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

public class SoulWeaverRecipeSerializer implements RecipeSerializer<SoulWeaverRecipe> {

    private SoulWeaverRecipeSerializer() {}

    public static final SoulWeaverRecipeSerializer INSTANCE = new SoulWeaverRecipeSerializer();
    public static final Identifier ID = SoulWeaverRecipe.Type.ID;

    public static final Codec<SoulWeaverRecipe> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                RecipeCodecs.CRAFTING_RESULT.fieldOf("result").forGetter(o -> ItemStack.EMPTY),
                Codecs.validate(Ingredient.DISALLOW_EMPTY_CODEC.listOf(), ingredients -> {
                    if (ingredients.size() <= 5) {
                        return DataResult.success(ingredients);
                    } else {
                        return DataResult.error(() -> "Gem tinkerer recipes cannot have more than 5 inputs");
                    }
                }).fieldOf("inputs").forGetter(o -> List.of()),
                Codec.BOOL.fieldOf("transferTag").forGetter(o -> false)
        ).apply(instance, (stack, ingredients, transferTag) -> {
            var inputs = DefaultedList.ofSize(5, Ingredient.EMPTY);
            for (int i = 0; i < ingredients.size(); i++) {
                inputs.set(i, ingredients.get(i));
            }

            return new SoulWeaverRecipe(stack, inputs, transferTag);
        });
    });

    @Override
    public Codec<SoulWeaverRecipe> codec() {
        return CODEC;
    }
    @Override
    public SoulWeaverRecipe read(PacketByteBuf buf) {
        ItemStack result = buf.readItemStack();
        boolean transferTag = buf.readBoolean();

        var inputs = DefaultedList.ofSize(5, Ingredient.EMPTY);
        for (int i = 0; i < 5; i++) {
            inputs.set(i, Ingredient.fromPacket(buf));
        }

        return new SoulWeaverRecipe(result, inputs, transferTag);
    }

    @Override
    public void write(PacketByteBuf buf, SoulWeaverRecipe recipe) {
        buf.writeItemStack(recipe.getResult(null));
        buf.writeBoolean(recipe.transferTag);

        for (Ingredient ingredient : recipe.getInputs()) {
            ingredient.write(buf);
        }
    }
}
