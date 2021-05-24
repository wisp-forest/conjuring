package com.glisco.conjuringforgery.items;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

public class GemSocket extends Item {

    public GemSocket(Properties properties) {
        super(properties);
    }

    public GemSocket() {
        this(new Properties().group(ConjuringForgery.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }
}
