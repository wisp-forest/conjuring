package com.glisco.conjuringforgery.items;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class ConjuringFocus extends Item {

    public ConjuringFocus() {
        super(new Properties().group(ConjuringForgery.CONJURING_GROUP).maxStackSize(1).rarity(Rarity.UNCOMMON));
    }


    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag context) {
        if (!stack.getOrCreateTag().contains("Entity")) {
            return;
        }

        String entityName = "entity." + stack.getTag().getCompound("Entity").getString("id").replace(':', '.');
        tooltip.add(new TranslationTextComponent(entityName).mergeStyle(TextFormatting.GRAY));
    }

    public static ItemStack create(EntityType<?> entityType) {
        ItemStack focus = new ItemStack(ConjuringForgery.CONJURING_FOCUS.get());
        CompoundNBT stackTag = focus.getOrCreateTag();

        CompoundNBT entityTag = new CompoundNBT();
        entityTag.putString("id", entityType.getRegistryName().toString());

        stackTag.put("Entity", entityTag);
        focus.setTag(stackTag);
        return focus;
    }
}
