package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.entities.SoulMagnetEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulAlloyShovel extends ShovelItem implements SoulAlloyTool{

    public SoulAlloyShovel() {
        super(SoulAlloyToolMaterial.INSTANCE, 1.5f, -3.0f, new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (!world.isClient()) {

            ItemStack shovel = user.getStackInHand(hand);

            if (shovel.getOrCreateTag().contains("CurrentProjectile") && ((ServerWorld) world).getEntity(shovel.getOrCreateTag().getUuid("CurrentProjectile")) != null) {
                ((SoulMagnetEntity) ((ServerWorld) world).getEntity(shovel.getOrCreateTag().getUuid("CurrentProjectile"))).recall();
            } else {
                SoulMagnetEntity magnet = new SoulMagnetEntity(world, user);
                magnet.refreshPositionAndAngles(user.getX(), user.getEyeY(), user.getZ(), 0, 0);
                magnet.setProperties(user, user.pitch, user.yaw, 0f, 1.5f, 1);
                world.spawnEntity(magnet);

                shovel.getOrCreateTag().putUuid("CurrentProjectile", magnet.getUuid());
            }

        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.addAll(SoulAlloyTool.getTooltip(stack));
    }
}
