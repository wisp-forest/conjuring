package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Rarity;

public class SoulAlloySword extends SwordItem {

    public SoulAlloySword() {
        super(SoulAlloyToolMaterial.INSTANCE, 3, -2.4f, new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }
}
