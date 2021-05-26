package com.glisco.conjuringforgery.items.soul_alloy_tools;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.entities.SoulMagnetEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class SoulAlloyShovel extends ShovelItem implements SoulAlloyTool {

    public SoulAlloyShovel() {
        super(SoulAlloyToolMaterial.INSTANCE, 1.5f, -3.0f, new Properties().group(ConjuringForgery.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }
    
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (context.getPlayer().isSneaking()) {
            return super.onItemUse(context);
        } else {
            return ActionResultType.PASS;
        }
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity user, Hand hand) {

        if (!world.isRemote()) {

            ItemStack shovel = user.getHeldItem(hand);

            if (shovel.getOrCreateTag().contains("CurrentProjectile") && ((ServerWorld) world).getEntityByUuid(shovel.getOrCreateTag().getUniqueId("CurrentProjectile")) != null) {
                ((SoulMagnetEntity) ((ServerWorld) world).getEntityByUuid(shovel.getOrCreateTag().getUniqueId("CurrentProjectile"))).recall();
            } else {
                SoulMagnetEntity magnet = new SoulMagnetEntity(world, user);
                magnet.setLocationAndAngles(user.getPosX(), user.getPosYEye(), user.getPosZ(), 0, 0);
                magnet.setDirectionAndMovement(user, user.rotationPitch, user.rotationYaw, 0f, 1.5f, 1);
                world.addEntity(magnet);

                shovel.getOrCreateTag().putUniqueId("CurrentProjectile", magnet.getUniqueID());
            }

        }

        return ActionResult.resultSuccess(user.getHeldItem(hand));
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag context) {
        tooltip.addAll(SoulAlloyTool.getTooltip(stack));
    }

}
