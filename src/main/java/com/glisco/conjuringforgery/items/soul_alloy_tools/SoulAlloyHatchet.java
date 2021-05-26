package com.glisco.conjuringforgery.items.soul_alloy_tools;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.entities.SoulFellerEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

public class SoulAlloyHatchet extends AxeItem implements SoulAlloyTool {

    public SoulAlloyHatchet() {
        super(SoulAlloyToolMaterial.INSTANCE, 5.0f, -3.0f, new Properties().group(ConjuringForgery.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (SoulAlloyTool.isSecondaryEnabled(context.getItem())) {
            return ActionResultType.PASS;
        } else {
            return super.onItemUse(context);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity user, Hand hand) {

        if (!SoulAlloyTool.isSecondaryEnabled(user.getHeldItem(hand))) return ActionResult.resultPass(user.getHeldItem(hand));

        if (!world.isRemote()) {
            SoulFellerEntity feller = new SoulFellerEntity(world, user);
            feller.setLocationAndAngles(user.getPosX(), user.getPosYEye(), user.getPosZ(), 0, 0);
            feller.setDirectionAndMovement(user, user.rotationPitch, user.rotationYaw, 0f, 1.5f, 1);

            feller.setItem(user.getHeldItem(hand));

            int scopeGems = SoulAlloyTool.getModifierLevel(user.getHeldItem(hand), SoulAlloyModifier.SCOPE);
            if (scopeGems > 0) {
                feller.setMaxBlocks((int) (8 + Math.pow(scopeGems, ConjuringForgery.CONFIG.tools_config.axe_scope_exponent) * 8));
            }

            world.addEntity(feller);

            user.getCooldownTracker().setCooldown(ConjuringForgery.SOUL_ALLOY_HATCHET.get(), ConjuringForgery.CONFIG.tools_config.axe_secondary_cooldown);
            user.getHeldItem(hand).damageItem(ConjuringForgery.CONFIG.tools_config.axe_secondary_base_durability_cost + ConjuringForgery.CONFIG.tools_config.axe_secondary_per_scope_durability_cost * scopeGems, user, player -> player.sendBreakAnimation(hand));

        }
        return ActionResult.resultSuccess(user.getHeldItem(hand));
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag context) {
        tooltip.addAll(SoulAlloyTool.getTooltip(stack));
    }

}
