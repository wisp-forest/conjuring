package com.glisco.conjuringforgery.items;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResultType;

public class ConjurationEssence extends Item {

    public ConjurationEssence(Properties settings) {
        super(settings);
    }

    public ConjurationEssence() {
        this(new Properties().group(ConjuringForgery.CONJURING_GROUP).rarity(Rarity.RARE));
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (!context.getPlayer().isSneaking()) return ActionResultType.PASS;
        if (!(context.getWorld().getBlockState(context.getPos()).getMaterial() == Material.ROCK)) return ActionResultType.PASS;

        context.getItem().shrink(1);
        if (context.getItem().getCount() == 0) {
            context.getPlayer().setHeldItem(context.getHand(), ItemStack.EMPTY);
        }

        InventoryHelper.spawnItemStack(context.getWorld(), context.getHitVec().x, context.getHitVec().y, context.getHitVec().z, new ItemStack(ConjuringForgery.LESSER_CONJURATION_ESSENCE.get(), 4));

        return ActionResultType.SUCCESS;
    }
}
