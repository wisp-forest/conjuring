package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class CharmItem extends Item {

    public CharmItem() {
        super(new Settings().group(ConjuringCommon.CONJURING_GROUP).maxCount(8).rarity(Rarity.UNCOMMON));
    }
}
