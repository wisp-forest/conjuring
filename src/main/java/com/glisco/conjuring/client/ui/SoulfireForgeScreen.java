package com.glisco.conjuring.client.ui;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.util.SoulfireForgeScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SoulfireForgeScreen extends HandledScreen<SoulfireForgeScreenHandler> {

    private static final Identifier TEXTURE = Conjuring.id("textures/gui/soulfire_forge.png");

    public SoulfireForgeScreen(SoulfireForgeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        this.renderBackground(context);

        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, backgroundWidth, backgroundHeight);

        int progress = this.handler.getProgress();
        context.drawTexture(TEXTURE, this.x + 90, this.y + 57 - progress, 176, 32 - progress, 32, 32);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        titleY = 5;
    }

    public int getRootX() {
        return this.x;
    }

    public int getRootY() {
        return this.y;
    }
}
