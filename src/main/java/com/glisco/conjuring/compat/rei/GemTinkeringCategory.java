package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.blocks.gem_tinkerer.GemTinkererBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GemTinkeringCategory implements RecipeCategory<GemTinkeringDisplay> {

    public static boolean FROGE_MODE = false;

    private static final TranslatableText NAME = new TranslatableText("conjuring.gui.gem_tinkerer");

    @Override
    public @NotNull Identifier getIdentifier() {
        return ConjuringPlugin.GEM_TINKERING;
    }

    @Override
    public @NotNull EntryStack getLogo() {
        return EntryStack.create(ConjuringCommon.GEM_TINKERER_BLOCK);
    }

    @Override
    public @NotNull String getCategoryName() {
        return NAME.getString();
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

        GemTinkererBlockEntity tinkerer = new GemTinkererBlockEntity();
        tinkerer.getInventory().set(0, recipeDisplay.getInputEntries().get(0).get(0).getItemStack());
        tinkerer.getInventory().set(1, recipeDisplay.getInputEntries().get(1).get(0).getItemStack());
        tinkerer.getInventory().set(2, recipeDisplay.getInputEntries().get(2).get(0).getItemStack());
        tinkerer.getInventory().set(3, recipeDisplay.getInputEntries().get(3).get(0).getItemStack());
        tinkerer.getInventory().set(4, recipeDisplay.getInputEntries().get(4).get(0).getItemStack());
        tinkerer.setLocation(MinecraftClient.getInstance().world, BlockPos.ORIGIN);

        widgets.add(Widgets.createDrawableWidget((drawableHelper, matrixStack, i, i1, v) -> {

            if (FROGE_MODE) {
                MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("conjuring", "textures/gui/froge.png"));
                DrawableHelper.drawTexture(matrixStack, origin.x, origin.y, 0, 0, 0, 128, 128, 128, 128);

                RenderSystem.multMatrix(new Matrix4f(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) (System.currentTimeMillis() / 30d % 360))));
                RenderSystem.translatef(15 * (float) Math.sin(System.currentTimeMillis() / 60d % (2 * Math.PI)), 35 * (float) Math.cos(System.currentTimeMillis() / 60d % (2 * Math.PI)), 0);
            }

            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

            matrixStack.push();
            matrixStack.translate(bounds.getCenterX() - 83, bounds.getCenterY() + 28, 0);
            matrixStack.scale((float) bounds.getWidth() * 0.75f, (float) (bounds.getWidth() + bounds.getHeight()) / -3.15f, (float) bounds.getHeight() * 0.75f);
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(30));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(45));

            RenderSystem.setupGuiFlatDiffuseLighting(Util.make(new Vector3f(0.2F, 1.0F, -0.7F), Vector3f::normalize), Util.make(new Vector3f(-0.2F, 1.0F, 0.7F), Vector3f::normalize));

            BlockEntityRenderDispatcher.INSTANCE.get(tinkerer).render(tinkerer, 0, matrixStack, immediate, 15728880, OverlayTexture.DEFAULT_UV);

            matrixStack.pop();
            immediate.draw();

        }));


        for (int i = 0; i < 5; i++) {

            int j = i;
            if (i == 0) j = 2;
            if (i == 2) j = 0;

            widgets.add(Widgets.createSlot(new Point(origin.getX() + 1 + i * 23, origin.getY() + 53)).entries(recipeDisplay.getInputEntries().get(j)));
        }

        widgets.add(Widgets.createResultSlotBackground(new Point(origin.getX() + 47, origin.getY() + 85)));
        widgets.add(Widgets.createSlot(new Point(origin.getX() + 47, origin.getY() + 85)).entries(recipeDisplay.getResultingEntries().get(0)).disableBackground());

        widgets.add(Widgets.createTexturedWidget(new Identifier("conjuring", "textures/gui/gem_tinkerer.png"), origin.getX() + 9, origin.getY() + 75, 0, 0, 25, 25));
        widgets.add(Widgets.createTexturedWidget(new Identifier("conjuring", "textures/gui/gem_tinkerer.png"), origin.getX() + 75, origin.getY() + 75, 25, 0, 25, 25));

        return widgets;
    }
}
