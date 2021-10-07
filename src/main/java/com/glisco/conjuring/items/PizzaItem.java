package com.glisco.conjuring.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PizzaItem extends Item {

    public PizzaItem() {
        super(new Settings().food(new FoodComponent.Builder().hunger(20).saturationModifier(0.75f).build()));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.getStackInHand(hand).getOrCreateNbt().contains("Brinsa")) {
            user.getStackInHand(hand).getNbt().putBoolean("Brinsa", true);
            return TypedActionResult.success(user.getStackInHand(hand));
        } else {
            return super.use(world, user, hand);
        }
    }
}
