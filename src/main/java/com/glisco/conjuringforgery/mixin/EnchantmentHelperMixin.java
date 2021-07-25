package com.glisco.conjuringforgery.mixin;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.items.soul_alloy_tools.SoulAlloyTool;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getEfficiencyModifier", at = @At("RETURN"), cancellable = true)
    private static void applyHaste(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (!(entity.getHeldItemMainhand().getItem() instanceof SoulAlloyTool)) return;
        if (entity.getHeldItemMainhand().getItem() == ConjuringForgery.getValue(ConjuringForgery.SOUL_ALLOY_SWORD)) return;

        cir.setReturnValue(cir.getReturnValue() + SoulAlloyTool.getModifierLevel(entity.getHeldItemMainhand(), SoulAlloyTool.SoulAlloyModifier.HASTE));
    }

    @Inject(method = "getLootingModifier", at = @At("RETURN"), cancellable = true)
    private static void applyAbundanceLooting(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (!(entity.getHeldItemMainhand().getItem() == ConjuringForgery.getValue(ConjuringForgery.SOUL_ALLOY_SWORD) || entity.getHeldItemMainhand().getItem() == ConjuringForgery.getValue(ConjuringForgery.SOUL_ALLOY_HATCHET)))
            return;
        cir.setReturnValue(cir.getReturnValue() + SoulAlloyTool.getModifierLevel(entity.getHeldItemMainhand(), SoulAlloyTool.SoulAlloyModifier.ABUNDANCE));
    }

    @Inject(method = "getEnchantmentLevel", at = @At(value = "RETURN"), cancellable = true)
    private static void applyAbundanceFortune(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (enchantment != Enchantments.FORTUNE) return;
        if (!(stack.getItem() == ConjuringForgery.getValue(ConjuringForgery.SOUL_ALLOY_PICKAXE) || stack.getItem() == ConjuringForgery.getValue(ConjuringForgery.SOUL_ALLOY_SHOVEL))) return;

        cir.setReturnValue(cir.getReturnValue() + SoulAlloyTool.getModifierLevel(stack, SoulAlloyTool.SoulAlloyModifier.ABUNDANCE));
    }

}
