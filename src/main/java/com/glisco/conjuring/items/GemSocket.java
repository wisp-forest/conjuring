package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class GemSocket extends Item {

    public GemSocket(Settings settings) {
        super(settings);
    }

    public GemSocket() {
        this(new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }
}
