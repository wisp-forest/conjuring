package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class SoulBrick extends Item {

    public SoulBrick(Settings settings) {
        super(settings);
    }

    public SoulBrick() {
        this(new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.RARE));
    }
}
