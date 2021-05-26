package com.glisco.conjuringforgery.items;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

public class CharmItem extends Item {

    public CharmItem() {
        super(new Properties().group(ConjuringForgery.CONJURING_GROUP).maxStackSize(8).rarity(Rarity.UNCOMMON));
    }
}
