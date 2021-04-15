package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.Items;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Rarity;

public class SoulAlloyShovel extends ShovelItem {

    public SoulAlloyShovel() {
        super(SoulAlloyToolMaterial.INSTANCE, 1.5f, -3.0f, new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }
}
