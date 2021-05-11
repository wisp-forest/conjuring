package com.glisco.conjuring.items.soul_alloy_tools;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.entities.SoulDiggerEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulAlloyPickaxe extends PickaxeItem implements SoulAlloyTool {

    public SoulAlloyPickaxe() {
        super(SoulAlloyToolMaterial.INSTANCE, 1, -2.8f, new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (!SoulAlloyTool.isSecondaryEnabled(user.getStackInHand(hand))) return TypedActionResult.pass(user.getStackInHand(hand));

        if (!world.isClient()) {
            SoulDiggerEntity digger = new SoulDiggerEntity(world, user);
            digger.refreshPositionAndAngles(user.getX(), user.getEyeY(), user.getZ(), 0, 0);
            digger.setProperties(user, user.pitch, user.yaw, 0f, 1.5f, 1);

            digger.setItem(user.getStackInHand(hand));

            world.spawnEntity(digger);

            user.getItemCooldownManager().set(ConjuringCommon.SOUL_ALLOY_PICKAXE, ConjuringCommon.CONFIG.tools_config.pickaxe_secondary_cooldown);
            user.getStackInHand(hand).damage(ConjuringCommon.CONFIG.tools_config.pickaxe_secondary_durability_cost, user, player -> player.sendToolBreakStatus(hand));
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.addAll(SoulAlloyTool.getTooltip(stack));
    }
}
