package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class CharmItem extends Item {

    public final SoulAlloyTool.SoulAlloyModifier modifier;

    public CharmItem(SoulAlloyTool.SoulAlloyModifier modifier) {
        super(new Item.Settings().rarity(Rarity.UNCOMMON).group(ConjuringCommon.CONJURING_GROUP).maxCount(8));
        this.modifier = modifier;
    }

    public SoulAlloyTool.SoulAlloyModifier getModifier() {
        return modifier;
    }
}
