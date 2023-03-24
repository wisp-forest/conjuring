package com.glisco.conjuring.items.soul_alloy_tools;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.entities.SoulProjectileEntity;
import com.glisco.conjuring.items.ConjuringItems;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulAlloySword extends SwordItem implements SoulAlloyTool {

    public SoulAlloySword() {
        super(SoulAlloyToolMaterial.INSTANCE, 3, -2.4f, new OwoItemSettings().group(Conjuring.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (!SoulAlloyTool.isSecondaryEnabled(user.getStackInHand(hand))) return TypedActionResult.pass(user.getStackInHand(hand));

        if (!world.isClient()) {

            float damage = (float) ((user.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + EnchantmentHelper.getAttackDamage(user.getMainHandStack(), EntityGroup.DEFAULT)) * 1.5f * Conjuring.CONFIG.tools_config.sword_projectile_damage_multiplier());

            for (int i = 0; i < 5; i++) {
                SoulProjectileEntity projectile = new SoulProjectileEntity(world, user);
                projectile.refreshPositionAndAngles(user.getX(), user.getEyeY(), user.getZ(), 0, 0);
                projectile.setVelocity(user, user.getPitch(), user.getYaw() - 10 + 5 * i, 0f, 1.5f, 1);
                projectile.setDamage(damage);

                world.spawnEntity(projectile);
            }

            user.getItemCooldownManager().set(ConjuringItems.SOUL_ALLOY_SWORD, Conjuring.CONFIG.tools_config.sword_secondary_cooldown());
            user.getStackInHand(hand).damage(Conjuring.CONFIG.tools_config.sword_secondary_durability_cost(), user, player -> player.sendToolBreakStatus(hand));
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return SoulAlloyTool.isSecondaryEnabled(stack) || super.isItemBarVisible(stack);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return SoulAlloyTool.isSecondaryEnabled(stack)
                ? 0x00FFFFF
                : super.getItemBarColor(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.addAll(SoulAlloyTool.getTooltip(stack));
    }
}
