package com.glisco.conjuringforgery.items.soul_alloy_tools;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.entities.SoulDiggerEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

public class SoulAlloyPickaxe extends PickaxeItem implements SoulAlloyTool {

    public SoulAlloyPickaxe() {
        super(SoulAlloyToolMaterial.INSTANCE, 1, -2.8f, new Properties().group(ConjuringForgery.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity user, Hand hand) {

        if (!SoulAlloyTool.isSecondaryEnabled(user.getHeldItem(hand))) return ActionResult.resultPass(user.getHeldItem(hand));

        if (!world.isRemote()) {
            SoulDiggerEntity digger = new SoulDiggerEntity(world, user);
            digger.setLocationAndAngles(user.getPosX(), user.getPosYEye(), user.getPosZ(), 0, 0);
            digger.setDirectionAndMovement(user, user.rotationPitch, user.rotationYaw, 0f, 1.5f, 1);

            digger.setItem(user.getHeldItem(hand));

            world.addEntity(digger);

            user.getCooldownTracker().setCooldown(ConjuringForgery.SOUL_ALLOY_PICKAXE.get(), ConjuringForgery.CONFIG.tools_config.pickaxe_secondary_cooldown);
            user.getHeldItem(hand).damageItem(ConjuringForgery.CONFIG.tools_config.pickaxe_secondary_durability_cost, user, player -> player.sendBreakAnimation(hand));
        }

        return ActionResult.resultSuccess(user.getHeldItem(hand));
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag context) {
        tooltip.addAll(SoulAlloyTool.getTooltip(stack));
    }
}
