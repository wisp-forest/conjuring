package com.glisco.conjuringforgery.compat.jei;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.blocks.soul_weaver.SoulWeaverRecipe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SoulWeavingRecipeCategory implements IRecipeCategory<SoulWeaverRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(ConjuringForgery.MODID, "soul_weaving");

    private final IDrawable background;
    private final IDrawable icon;

    private final IDrawable plus;
    private final IDrawable slot;

    public SoulWeavingRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(new ItemStack(ConjuringForgery.SOuL_WEAVER_ITEM.get()));

        ResourceLocation location = new ResourceLocation(ConjuringForgery.MODID, "textures/gui/soul_weaver.png");
        this.background = guiHelper.createDrawable(location, 0, -5, 154, 110);

        this.plus = guiHelper.createDrawable(location, 161, 0, 10, 10);
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends SoulWeaverRecipe> getRecipeClass() {
        return SoulWeaverRecipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("conjuring.gui.soul_weaver");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(SoulWeaverRecipe soulWeaverRecipe, IIngredients iIngredients) {
        List<List<ItemStack>> inputs = soulWeaverRecipe.getInputs().stream().map((i) -> {
            List<ItemStack> entries = new ArrayList();
            ItemStack[] var2 = i.getMatchingStacks();

            Collections.addAll(entries, var2);

            return entries;
        }).collect(Collectors.toList());

        iIngredients.setInputLists(VanillaTypes.ITEM, inputs);
        iIngredients.setOutput(VanillaTypes.ITEM, soulWeaverRecipe.getRecipeOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, SoulWeaverRecipe soulWeaverRecipe, IIngredients iIngredients) {
        final IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        itemStacks.init(0, true, 15, 80);
        itemStacks.set(0, new ItemStack(ConjuringForgery.CONJURATION_ESSENCE.get()));

        itemStacks.init(1, false, 0, 5);
        itemStacks.set(1, iIngredients.getInputs(VanillaTypes.ITEM).get(1));

        itemStacks.init(2, false, 0, 46);
        itemStacks.set(2, iIngredients.getInputs(VanillaTypes.ITEM).get(2));

        itemStacks.init(3, false, background.getWidth() - 18, 5);
        itemStacks.set(3, iIngredients.getInputs(VanillaTypes.ITEM).get(3));

        itemStacks.init(4, false, background.getWidth() - 18, 46);
        itemStacks.set(4, iIngredients.getInputs(VanillaTypes.ITEM).get(4));

        itemStacks.init(5, false, 68, 26);
        itemStacks.set(5, iIngredients.getInputs(VanillaTypes.ITEM).get(0));

        itemStacks.init(6, false, 68, 81);
        itemStacks.set(6, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public void draw(SoulWeaverRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();

        slot.draw(matrixStack, 0, 5);
        slot.draw(matrixStack, background.getWidth() - 18, 5);

        slot.draw(matrixStack, 0, 46);
        slot.draw(matrixStack, background.getWidth() - 18, 46);

        slot.draw(matrixStack, 68, 26);

        plus.draw(matrixStack, 1, 84);
        slot.draw(matrixStack, 15, 80);

        RenderSystem.disableBlend();
        RenderSystem.disableAlphaTest();
    }
}
