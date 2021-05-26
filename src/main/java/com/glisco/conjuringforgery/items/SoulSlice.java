package com.glisco.conjuringforgery.items;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

public class SoulSlice extends Item {

    public SoulSlice(Properties settings) {
        super(settings);
    }

    public SoulSlice() {
        this(new Properties().group(ConjuringForgery.CONJURING_GROUP).rarity(Rarity.RARE));
    }
}
