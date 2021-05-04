package com.glisco.conjuring.mixin;

import com.glisco.conjuring.compat.rei.GemTinkeringCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {"me.shedaniel.rei.impl.OverlaySearchFieldSyntaxHighlighter"})
public class OverlaySearchFieldSyntaxHighlighterMixin {

    @Inject(method = "accept", at = @At("HEAD"), remap = false)
    public void froge(String text, CallbackInfo ci) {
        GemTinkeringCategory.FROGE_MODE = text.contains("froge");
    }

}
