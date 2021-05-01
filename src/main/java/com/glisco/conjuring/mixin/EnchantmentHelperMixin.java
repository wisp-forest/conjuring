package com.glisco.conjuring.mixin;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyTool;
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

    @Inject(method = "getEfficiency", at = @At("RETURN"), cancellable = true)
    private static void applyHaste(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (!(entity.getMainHandStack().getItem() instanceof SoulAlloyTool)) return;
        if (entity.getMainHandStack().getItem() == ConjuringCommon.SOUL_ALLOY_SWORD) return;

        cir.setReturnValue(cir.getReturnValue() + SoulAlloyTool.getModifierLevel(entity.getMainHandStack(), SoulAlloyTool.SoulAlloyModifier.HASTE));
    }

    @Inject(method = "getLooting", at = @At("RETURN"), cancellable = true)
    private static void applyAbundanceLooting(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (entity.getMainHandStack().getItem() != ConjuringCommon.SOUL_ALLOY_SWORD) return;
        cir.setReturnValue(cir.getReturnValue() + SoulAlloyTool.getModifierLevel(entity.getMainHandStack(), SoulAlloyTool.SoulAlloyModifier.ABUNDANCE));
    }

    @Inject(method = "getLevel", at = @At(value = "RETURN"), cancellable = true)
    private static void applyAbundanceFortune(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (enchantment != Enchantments.FORTUNE) return;
        if (stack.getItem() != ConjuringCommon.SOUL_ALLOY_PICKAXE) return;

        cir.setReturnValue(cir.getReturnValue() + SoulAlloyTool.getModifierLevel(stack, SoulAlloyTool.SoulAlloyModifier.ABUNDANCE));
    }

}
