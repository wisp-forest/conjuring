package com.glisco.conjuring.mixin;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.items.ConjuringItems;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyTool;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
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
    private Item item;

    @ModifyVariable(method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I", shift = At.Shift.AFTER), ordinal = 1)
    public int applyIgnorance(int i) {
        if (!(item instanceof SoulAlloyTool)) return i;
        if (item == ConjuringItems.SOUL_ALLOY_SWORD) return i;

        return i + SoulAlloyTool.getModifierLevel((ItemStack) (Object) this, SoulAlloyTool.SoulAlloyModifier.IGNORANCE);
    }

    @Inject(method = "getAttributeModifiers", at = @At("TAIL"), cancellable = true)
    public void applyHasteSword(EquipmentSlot equipmentSlot, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        if (equipmentSlot != EquipmentSlot.MAINHAND) return;
        if (item != ConjuringItems.SOUL_ALLOY_SWORD) return;

        final ItemStack thisStack = (ItemStack) (Object) this;

        if (SoulAlloyTool.getModifierLevel(thisStack, SoulAlloyTool.SoulAlloyModifier.HASTE) < 1) return;

        Multimap<EntityAttribute, EntityAttributeModifier> modifierMap = cir.getReturnValue();

        modifierMap = HashMultimap.create(modifierMap);
        modifierMap.removeAll(EntityAttributes.GENERIC_ATTACK_SPEED);

        modifierMap.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ItemAccessor.getAttackSpeedModifierID(), "Weapon modifier", -2.4f + Math.pow(SoulAlloyTool.getModifierLevel(thisStack, SoulAlloyTool.SoulAlloyModifier.HASTE), Conjuring.CONFIG.tools_config.sword_haste_exponent), EntityAttributeModifier.Operation.ADDITION));

        cir.setReturnValue(modifierMap);
    }

}
