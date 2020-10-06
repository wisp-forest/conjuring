package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class ConjuringFocus extends Item {

    public ConjuringFocus() {
        super(new Item.Settings().group(ConjuringCommon.CONJURING_GROUP).maxCount(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        CompoundTag stackTag = stack.getOrCreateTag();

        if (stackTag.contains("Entity") || entity.getType().equals(EntityType.BEE)) {
            return ActionResult.PASS;
        }

        CompoundTag entityTag = new CompoundTag();
        entityTag.putString("id", Registry.ENTITY_TYPE.getId(entity.getType()).toString());

        stackTag.put("Entity", entityTag);
        stack.setTag(stackTag);
        user.setStackInHand(hand, stack);

        entity.remove();
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (!stack.getOrCreateTag().contains("Entity")) {
            return;
        }

        String entityName = "entity." + stack.getTag().getCompound("Entity").getString("id").replace(':', '.');
        tooltip.add(new TranslatableText(entityName).setStyle(Style.EMPTY.withColor(TextColor.parse("gray"))));
    }

    public static ItemStack create(EntityType<?> entityType) {
        ItemStack focus = new ItemStack(ConjuringCommon.CONJURING_FOCUS);
        CompoundTag stackTag = focus.getOrCreateTag();

        CompoundTag entityTag = new CompoundTag();
        entityTag.putString("id", Registry.ENTITY_TYPE.getId(entityType).toString());

        stackTag.put("Entity", entityTag);
        focus.setTag(stackTag);
        return focus;
    }
}
