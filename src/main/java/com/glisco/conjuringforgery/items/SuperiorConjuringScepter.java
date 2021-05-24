package com.glisco.conjuringforgery.items;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.WorldHelper;
import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeTileEntity;
import com.glisco.conjuringforgery.entities.SoulProjectile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class SuperiorConjuringScepter extends ConjuringScepter {

    public SuperiorConjuringScepter(Properties properties) {
        super(properties);
    }

    public SuperiorConjuringScepter() {
        this(new Properties().group(ConjuringForgery.CONJURING_GROUP).maxStackSize(1).rarity(Rarity.RARE));
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (!(context.getWorld().getTileEntity(context.getPos()) instanceof SoulfireForgeTileEntity)) {
            return ConjuringScepter.onBlockUse(context);
        }

        SoulfireForgeTileEntity forge = (SoulfireForgeTileEntity) context.getWorld().getTileEntity(context.getPos());
        if (!forge.isRunning()) return ActionResultType.PASS;

        forge.finishInstantly();
        context.getPlayer().getCooldownTracker().setCooldown(ConjuringForgery.SUPERIOR_CONJURING_SCEPTER.get(), 30);

        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity user, Hand hand) {
        if(user.isSneaking()){
            return ConjuringScepter.onUse(user, hand);
        }

        user.setActiveHand(hand);
        return ActionResult.resultSuccess(user.getHeldItem(hand));
    }


    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if(!(user instanceof PlayerEntity)) return;
        if(72000 - remainingUseTicks < 20) return;

        if (!world.isRemote()) {
            SoulProjectile projectile = new SoulProjectile(world, user.getPosX(), user.getPosYEye(), user.getPosZ(), user);
            projectile.func_234612_a_(user, user.rotationPitch, user.rotationYaw, 0f, 1.5f, 1);

            world.addEntity(projectile);
            WorldHelper.playSound(world, user.getPosition(), 15, SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 2, 1);
        }

        if(!((PlayerEntity)user).abilities.isCreativeMode){
            ((PlayerEntity)user).getCooldownTracker().setCooldown(ConjuringForgery.SUPERIOR_CONJURING_SCEPTER.get(), 100);
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }
}
