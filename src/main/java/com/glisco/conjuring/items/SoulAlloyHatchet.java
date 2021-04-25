package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.entities.SoulFellerEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulAlloyHatchet extends AxeItem implements SoulAlloyTool {

    public SoulAlloyHatchet() {
        super(SoulAlloyToolMaterial.INSTANCE, 5.0f, -3.0f, new Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (!world.isClient()) {
            SoulFellerEntity feller = new SoulFellerEntity(world, user);
            feller.refreshPositionAndAngles(user.getX(), user.getEyeY(), user.getZ(), 0, 0);
            feller.setProperties(user, user.pitch, user.yaw, 0f, 1.5f, 1);

            world.spawnEntity(feller);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.addAll(SoulAlloyTool.getTooltip(stack));
    }

    @Override
    public boolean canAddModifiers(ItemStack stack) {
        return stack.getOrCreateSubTag("Modifiers").getKeys().size() < 2;
    }

    @Override
    public void addModifier(ItemStack stack, SoulAlloyModifier modifier) {

        CompoundTag modifierTag = stack.getOrCreateSubTag("Modifiers");

        int level = modifierTag.contains(modifier.name()) ? modifierTag.getInt(modifier.name()) : 0;
        level++;

        modifierTag.putInt(modifier.name(), level);
    }
}
