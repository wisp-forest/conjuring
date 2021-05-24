package com.glisco.conjuringforgery.items;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

public class SoulRod extends Item {

    public SoulRod(Properties properties) {
        super(properties);
    }

    public SoulRod() {
        this(new Properties().group(ConjuringForgery.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
