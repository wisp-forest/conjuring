package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.blocks.soulfireForge.SoulfireForgeBlockEntity;
import com.glisco.conjuring.entities.SoulProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class SuperiorConjuringScepter extends ConjuringScepter {

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
        if (!(context.getWorld().getBlockEntity(context.getBlockPos()) instanceof SoulfireForgeBlockEntity)) {
            return ConjuringScepter.onBlockUse(context);
        }

        SoulfireForgeBlockEntity forge = (SoulfireForgeBlockEntity) context.getWorld().getBlockEntity(context.getBlockPos());
        if (!forge.isRunning()) return ActionResult.PASS;

        forge.finishInstantly();
        context.getPlayer().getItemCooldownManager().set(ConjuringCommon.SUPERIOR_CONJURING_SCEPTER, 30);

        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()) {
            return ConjuringScepter.onUse(user, hand);
        }

        user.setCurrentHand(hand);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity)) return;
        if (72000 - remainingUseTicks < 20) return;

        if (!world.isClient()) {
            SoulProjectileEntity projectile = new SoulProjectileEntity(world, user);
            projectile.refreshPositionAndAngles(user.getX(), user.getEyeY(), user.getZ(), 0, 0);
            projectile.setProperties(user, user.pitch, user.yaw, 0f, 1.5f, 1);
            world.spawnEntity(projectile);

            world.playSound(null, user.getBlockPos(), SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 2, 1);
        }

        if (!((PlayerEntity) user).abilities.creativeMode) {
            ((PlayerEntity) user).getItemCooldownManager().set(ConjuringCommon.SUPERIOR_CONJURING_SCEPTER, 100);
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }
}
