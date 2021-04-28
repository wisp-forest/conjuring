package com.glisco.conjuring.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface SoulAlloyTool {

    static void addModifier(ItemStack stack, SoulAlloyModifier modifier) {
        CompoundTag modifierTag = stack.getOrCreateSubTag("Modifiers");

        int level = modifierTag.contains(modifier.name()) ? modifierTag.getInt(modifier.name()) : 0;
        level++;

        modifierTag.putInt(modifier.name(), level);
    }

    static boolean canAddModifier(ItemStack stack, SoulAlloyModifier modifier) {
        CompoundTag modifierTag = stack.getOrCreateSubTag("Modifiers");

        return modifierTag.getKeys().size() < 2 || (modifierTag.contains(modifier.name()) ? modifierTag.getInt(modifier.name()) : 0) == 1;
    }

    static boolean canAddModifiers(ItemStack stack, List<SoulAlloyModifier> modifiers) {

        HashMap<SoulAlloyModifier, Integer> modifierMap = getModifiers(stack);

        for (SoulAlloyModifier modifier : modifiers) {

            if (SoulAlloyTool.canAddModifier(stack, modifier)) {
                if (!modifierMap.containsKey(modifier)) {
                    modifierMap.put(modifier, 1);
                } else {
                    modifierMap.put(modifier, modifierMap.get(modifier) + 1);
                }
            } else {
                return false;
            }
        }

        return modifierMap.keySet().stream().allMatch(modifier -> modifierMap.get(modifier) <= 2) && modifierMap.size() <= 2;

    }

    static List<Text> getTooltip(ItemStack stack) {

        List<Text> tooltip = new ArrayList<>();

        CompoundTag modifiers = stack.getOrCreateSubTag("Modifiers");

        for (String key : modifiers.getKeys()) {
            final SoulAlloyModifier modifier = SoulAlloyModifier.valueOf(key);

            StringBuilder level = new StringBuilder();
            for (int i = 0; i < modifiers.getInt(key); i++) {
                level.append("●");
            }

            tooltip.add(new TranslatableText(modifier.translation_key).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(modifier.textColor))).append(": §7" + level));
        }

        if (!tooltip.isEmpty() && stack.hasEnchantments()) {
            tooltip.add(new LiteralText(""));
        }

        return tooltip;
    }

    static HashMap<SoulAlloyModifier, Integer> getModifiers(ItemStack stack) {
        HashMap<SoulAlloyModifier, Integer> modifierMap = new HashMap<>();
        CompoundTag modifierTag = stack.getOrCreateSubTag("Modifiers");
        modifierTag.getKeys().forEach(s -> modifierMap.put(SoulAlloyModifier.valueOf(s), modifierTag.getInt(s)));
        return modifierMap;
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
