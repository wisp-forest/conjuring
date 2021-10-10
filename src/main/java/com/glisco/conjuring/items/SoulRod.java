package com.glisco.conjuring.items;

import com.glisco.conjuring.Conjuring;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class SoulRod extends Item {

    public SoulRod(Settings settings) {
        super(settings);
    }

    public SoulRod() {
        this(new Settings().group(Conjuring.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
