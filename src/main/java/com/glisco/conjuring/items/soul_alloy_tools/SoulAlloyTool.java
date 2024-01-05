package com.glisco.conjuring.items.soul_alloy_tools;

import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface SoulAlloyTool {

    String MODIFIERS_KEY = "Modifiers";
    KeyedEndec<Boolean> SECONDARY_ENABLED = Endec.BOOLEAN.keyed("SecondaryEnabled", false);

    default boolean canAoeDig() {
        return false;
    }

    default Predicate<BlockState> getAoeToolOverridePredicate() {
        return SoulAlloyToolAbilities.NO_TOOL_OVERRIDE;
    }

    static void toggleEnabledState(ItemStack stack) {
        stack.mutate(SECONDARY_ENABLED, enabled -> !enabled);
    }

    static boolean isSecondaryEnabled(ItemStack stack) {
        return stack.get(SECONDARY_ENABLED);
    }

    static void addModifier(ItemStack stack, SoulAlloyModifier modifier) {
        NbtCompound modifierTag = stack.getOrCreateSubNbt(MODIFIERS_KEY);

        int level = modifierTag.contains(modifier.name()) ? modifierTag.getInt(modifier.name()) : 0;
        level++;

        modifierTag.putInt(modifier.name(), level);
    }

    static boolean canAddModifier(ItemStack stack, SoulAlloyModifier modifier) {
        NbtCompound modifierTag = stack.getOrCreateSubNbt(MODIFIERS_KEY);

        if (modifierTag.getKeys().size() >= 2 && getModifierLevel(stack, modifier) == 0) return false;

        if (getModifierLevel(stack, modifier) >= 3) return false;

        return modifierTag.getKeys().stream().mapToInt(modifierTag::getInt).sum() < 5;
    }

    static boolean canAddModifiers(ItemStack stack, List<SoulAlloyModifier> modifiers) {

        var modifierMap = getModifiers(stack);

        for (SoulAlloyModifier modifier : modifiers) {
            if (!modifierMap.containsKey(modifier)) {
                modifierMap.put(modifier, 1);
            } else {
                modifierMap.put(modifier, modifierMap.get(modifier) + 1);
            }
        }

        for (var entry : modifierMap.entrySet()) {
            if (entry.getValue() < 3) continue;
            if (entry.getValue() > 3) return false;
            if (modifierMap.entrySet().stream().anyMatch(currentEntry -> currentEntry != entry && currentEntry.getValue() > 2))
                return false;
        }

        return modifierMap.size() <= 2;

    }

    static List<Text> getTooltip(ItemStack stack) {
        if (stack.getSubNbt(MODIFIERS_KEY) == null) return List.of();

        var tooltip = new ArrayList<Text>();
        var modifiers = stack.getOrCreateSubNbt(MODIFIERS_KEY);

        for (String key : modifiers.getKeys()) {
            var level = "●".repeat(Math.max(0, modifiers.getInt(key)));

            var modifier = SoulAlloyModifier.valueOf(key);
            tooltip.add(Text.translatable(modifier.translation_key).styled(style -> style.withColor(modifier.textColor)).append(": ").append(TextOps.withFormatting(level, Formatting.GRAY)));
        }

        if (!tooltip.isEmpty() && stack.hasEnchantments()) {
            tooltip.add(Text.empty());
        }

        return tooltip;
    }

    static int getModifierLevel(ItemStack stack, SoulAlloyModifier modifier) {
        return stack.getSubNbt(MODIFIERS_KEY) != null && stack.getSubNbt(MODIFIERS_KEY).contains(modifier.name()) ? stack.getSubNbt(MODIFIERS_KEY).getInt(modifier.name()) : 0;
    }

    static Map<SoulAlloyModifier, Integer> getModifiers(ItemStack stack) {
        if (stack.getNbt() == null) return Map.of();

        var modifierMap = new HashMap<SoulAlloyModifier, Integer>();
        var modifierTag = stack.getOrCreateSubNbt(MODIFIERS_KEY);
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
