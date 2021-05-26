package com.glisco.conjuringforgery.mixin;

import com.glisco.conjuringforgery.items.soul_alloy_tools.SoulAlloyToolAbilities;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @ModifyVariable(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getFireAspectModifier(Lnet/minecraft/entity/LivingEntity;)I"), ordinal = 3)
    public boolean disableSweepForScope(boolean sweep) {
        if (!SoulAlloyToolAbilities.canAoeHit((PlayerEntity) (Object) this)) return sweep;
        return false;
    }

}
