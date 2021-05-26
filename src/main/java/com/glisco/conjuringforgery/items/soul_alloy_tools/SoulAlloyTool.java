package com.glisco.conjuringforgery.items.soul_alloy_tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SoulAlloyTool {

    static void toggleEnabledState(ItemStack stack) {
        boolean currentState = stack.getOrCreateTag().contains("SecondaryEnabled") && stack.getOrCreateTag().getBoolean("SecondaryEnabled");
        currentState = !currentState;
        stack.getOrCreateTag().putBoolean("SecondaryEnabled", currentState);
    }

    static boolean isSecondaryEnabled(ItemStack stack) {
        return stack.getOrCreateTag().contains("SecondaryEnabled") && stack.getOrCreateTag().getBoolean("SecondaryEnabled");
    }

    static void addModifier(ItemStack stack, SoulAlloyModifier modifier) {
        CompoundNBT modifierTag = stack.getOrCreateChildTag("Modifiers");

        int level = modifierTag.contains(modifier.name()) ? modifierTag.getInt(modifier.name()) : 0;
        level++;

        modifierTag.putInt(modifier.name(), level);
    }

    static boolean canAddModifier(ItemStack stack, SoulAlloyModifier modifier) {
        CompoundNBT modifierTag = stack.getOrCreateChildTag("Modifiers");

        if (modifierTag.keySet().size() >= 2 && getModifierLevel(stack, modifier) == 0) return false;

        if (getModifierLevel(stack, modifier) >= 3) return false;

        return modifierTag.keySet().stream().mapToInt(modifierTag::getInt).sum() < 5;
    }

    static boolean canAddModifiers(ItemStack stack, List<SoulAlloyModifier> modifiers) {

        HashMap<SoulAlloyModifier, Integer> modifierMap = getModifiers(stack);

        for (SoulAlloyModifier modifier : modifiers) {
            if (!modifierMap.containsKey(modifier)) {
                modifierMap.put(modifier, 1);
            } else {
                modifierMap.put(modifier, modifierMap.get(modifier) + 1);
            }
        }

        for (Map.Entry<SoulAlloyModifier, Integer> entry : modifierMap.entrySet()) {
            if (entry.getValue() < 3) continue;
            if (entry.getValue() > 3) return false;
            if (modifierMap.entrySet().stream().anyMatch(currentEntry -> currentEntry != entry && currentEntry.getValue() > 2)) return false;
        }

        return modifierMap.size() <= 2;

    }

    static List<ITextComponent> getTooltip(ItemStack stack) {

        List<ITextComponent> tooltip = new ArrayList<>();
        CompoundNBT modifiers = stack.getOrCreateChildTag("Modifiers");

        for (String key : modifiers.keySet()) {
            final SoulAlloyModifier modifier = SoulAlloyModifier.valueOf(key);

            StringBuilder level = new StringBuilder();
            for (int i = 0; i < modifiers.getInt(key); i++) {
                level.append("●");
            }

            tooltip.add(new TranslationTextComponent(modifier.translation_key).setStyle(Style.EMPTY.setColor(Color.fromInt(modifier.textColor))).appendString(": §7" + level));
        }

        if (!tooltip.isEmpty() && stack.isEnchanted()) {
            tooltip.add(new StringTextComponent(""));
        }

        return tooltip;
    }

    static int getModifierLevel(ItemStack stack, SoulAlloyModifier modifier) {
        return stack.getOrCreateChildTag("Modifiers").contains(modifier.name()) ? stack.getOrCreateChildTag("Modifiers").getInt(modifier.name()) : 0;
    }

    static HashMap<SoulAlloyModifier, Integer> getModifiers(ItemStack stack) {
        HashMap<SoulAlloyModifier, Integer> modifierMap = new HashMap<>();
        CompoundNBT modifierTag = stack.getOrCreateChildTag("Modifiers");
        modifierTag.keySet().forEach(s -> modifierMap.put(SoulAlloyModifier.valueOf(s), modifierTag.getInt(s)));
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
