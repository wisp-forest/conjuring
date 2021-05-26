package com.glisco.conjuringforgery.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class PizzaItem extends Item {

    public PizzaItem() {
        super(new Properties().food(new Food.Builder().hunger(20).saturation(0.75f).build()));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity user, Hand hand) {
        if (!user.getHeldItem(hand).getOrCreateTag().contains("Brinsa")) {
            user.getHeldItem(hand).getTag().putBoolean("Brinsa", true);
            return ActionResult.resultSuccess(user.getHeldItem(hand));
        } else {
            return super.onItemRightClick(world, user, hand);
        }
    }
}
