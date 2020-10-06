package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class SoulAlloy extends Item {

    public SoulAlloy(Settings settings) {
        super(settings);
    }

    public SoulAlloy() {
        this(new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }
}
