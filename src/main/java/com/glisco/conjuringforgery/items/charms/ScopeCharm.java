package com.glisco.conjuringforgery.items.charms;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

public class ScopeCharm extends Item {
    public ScopeCharm() {
        super(new Properties().rarity(Rarity.UNCOMMON).group(ConjuringForgery.CONJURING_GROUP).maxStackSize(8));
    }
}
