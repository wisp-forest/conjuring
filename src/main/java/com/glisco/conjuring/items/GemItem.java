package com.glisco.conjuring.items;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyTool;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GemItem extends Item {

    public final SoulAlloyTool.SoulAlloyModifier modifier;

    public GemItem(SoulAlloyTool.SoulAlloyModifier modifier) {
        super(new Settings().rarity(Rarity.UNCOMMON).group(Conjuring.CONJURING_GROUP));
        this.modifier = modifier;
    }

    public SoulAlloyTool.SoulAlloyModifier getModifier() {
        return modifier;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable(modifier.translation_key).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(modifier.textColor))));
        tooltip.add(Text.translatable(modifier.translation_key + ".description").formatted(Formatting.GRAY));
    }
}
