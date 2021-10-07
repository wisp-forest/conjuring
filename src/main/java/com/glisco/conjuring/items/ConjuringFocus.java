package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class ConjuringFocus extends Item {

    public ConjuringFocus() {
        super(new Settings().group(ConjuringCommon.CONJURING_GROUP).maxCount(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (!stack.getOrCreateNbt().contains("Entity")) {
            return;
        }

        String entityName = "entity." + stack.getNbt().getCompound("Entity").getString("id").replace(':', '.');
        tooltip.add(new TranslatableText(entityName).setStyle(Style.EMPTY.withColor(TextColor.parse("gray"))));
    }

    public static ItemStack writeData(ItemStack focus, EntityType<?> entityType) {
        NbtCompound stackTag = focus.getOrCreateNbt();

        NbtCompound entityTag = new NbtCompound();
        entityTag.putString("id", Registry.ENTITY_TYPE.getId(entityType).toString());

        stackTag.put("Entity", entityTag);
        focus.setNbt(stackTag);
        return focus;
    }
}
