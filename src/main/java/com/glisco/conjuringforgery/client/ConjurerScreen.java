package com.glisco.conjuringforgery.client;

import com.glisco.conjuringforgery.ConjurerContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ConjurerScreen extends ContainerScreen<ConjurerContainer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("conjuring", "textures/gui/conjurer.png");

    public ConjurerScreen(ConjurerContainer handler, PlayerInventory inventory, ITextComponent title) {
        super(handler, inventory, title);
        this.ySize = 192;
        this.playerInventoryTitleY = 10000;
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
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        minecraft.getTextureManager().bindTexture(TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.blit(matrixStack, x, y, 0, 0, xSize, ySize);
    }


}
