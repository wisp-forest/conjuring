package com.glisco.conjuringforgery.compat.jei;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeRecipe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SoulfireForgeRecipeCategory implements IRecipeCategory<SoulfireForgeRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(ConjuringForgery.MODID, "soulfire_forge");

    private final IDrawable background;
    private final IDrawable icon;

    public SoulfireForgeRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(new ItemStack(ConjuringForgery.SOULFIRE_FORGE_ITEM.get()));

        ResourceLocation location = new ResourceLocation(ConjuringForgery.MODID, "textures/gui/soulfire_forge_jei.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 116, 54);
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends SoulfireForgeRecipe> getRecipeClass() {
        return SoulfireForgeRecipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("conjuring.gui.soulfire_forge");
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
    public void setIngredients(SoulfireForgeRecipe soulfireForgeRecipe, IIngredients iIngredients) {
        List<List<ItemStack>> inputs = soulfireForgeRecipe.getInputs().stream().map((i) -> {
            List<ItemStack> entries = new ArrayList();
            ItemStack[] var2 = i.getMatchingStacks();

            Collections.addAll(entries, var2);

            return entries;
        }).collect(Collectors.toList());

        iIngredients.setInputLists(VanillaTypes.ITEM, inputs);
        iIngredients.setOutput(VanillaTypes.ITEM, soulfireForgeRecipe.getRecipeOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, SoulfireForgeRecipe soulfireForgeRecipe, IIngredients iIngredients) {
        int index = 0;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                iRecipeLayout.getItemStacks().init(index, true, x * 18, y * 18);
                iRecipeLayout.getItemStacks().set(index, iIngredients.getInputs(VanillaTypes.ITEM).get(index));
                index++;
            }
        }

        iRecipeLayout.getItemStacks().init(index, false, 94, 18);
        iRecipeLayout.getItemStacks().set(index, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public void draw(SoulfireForgeRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();

        String text = recipe.getSmeltTime() / 20 + "s";
        FontRenderer fontRenderer = Minecraft.getInstance().getRenderManager().getFontRenderer();

        int fontWidth = fontRenderer.getStringWidth(text);
        fontRenderer.drawString(matrixStack, text, 73 - fontWidth / 2.0f, 43, 0x3F3F3F);

        RenderSystem.disableBlend();
        RenderSystem.disableAlphaTest();
    }
}
