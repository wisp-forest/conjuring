package com.glisco.conjuring.items;

import com.glisco.conjuring.Conjuring;
import io.wispforest.lavender.book.BookItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.nbt.NbtKey;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class EnchiridionItem extends BookItem {

    private static final NbtKey<Boolean> SANDWICH = new NbtKey<>("Sandwich", NbtKey.Type.BOOLEAN);

    public EnchiridionItem() {
        super(new OwoItemSettings().maxCount(1).group(Conjuring.CONJURING_GROUP), Conjuring.id("enchiridion"));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getPlayer().isSneaking()) return ActionResult.PASS;
        if (!context.getWorld().getBlockState(context.getBlockPos()).isOf(Blocks.SNOW_BLOCK)) return ActionResult.PASS;

        context.getStack().mutate(SANDWICH, sandwich -> !sandwich);
        return ActionResult.SUCCESS;
    }

    @Override
    public Text getName(ItemStack stack) {
        return stack.getOr(SANDWICH, false) ? Text.literal("Ice Cream Sandwich") : super.getName(stack);
    }

}
