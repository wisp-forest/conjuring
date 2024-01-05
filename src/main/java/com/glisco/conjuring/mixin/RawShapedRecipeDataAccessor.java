package com.glisco.conjuring.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RawShapedRecipe.Data.class)
public interface RawShapedRecipeDataAccessor {

    @Accessor("KEY_ENTRY_CODEC")
    static Codec<Character> conjuring$keyEntryCodec() {
        throw new UnsupportedOperationException();
    }

}
