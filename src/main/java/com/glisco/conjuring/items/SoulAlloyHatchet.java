package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.AxeItem;
import net.minecraft.util.Rarity;

public class SoulAlloyHatchet extends AxeItem {

    public SoulAlloyHatchet() {
        super(SoulAlloyToolMaterial.INSTANCE, 5.0f, -3.0f, new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }
}
