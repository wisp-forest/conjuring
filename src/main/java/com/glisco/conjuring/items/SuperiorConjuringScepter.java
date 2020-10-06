package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.WorldHelper;
import com.glisco.conjuring.blocks.soulfireForge.SoulfireForgeBlockEntity;
import com.glisco.conjuring.entities.SoulProjectile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SuperiorConjuringScepter extends Item {

    public SuperiorConjuringScepter(Settings settings) {
        super(settings);
    }

    public SuperiorConjuringScepter() {
        this(new Settings().group(ConjuringCommon.CONJURING_GROUP).maxCount(1).rarity(Rarity.RARE));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!(context.getWorld().getBlockEntity(context.getBlockPos()) instanceof SoulfireForgeBlockEntity)) return ActionResult.PASS;

        SoulfireForgeBlockEntity forge = (SoulfireForgeBlockEntity) context.getWorld().getBlockEntity(context.getBlockPos());
        if (!forge.isRunning()) return ActionResult.PASS;

        forge.finishInstantly();
        context.getPlayer().getItemCooldownManager().set(ConjuringCommon.SUPERIOR_CONJURING_SCEPTER, 30);

        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        SoulProjectile projectile = new SoulProjectile(world, user.getX(), user.getEyeY(), user.getZ(), user);
        projectile.setProperties(user, user.pitch, user.yaw, 0f, 1.5f, 1);
        if (!world.isClient()) {
            world.spawnEntity(projectile);
            WorldHelper.playSound(world, user.getBlockPos(), 15, SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 2, 1);
        }

        user.getItemCooldownManager().set(ConjuringCommon.SUPERIOR_CONJURING_SCEPTER, 100);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
