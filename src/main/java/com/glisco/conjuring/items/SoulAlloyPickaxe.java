package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.entities.SoulDiggerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SoulAlloyPickaxe extends PickaxeItem implements SoulAlloyTool {

    public SoulAlloyPickaxe() {
        super(SoulAlloyToolMaterial.INSTANCE, 1, -2.8f, new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (!world.isClient()) {
            SoulDiggerEntity digger = new SoulDiggerEntity(world, user);
            digger.refreshPositionAndAngles(user.getX(), user.getEyeY(), user.getZ(), 0, 0);
            digger.setProperties(user, user.pitch, user.yaw, 0f, 1.5f, 1);

            world.spawnEntity(digger);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
