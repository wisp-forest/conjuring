package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.blocks.gem_tinkerer.GemTinkererBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.TextField;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GemTinkeringCategory implements DisplayCategory<GemTinkeringDisplay> {
    private static final TranslatableText NAME = new TranslatableText("conjuring.gui.gem_tinkerer");

    @Override
    public CategoryIdentifier<? extends GemTinkeringDisplay> getCategoryIdentifier() {
        return ConjuringPlugin.GEM_TINKERING;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ConjuringCommon.GEM_TINKERER_BLOCK);
    }

    @Override
    public Text getTitle() {
        return NAME;
    }

    @Override
    public int getDisplayHeight() {
        return 180;
    }

    @Override
    public @NotNull List<Widget> setupDisplay(GemTinkeringDisplay recipeDisplay, Rectangle bounds) {
        Point origin = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);

        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        GemTinkererBlockEntity tinkerer = new GemTinkererBlockEntity(BlockPos.ORIGIN, ConjuringCommon.GEM_TINKERER_BLOCK.getDefaultState());
        tinkerer.getInventory().set(0, recipeDisplay.getInputEntries().get(0).get(0).castValue());
        tinkerer.getInventory().set(1, recipeDisplay.getInputEntries().get(1).get(0).castValue());
        tinkerer.getInventory().set(2, recipeDisplay.getInputEntries().get(2).get(0).castValue());
        tinkerer.getInventory().set(3, recipeDisplay.getInputEntries().get(3).get(0).castValue());
        tinkerer.getInventory().set(4, recipeDisplay.getInputEntries().get(4).get(0).castValue());
        tinkerer.setWorld(MinecraftClient.getInstance().world);

        widgets.add(Widgets.createDrawableWidget((drawableHelper, matrixStack, i, i1, v) -> {
            boolean FROGE_MODE = isFroge();

            if (FROGE_MODE) {
                matrixStack.push();
                RenderSystem.setShaderTexture(0, new Identifier("conjuring", "textures/gui/froge.png"));
                DrawableHelper.drawTexture(matrixStack, origin.x, origin.y, 0, 0, 0, 128, 128, 128, 128);

                matrixStack.method_34425(new Matrix4f(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) (System.currentTimeMillis() / 30d % 360))));
                matrixStack.translate(15 * (float) Math.sin(System.currentTimeMillis() / 60d % (2 * Math.PI)), 35 * (float) Math.cos(System.currentTimeMillis() / 60d % (2 * Math.PI)), 0);
            }

            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

            matrixStack.push();
            matrixStack.translate(bounds.getCenterX() - 83, bounds.getCenterY() + 28, 0);
            matrixStack.scale((float) bounds.getWidth() * 0.75f, (float) (bounds.getWidth() + bounds.getHeight()) / -3.15f, (float) bounds.getHeight() * 0.75f);
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(30));
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(45));

            RenderSystem.setupGuiFlatDiffuseLighting(Util.make(new Vec3f(0.2F, 1.0F, -0.7F), Vec3f::normalize), Util.make(new Vec3f(-0.2F, 1.0F, 0.7F), Vec3f::normalize));

            MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(tinkerer).render(tinkerer, 0, matrixStack, immediate, 15728880, OverlayTexture.DEFAULT_UV);

            matrixStack.pop();
            immediate.draw();

            if (FROGE_MODE) {
                matrixStack.pop();
            }

        }));


        for (int i = 0; i < 5; i++) {

            int j = i;
            if (i == 0) j = 2;
            if (i == 2) j = 0;

            widgets.add(Widgets.createSlot(new Point(origin.getX() + 1 + i * 23, origin.getY() + 53)).entries(recipeDisplay.getInputEntries().get(j)));
        }

        widgets.add(Widgets.createResultSlotBackground(new Point(origin.getX() + 47, origin.getY() + 85)));
        widgets.add(Widgets.createSlot(new Point(origin.getX() + 47, origin.getY() + 85)).entries(recipeDisplay.getOutputEntries().get(0)).disableBackground());

        widgets.add(Widgets.createTexturedWidget(new Identifier("conjuring", "textures/gui/gem_tinkerer.png"), origin.getX() + 9, origin.getY() + 75, 0, 0, 25, 25));
        widgets.add(Widgets.createTexturedWidget(new Identifier("conjuring", "textures/gui/gem_tinkerer.png"), origin.getX() + 75, origin.getY() + 75, 25, 0, 25, 25));

        return widgets;
    }

    private boolean isFroge() {
        TextField searchTextField = REIRuntime.getInstance().getSearchTextField();
        if (searchTextField != null) {
            return searchTextField.getText().contains("froge");
        }
        return false;
    }
}
