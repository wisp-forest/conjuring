package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.blocks.ConjuringBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Label;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SoulfireForgeCategory implements DisplayCategory<SoulfireForgeDisplay> {

    private static final TranslatableText NAME = new TranslatableText("conjuring.gui.soulfire_forge");

    @Override
    public CategoryIdentifier<? extends SoulfireForgeDisplay> getCategoryIdentifier() {
        return ConjuringCommonPlugin.SOULFIRE_FORGE;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ConjuringBlocks.SOULFIRE_FORGE);
    }

    @Override
    public Text getTitle() {
        return NAME;
    }

    @Override
    @NotNull
    public List<Widget> setupDisplay(SoulfireForgeDisplay recipeDisplay, Rectangle bounds) {
        Point origin = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);

        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        List<EntryIngredient> inputs = recipeDisplay.getInputEntries();
        List<Slot> slots = new ArrayList<>();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                slots.add(Widgets.createSlot(new Point(origin.x + 1 + x * 18, origin.y + 1 + y * 18)).markInput());
            }
        }

        for (int i = 0; i < inputs.size(); i++) {
            if (!inputs.get(i).isEmpty()) {
                slots.get(i).entries(inputs.get(i));
            }
        }

        widgets.addAll(slots);
        widgets.add(Widgets.createResultSlotBackground(new Point(origin.x + 95, origin.y + 19)));
        widgets.add(Widgets.createSlot(new Point(origin.x + 95, origin.y + 19)).entries(recipeDisplay.getOutputEntries().get(0)).disableBackground().markOutput());

        widgets.add(Widgets.createTexturedWidget(Conjuring.id("textures/gui/soulfire_forge.png"), origin.x + 57, origin.y + 11, 176, 0, 32, 32));

        Label timeWidget = Widgets.createLabel(new Point(origin.x + 74, origin.y + 43), new LiteralText(recipeDisplay.getSmeltTime() / 20 + "s")).color(0x3F3F3F).shadow(false);
        if (recipeDisplay.getSmeltTime() == 1380) {
            timeWidget = timeWidget.tooltipLine("haha funny number");
        }
        widgets.add(timeWidget);

        return widgets;
    }
}
