package com.glisco.conjuringforgery.items;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

public class SoulAlloy extends Item {

    public SoulAlloy(Properties properties) {
        super(properties);
    }

    public SoulAlloy() {
        this(new Properties().group(ConjuringForgery.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }
}
