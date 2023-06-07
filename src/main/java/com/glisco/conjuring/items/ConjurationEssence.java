package com.glisco.conjuring.items;

import com.glisco.conjuring.Conjuring;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Rarity;

public class ConjurationEssence extends Item {

    public ConjurationEssence(Settings settings) {
        super(settings);
    }

    public ConjurationEssence() {
        this(new OwoItemSettings().group(Conjuring.CONJURING_GROUP).rarity(Rarity.RARE));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getPlayer().isSneaking()) return ActionResult.PASS;
        if (!(context.getWorld().getBlockState(context.getBlockPos()).isIn(BlockTags.BASE_STONE_OVERWORLD))) return ActionResult.PASS;

        context.getStack().decrement(1);
        if (context.getStack().getCount() == 0) {
            context.getPlayer().setStackInHand(context.getHand(), ItemStack.EMPTY);
        }

        ItemScatterer.spawn(context.getWorld(), context.getHitPos().x, context.getHitPos().y, context.getHitPos().z, new ItemStack(ConjuringItems.LESSER_CONJURATION_ESSENCE, 4));

        return ActionResult.SUCCESS;
    }
}
