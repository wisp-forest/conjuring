package com.glisco.conjuringforgery.items;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

public class LesserConjurationEssence extends Item {

    public LesserConjurationEssence(Properties settings) {
        super(settings);
    }

    public LesserConjurationEssence() {
        this(new Properties().group(ConjuringForgery.CONJURING_GROUP).rarity(Rarity.RARE));
    }

}
