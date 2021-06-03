package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.ConjuringCommon;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class SoulWeavingCategory implements DisplayCategory<SoulWeavingDisplay> {

    private static final TranslatableText NAME = new TranslatableText("conjuring.gui.soul_weaver");

    @Override
    public CategoryIdentifier<? extends SoulWeavingDisplay> getCategoryIdentifier() {
        return ConjuringPlugin.SOUL_WEAVING;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ConjuringCommon.SOUL_WEAVER_BLOCK);
    }

    @Override
    public Text getTitle() {
        return NAME;
    }

    @Override
    public int getDisplayHeight() {
        return 120;
    }

    @Override
    public int getDisplayWidth(SoulWeavingDisplay display) {
        return 170;
    }

    @Override
    public @NotNull List<Widget> setupDisplay(SoulWeavingDisplay recipeDisplay, Rectangle bounds) {

        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        widgets.add(Widgets.createSlot(new Point(bounds.getX() + 77, bounds.getY() + 31)).entries(recipeDisplay.getInputEntries().get(0)));

        widgets.add(Widgets.createSlot(new Point(bounds.getX() + 8, bounds.getY() + 10)).entries(recipeDisplay.getInputEntries().get(1)));
        widgets.add(Widgets.createSlot(new Point(bounds.getX() + 8, bounds.getY() + 52)).entries(recipeDisplay.getInputEntries().get(2)));

        widgets.add(Widgets.createSlot(new Point(bounds.getX() + 146, bounds.getY() + 10)).entries(recipeDisplay.getInputEntries().get(3)));
        widgets.add(Widgets.createSlot(new Point(bounds.getX() + 146, bounds.getY() + 52)).entries(recipeDisplay.getInputEntries().get(4)));

        widgets.add(Widgets.createResultSlotBackground(new Point(bounds.getX() + 77, bounds.getY() + 88)));
        widgets.add(Widgets.createSlot(new Point(bounds.getX() + 77, bounds.getY() + 88)).entries(recipeDisplay.getOutputEntries().get(0)).disableBackground());

        widgets.add(Widgets.createTexturedWidget(new Identifier("conjuring", "textures/gui/soul_weaver.png"), bounds.getX() + 8, bounds.getY() + 10, 0, 0, 154, 96));
        widgets.add(Widgets.createSlot(new Point(bounds.getX() + 24, bounds.getY() + 88)).entry(EntryStacks.of(ConjuringCommon.CONJURATION_ESSENCE)));

        return widgets;
    }
}
