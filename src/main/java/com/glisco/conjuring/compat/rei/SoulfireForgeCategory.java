package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.ConjuringCommon;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Label;
import me.shedaniel.rei.api.widgets.Slot;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SoulfireForgeCategory implements RecipeCategory<SoulfireForgeDisplay> {

    private static final TranslatableText NAME = new TranslatableText("conjuring.gui.soulfire_forge");

    @Override
    public @NotNull Identifier getIdentifier() {
        return ConjuringPlugin.SOULFIRE_FORGE;
    }

    @Override
    public @NotNull EntryStack getLogo() {
        return EntryStack.create(ConjuringCommon.SOULFIRE_FORGE_BLOCK);
    }

    @Override
    public @NotNull String getCategoryName() {
        return NAME.getString();
    }

    @Override
    public @NotNull List<Widget> setupDisplay(SoulfireForgeDisplay recipeDisplay, Rectangle bounds) {
        Point origin = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);

        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        List<List<EntryStack>> inputs = recipeDisplay.getInputEntries();
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
        widgets.add(Widgets.createSlot(new Point(origin.x + 95, origin.y + 19)).entries(recipeDisplay.getOutputEntries()).disableBackground().markOutput());

        widgets.add(Widgets.createTexturedWidget(new Identifier("conjuring", "textures/gui/soulfire_forge.png"), origin.x + 57, origin.y + 11, 176, 0, 32, 32));

        Label timeWidget = Widgets.createLabel(new Point(origin.x + 74, origin.y + 43), new LiteralText(recipeDisplay.getSmeltTime() / 20 + "s")).color(0x3F3F3F).shadow(false);
        if (recipeDisplay.getSmeltTime() == 1380) {
            timeWidget = timeWidget.tooltipLine("haha funny number");
        }
        widgets.add(timeWidget);

        return widgets;
    }
}
