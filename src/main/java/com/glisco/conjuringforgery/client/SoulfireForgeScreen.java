package com.glisco.conjuringforgery.client;

import com.glisco.conjuringforgery.SoulfireForgeContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SoulfireForgeScreen extends ContainerScreen<SoulfireForgeContainer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("conjuring", "textures/gui/soulfire_forge.png");

    public SoulfireForgeScreen(SoulfireForgeContainer handler, PlayerInventory inventory, ITextComponent title) {
        super(handler, inventory, title);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        minecraft.getTextureManager().bindTexture(TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        blit(matrices, x, y, 0, 0, xSize, ySize);

        int progress = container.getProgress();

        blit(matrices, x + 90, y + 57 - progress, 176, 32 - progress, 32, 32);

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        renderHoveredTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (xSize - font.getStringPropertyWidth(title)) / 2;
        titleY--;
    }
}
