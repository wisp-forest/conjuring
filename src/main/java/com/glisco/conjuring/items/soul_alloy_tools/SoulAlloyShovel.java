package com.glisco.conjuring.items.soul_alloy_tools;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.entities.SoulMagnetEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulAlloyShovel extends ShovelItem implements SoulAlloyTool {

    public SoulAlloyShovel() {
        super(SoulAlloyToolMaterial.INSTANCE, 1.5f, -3.0f, new Settings().group(Conjuring.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer().isSneaking()) {
            return super.useOnBlock(context);
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (!world.isClient()) {

            ItemStack shovel = user.getStackInHand(hand);

            if (shovel.getOrCreateNbt().contains("CurrentProjectile") && ((ServerWorld) world).getEntity(shovel.getOrCreateNbt().getUuid("CurrentProjectile")) != null) {
                ((SoulMagnetEntity) ((ServerWorld) world).getEntity(shovel.getOrCreateNbt().getUuid("CurrentProjectile"))).recall();
            } else {
                SoulMagnetEntity magnet = new SoulMagnetEntity(world, user);
                magnet.refreshPositionAndAngles(user.getX(), user.getEyeY(), user.getZ(), 0, 0);
                magnet.setProperties(user, user.getPitch(), user.getYaw(), 0f, 1.5f, 1);
                world.spawnEntity(magnet);

                shovel.getOrCreateNbt().putUuid("CurrentProjectile", magnet.getUuid());
            }

        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.addAll(SoulAlloyTool.getTooltip(stack));
    }

}
