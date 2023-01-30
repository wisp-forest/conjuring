package com.glisco.conjuring.items;

import com.glisco.conjuring.Conjuring;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.List;

public class ConjuringFocus extends Item {

    private final boolean hasGlint;

    public ConjuringFocus(boolean hasGlint) {
        super(new OwoItemSettings().group(Conjuring.CONJURING_GROUP).maxCount(1).rarity(Rarity.UNCOMMON));
        this.hasGlint = hasGlint;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (!stack.getOrCreateNbt().contains("Entity")) {
            return;
        }

        String entityName = "entity." + stack.getNbt().getCompound("Entity").getString("id").replace(':', '.');
        tooltip.add(Text.translatable(entityName).formatted(Formatting.GRAY));
    }

    public static ItemStack writeData(ItemStack focus, EntityType<?> entityType) {
        NbtCompound stackTag = focus.getOrCreateNbt();

        NbtCompound entityTag = new NbtCompound();
        entityTag.putString("id", Registries.ENTITY_TYPE.getId(entityType).toString());

        stackTag.put("Entity", entityTag);
        focus.setNbt(stackTag);
        return focus;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return hasGlint;
    }
}
