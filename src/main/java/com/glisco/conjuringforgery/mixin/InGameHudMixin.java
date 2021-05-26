package com.glisco.conjuringforgery.mixin;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(IngameGui.class)
public class InGameHudMixin {

    @Shadow
    @Final
    protected Minecraft mc;

    @ModifyVariable(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/IngameGui;scaledHeight:I", opcode = Opcodes.GETFIELD), ordinal = 0)
    public boolean showAttackIndicator(boolean showIndicator) {
        if (this.mc.player.getHeldItemMainhand().getItem() != ConjuringForgery.SOUL_ALLOY_SWORD.get()) return showIndicator;
        return this.mc.player.getCooledAttackStrength(0) == 1 && this.mc.pointedEntity != null && this.mc.pointedEntity instanceof LivingEntity;
    }

}
