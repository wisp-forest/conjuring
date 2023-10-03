package com.glisco.conjuring.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.recipe.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapedRecipe.Serializer.class)
public interface ShapedRecipeSerializerAccessor {

    @Accessor("KEY_ENTRY_CODEC")
    static Codec<String> conjuring$keyEntryCodec() {
        throw new UnsupportedOperationException();
    }

}
