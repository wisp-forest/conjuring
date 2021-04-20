package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.entities.SoulFellerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SoulAlloyHatchet extends AxeItem {

    public SoulAlloyHatchet() {
        super(SoulAlloyToolMaterial.INSTANCE, 5.0f, -3.0f, new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (!world.isClient()) {
            SoulFellerEntity feller = new SoulFellerEntity(world, user);
            feller.refreshPositionAndAngles(user.getX(), user.getEyeY(), user.getZ(), 0, 0);
            feller.setProperties(user, user.pitch, user.yaw, 0f, 1.5f, 1);

            world.spawnEntity(feller);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
