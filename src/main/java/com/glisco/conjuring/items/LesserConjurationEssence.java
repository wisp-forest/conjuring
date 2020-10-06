package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class LesserConjurationEssence extends Item {

    public LesserConjurationEssence(Settings settings) {
        super(settings);
    }

    public LesserConjurationEssence() {
        this(new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.RARE));
    }

}
