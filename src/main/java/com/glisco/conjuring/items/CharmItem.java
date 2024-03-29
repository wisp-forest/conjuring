package com.glisco.conjuring.items;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyTool;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.ops.TextOps;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CharmItem extends Item {

    public final SoulAlloyTool.SoulAlloyModifier modifier;

    public CharmItem(SoulAlloyTool.SoulAlloyModifier modifier) {
        super(new OwoItemSettings().rarity(Rarity.UNCOMMON).maxCount(8).group(Conjuring.CONJURING_GROUP));
        this.modifier = modifier;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(TextOps.translateWithColor(modifier.translation_key, modifier.textColor));
        tooltip.add(Text.translatable(modifier.translation_key + ".description").formatted(Formatting.GRAY));
    }
}
