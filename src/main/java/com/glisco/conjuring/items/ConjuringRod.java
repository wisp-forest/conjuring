package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;

public class ConjuringRod extends Item {

    public ConjuringRod(Settings settings) {
        super(settings);
    }

    public ConjuringRod() {
        this(new Item.Settings().group(ConjuringCommon.CONJURING_GROUP).maxCount(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).isOf(Blocks.SPAWNER)) {
            context.getPlayer().sendMessage(new LiteralText("clicked"), true);
            return ActionResult.SUCCESS;
        } else {
            return super.useOnBlock(context);
        }
    }

}
