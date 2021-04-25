package com.glisco.conjuring.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;

public interface SoulAlloyTool {

    boolean canAddModifiers(ItemStack stack);

    void addModifier(ItemStack stack, SoulAlloyModifier modifier);

    static List<Text> getTooltip(ItemStack stack) {

        List<Text> tooltip = new ArrayList<>();

        CompoundTag modifiers = stack.getOrCreateSubTag("Modifiers");

        for (String key : modifiers.getKeys()) {
            final SoulAlloyModifier modifier = SoulAlloyModifier.valueOf(key);
            tooltip.add(new TranslatableText(modifier.translation_key).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(modifier.textColor))).append(": §7" + modifiers.getInt(key)));
        }

        if (!tooltip.isEmpty()) {
            tooltip.add(0, new LiteralText("§7Modifiers"));
        }

        return tooltip;
    }

    enum SoulAlloyModifier {

        HASTE(0x007a18, "modifier.conjuring.haste"),
        ABUNDANCE(0xa80f01, "modifier.conjuring.abundance"),
        SCOPE(0x4d8184, "modifier.conjuring.scope"),
        IGNORANCE(0x123f89, "modifier.conjuring.ignorance");

        public final int textColor;
        public final String translation_key;

        SoulAlloyModifier(int textColor, String translation_key) {
            this.textColor = textColor;
            this.translation_key = translation_key;
        }
    }

}
