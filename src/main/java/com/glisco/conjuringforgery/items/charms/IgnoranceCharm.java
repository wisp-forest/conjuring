package com.glisco.conjuringforgery.items.charms;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

public class IgnoranceCharm extends Item {
    public IgnoranceCharm() {
        super(new Properties().rarity(Rarity.UNCOMMON).group(ConjuringForgery.CONJURING_GROUP).maxStackSize(8));
    }
}
