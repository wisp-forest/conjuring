package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class SoulSlice extends Item {

    public SoulSlice(Settings settings) {
        super(settings);
    }

    public SoulSlice() {
        this(new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.RARE));
    }
}
