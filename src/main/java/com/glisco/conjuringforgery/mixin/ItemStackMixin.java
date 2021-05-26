package com.glisco.conjuringforgery.mixin;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.items.soul_alloy_tools.SoulAlloyTool;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Shadow
    @Final
    @Deprecated
    private Item item;

    @ModifyVariable(method = "attemptDamageItem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I", shift = At.Shift.AFTER), ordinal = 1)
    public int applyIgnorance(int i) {
        if (!(item instanceof SoulAlloyTool)) return i;
        if (item == ConjuringForgery.SOUL_ALLOY_SWORD.get()) return i;

        return i + SoulAlloyTool.getModifierLevel((ItemStack) (Object) this, SoulAlloyTool.SoulAlloyModifier.IGNORANCE);
    }

    @Inject(method = "getAttributeModifiers", at = @At("TAIL"), cancellable = true)
    public void applyHasteSword(EquipmentSlotType equipmentSlot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir) {
        if (equipmentSlot != EquipmentSlotType.MAINHAND) return;
        if (item != ConjuringForgery.SOUL_ALLOY_SWORD.get()) return;

        final ItemStack thisStack = (ItemStack) (Object) this;

        if (SoulAlloyTool.getModifierLevel(thisStack, SoulAlloyTool.SoulAlloyModifier.HASTE) < 1) return;

        Multimap<Attribute, AttributeModifier> modifierMap = cir.getReturnValue();

        modifierMap = HashMultimap.create(modifierMap);
        modifierMap.removeAll(Attributes.ATTACK_SPEED);

        modifierMap.put(Attributes.ATTACK_SPEED, new AttributeModifier(ItemAccessor.getAttackSpeedModifierID(), "Weapon modifier", -2.4f + Math.pow(SoulAlloyTool.getModifierLevel(thisStack, SoulAlloyTool.SoulAlloyModifier.HASTE), ConjuringForgery.CONFIG.tools_config.sword_haste_exponent), AttributeModifier.Operation.ADDITION));

        cir.setReturnValue(modifierMap);
    }

}
