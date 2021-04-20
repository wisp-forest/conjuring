package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.entities.SoulProjectileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SoulAlloySword extends SwordItem {

    public SoulAlloySword() {
        super(SoulAlloyToolMaterial.INSTANCE, 3, -2.4f, new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (!world.isClient()) {
            SoulProjectileEntity projectile = new SoulProjectileEntity(world, user);
            projectile.refreshPositionAndAngles(user.getX(), user.getEyeY(), user.getZ(), 0, 0);
            projectile.setProperties(user, user.pitch, user.yaw, 0f, 1.5f, 1);
            projectile.setDamage(25f);

            world.spawnEntity(projectile);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
