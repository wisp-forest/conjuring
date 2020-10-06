package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class ConjuringScepter extends Item {

    public ConjuringScepter(Settings settings) {
        super(settings);
    }

    public ConjuringScepter() {
        this(new Item.Settings().group(ConjuringCommon.CONJURING_GROUP).maxCount(1).rarity(Rarity.UNCOMMON));
    }
}
