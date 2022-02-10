package com.glisco.conjuring.client.ui;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.util.SoulfireForgeScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SoulfireForgeScreen extends HandledScreen<ScreenHandler> {

    private static final Identifier TEXTURE = Conjuring.id("textures/gui/soulfire_forge.png");

    public SoulfireForgeScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = getRootX();
        int y = getRootY();
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);

        int progress = ((SoulfireForgeScreenHandler) this.handler).getProgress();

        drawTexture(matrices, x + 90, y + 57 - progress, 176, 32 - progress, 32, 32);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        titleY = 5;
    }

    public int getRootX() {
        return (width - backgroundWidth) / 2;
    }

    public int getRootY() {
        return (height - backgroundHeight) / 2;
    }
}
