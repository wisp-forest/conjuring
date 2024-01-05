package com.glisco.conjuring.items;

import com.glisco.conjuring.Conjuring;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import io.wispforest.owo.serialization.format.nbt.NbtEndec;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.List;

public class ConjuringFocus extends Item {

    private static final KeyedEndec<NbtCompound> ENTITY_KEY = NbtEndec.COMPOUND.keyed("Entity", (NbtCompound) null);

    private final boolean hasGlint;

    public ConjuringFocus(boolean hasGlint) {
        super(new OwoItemSettings().group(Conjuring.CONJURING_GROUP).maxCount(1).rarity(Rarity.UNCOMMON));
        this.hasGlint = hasGlint;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (!stack.has(ENTITY_KEY)) return;

        tooltip.add(Text.translatable(Util.createTranslationKey(
                "entity",
                Identifier.tryParse(stack.get(ENTITY_KEY).getString("id"))
        )).formatted(Formatting.GRAY));
    }

    public static ItemStack writeData(ItemStack focus, EntityType<?> entityType) {
        var entityTag = new NbtCompound();
        entityTag.putString("id", Registries.ENTITY_TYPE.getId(entityType).toString());

        focus.put(ENTITY_KEY, entityTag);
        return focus;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return this.hasGlint;
    }
}
