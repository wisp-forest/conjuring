package com.glisco.conjuringforgery.compat.jei;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.blocks.gem_tinkerer.GemTinkererBlockEntity;
import com.glisco.conjuringforgery.blocks.gem_tinkerer.GemTinkererRecipe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GemTinkeringCategory implements IRecipeCategory<GemTinkererRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(ConjuringForgery.MODID, "gem_tinkering");

    private GemTinkererDrawable tinkererDrawable;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;

    private final IDrawable leftArrow;
    private final IDrawable rightArrow;

    public GemTinkeringCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(new ItemStack(ConjuringForgery.GEM_TINKERER_ITEM.get()));

        this.background = guiHelper.createBlankDrawable(160, 230);
        this.slot = guiHelper.getSlotDrawable();

        this.leftArrow = guiHelper.createDrawable(new ResourceLocation("conjuring", "textures/gui/gem_tinkerer.png"), 0, 0, 25, 25);
        this.rightArrow = guiHelper.createDrawable(new ResourceLocation("conjuring", "textures/gui/gem_tinkerer.png"), 25, 0, 25, 25);
    }


    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends GemTinkererRecipe> getRecipeClass() {
        return GemTinkererRecipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("conjuring.gui.gem_tinkerer");
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
    public void setIngredients(GemTinkererRecipe gemTinkererRecipe, IIngredients iIngredients) {
        List<List<ItemStack>> inputs = gemTinkererRecipe.getInputs().stream().map((i) -> {
            List<ItemStack> entries = new ArrayList();
            ItemStack[] var2 = i.getMatchingStacks();

            Collections.addAll(entries, var2);

            return entries;
        }).collect(Collectors.toList());

        iIngredients.setInputLists(VanillaTypes.ITEM, inputs);
        iIngredients.setOutput(VanillaTypes.ITEM, gemTinkererRecipe.getRecipeOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, GemTinkererRecipe gemTinkererRecipe, IIngredients iIngredients) {
        tinkererDrawable = new GemTinkererDrawable(iIngredients.getInputs(VanillaTypes.ITEM).stream().map(stacks -> stacks.get(0)).collect(Collectors.toList()));

        for (int i = 0; i < 5; i++) {

            int j = i;
            if (i == 0) j = 2;
            if (i == 2) j = 0;

            iRecipeLayout.getItemStacks().init(i, false, 15 + i * 27, 160);
            iRecipeLayout.getItemStacks().set(i, iIngredients.getInputs(VanillaTypes.ITEM).get(j));
        }

        iRecipeLayout.getItemStacks().init(9, false, 69, 198);
        iRecipeLayout.getItemStacks().set(9, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public void draw(GemTinkererRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();

        tinkererDrawable.draw(matrixStack, 0, 0);

        for (int i = 0; i < 5; i++) {
            slot.draw(matrixStack, 15 + i * 27, 160);
        }

        slot.draw(matrixStack, 69, 198);

        leftArrow.draw(matrixStack, 23, 192);
        rightArrow.draw(matrixStack, getBackground().getWidth() - 52, 192);

        RenderSystem.disableBlend();
        RenderSystem.disableAlphaTest();
    }

    private static class GemTinkererDrawable implements IDrawable {

        private final GemTinkererBlockEntity tinkerer;

        public GemTinkererDrawable(List<ItemStack> stacks) {
            tinkerer = new GemTinkererBlockEntity();
            tinkerer.getInventory().set(0, stacks.get(0));
            tinkerer.getInventory().set(1, stacks.get(1));
            tinkerer.getInventory().set(2, stacks.get(2));
            tinkerer.getInventory().set(3, stacks.get(3));
            tinkerer.getInventory().set(4, stacks.get(4));
            tinkerer.setWorldAndPos(Minecraft.getInstance().world, BlockPos.ZERO);
        }

        @Override
        public int getWidth() {
            return 160;
        }

        @Override
        public int getHeight() {
            return 150;
        }

        @Override
        public void draw(MatrixStack matrixStack, int x, int y) {
            IRenderTypeBuffer.Impl impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

            matrixStack.push();
            matrixStack.translate((x + getWidth()) / 3f - 89, y + getHeight() + 20, 0);
            matrixStack.scale(getWidth() * 0.5f, getHeight() / -1.9f, 1);
            matrixStack.scale(2, 2, 1);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(30));
            matrixStack.rotate(Vector3f.YP.rotationDegrees(45));

            RenderSystem.setupGuiFlatDiffuseLighting(Util.make(new Vector3f(0.2F, 1.0F, -0.7F), Vector3f::normalize), Util.make(new Vector3f(-0.2F, 1.0F, 0.7F), Vector3f::normalize));

            TileEntityRendererDispatcher.instance.getRenderer(tinkerer).render(tinkerer, 0, matrixStack, impl, 15728880, OverlayTexture.NO_OVERLAY);

            matrixStack.pop();
            impl.finish();
        }
    }
}
