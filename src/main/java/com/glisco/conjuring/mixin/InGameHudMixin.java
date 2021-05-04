package com.glisco.conjuring.mixin;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyVariable(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;scaledHeight:I", opcode = Opcodes.GETFIELD), ordinal = 0)
    public boolean showAttackIndicator(boolean showIndicator) {
        if (this.client.player.getMainHandStack().getItem() != ConjuringCommon.SOUL_ALLOY_SWORD) return showIndicator;
        return this.client.player.getAttackCooldownProgress(0) == 1 && this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity;
    }

}
