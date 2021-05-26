package com.glisco.conjuringforgery.items.soul_alloy_tools;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.entities.SoulProjectileEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

public class SoulAlloySword extends SwordItem implements SoulAlloyTool {

    public SoulAlloySword() {
        super(SoulAlloyToolMaterial.INSTANCE, 3, -2.4f, new Properties().group(ConjuringForgery.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity user, Hand hand) {

        if (!SoulAlloyTool.isSecondaryEnabled(user.getHeldItem(hand))) return ActionResult.resultPass(user.getHeldItem(hand));

        if (!world.isRemote()) {

            float damage = (float) ((user.getAttributeValue(Attributes.ATTACK_DAMAGE) + EnchantmentHelper.getModifierForCreature(user.getHeldItemMainhand(), CreatureAttribute.UNDEFINED)) * 1.5f *  ConjuringForgery.CONFIG.tools_config.sword_projectile_damage_multiplier);

            for (int i = 0; i < 5; i++) {
                SoulProjectileEntity projectile = new SoulProjectileEntity(world, user);
                projectile.setLocationAndAngles(user.getPosX(), user.getPosYEye(), user.getPosZ(), 0, 0);
                projectile.setDirectionAndMovement(user, user.rotationPitch, user.rotationYaw - 10 + 5 * i, 0f, 1.5f, 1);
                projectile.setDamage(damage);

                world.addEntity(projectile);
            }

            user.getCooldownTracker().setCooldown(ConjuringForgery.SOUL_ALLOY_SWORD.get(), ConjuringForgery.CONFIG.tools_config.sword_secondary_cooldown);
            user.getHeldItem(hand).damageItem(ConjuringForgery.CONFIG.tools_config.sword_secondary_durability_cost, user, player -> player.sendBreakAnimation(hand));
        }

        return ActionResult.resultSuccess(user.getHeldItem(hand));
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag context) {
        tooltip.addAll(SoulAlloyTool.getTooltip(stack));
    }
}
