package com.glisco.conjuringforgery.mixin;

import com.glisco.conjuringforgery.items.soul_alloy_tools.SoulAlloyTool;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    protected abstract void draw(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    @Inject(method = "renderItemOverlayIntoGUI", at = @At("TAIL"))
    private void renderCustomDurabilityBar(FontRenderer textRenderer, ItemStack stack, int x, int y, String amountText, CallbackInfo ci) {
        if (!(stack.getItem() instanceof SoulAlloyTool)) return;
        if (!SoulAlloyTool.isSecondaryEnabled(stack)) return;

        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        int width = Math.round(13.0F - stack.getDamage() * 13.0F / stack.getMaxDamage());

        this.draw(bufferBuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
        this.draw(bufferBuilder, x + 2, y + 13, width, 1, 0, 255, 255, 255);

        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();

    }
}
