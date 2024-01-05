package com.glisco.conjuring.blocks.soulfire_forge;

import com.glisco.conjuring.mixin.RawShapedRecipeDataAccessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Map;

public record SoulfireForgeRecipeModel(Map<Character, Ingredient> key, List<String> pattern, int smeltTime,
                                       ItemStack result) {

    public static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().flatXmap(rows -> {
        if (rows.size() != 3) {
            return DataResult.error(() -> "Invalid pattern: must have three rows");
        } else {
            for (var row : rows) {
                if (row.length() != 3) {
                    return DataResult.error(() -> "Invalid pattern: each row must be three characters");
                }
            }

            return DataResult.success(rows);
        }
    }, DataResult::success);

    public static final MapCodec<SoulfireForgeRecipeModel> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codecs.strictUnboundedMap(RawShapedRecipeDataAccessor.conjuring$keyEntryCodec(), Ingredient.DISALLOW_EMPTY_CODEC).fieldOf("key").forGetter(SoulfireForgeRecipeModel::key),
            PATTERN_CODEC.fieldOf("pattern").forGetter(SoulfireForgeRecipeModel::pattern),
            Codec.INT.fieldOf("smeltTime").forGetter(SoulfireForgeRecipeModel::smeltTime),
            ItemStack.RECIPE_RESULT_CODEC.fieldOf("result").forGetter(SoulfireForgeRecipeModel::result)
    ).apply(instance, SoulfireForgeRecipeModel::new));
}
