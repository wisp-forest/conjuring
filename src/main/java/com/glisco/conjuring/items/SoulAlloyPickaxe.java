package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.Rarity;

public class SoulAlloyPickaxe extends PickaxeItem {

    public SoulAlloyPickaxe() {
        super(SoulAlloyToolMaterial.INSTANCE, 1, -2.8f, new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }
}
