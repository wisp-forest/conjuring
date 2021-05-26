package com.glisco.conjuringforgery.items;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.items.soul_alloy_tools.SoulAlloyTool;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GemItem extends Item {

    public final SoulAlloyTool.SoulAlloyModifier modifier;

    public GemItem(SoulAlloyTool.SoulAlloyModifier modifier) {
        super(new Item.Properties().rarity(Rarity.UNCOMMON).group(ConjuringForgery.CONJURING_GROUP));
        this.modifier = modifier;
    }

    public SoulAlloyTool.SoulAlloyModifier getModifier() {
        return modifier;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(modifier.translation_key).setStyle(Style.EMPTY.setColor(Color.fromInt(modifier.textColor))));

    }
}
