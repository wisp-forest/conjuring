package com.glisco.conjuring.items.charms;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class HasteCharm extends Item {
    public HasteCharm() {
        super(new Item.Settings().rarity(Rarity.UNCOMMON).group(ConjuringCommon.CONJURING_GROUP).maxCount(6));
    }
}
