package com.glisco.conjuring_testmod;

import com.glisco.conjuring.blocks.BlackstonePedestalBlockEntity;
import com.glisco.conjuring.blocks.RitualCore;
import com.glisco.conjuring.blocks.SoulFunnelBlock;
import com.glisco.conjuring.blocks.SoulFunnelBlockEntity;
import com.glisco.conjuring.blocks.gem_tinkerer.GemTinkererBlockEntity;
import com.glisco.conjuring.blocks.soul_weaver.SoulWeaverBlockEntity;
import com.glisco.conjuring.blocks.soulfire_forge.SoulfireForgeBlockEntity;
import com.glisco.conjuring.items.ConjuringItems;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyTool;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ConjuringTestSuite {

    private static final BlockPos TEST_ORIGIN = new BlockPos(0, 1, 0);
    private static final List<BlockPos> PEDESTAL_OFFSETS;

    @GameTest(templateName = "conjuring_testmod:linking_funnel", tickLimit = 20)
    public void linkingFunnel(TestContext context) {
        var helper = new TestInteractionHelper(context);

        helper.setHandItem(new ItemStack(ConjuringItems.CONJURING_SCEPTER));

        helper.interactItemAtBlock(TEST_ORIGIN.add(0, 0, 3), true);
        helper.interactItemAtBlock(TEST_ORIGIN, true);

        if (!((SoulFunnelBlockEntity) context.getBlockEntity(TEST_ORIGIN)).getPedestalPositions().contains(context.getAbsolutePos(TEST_ORIGIN.add(0, 0, 3))))
            throw new GameTestException("Expected pedestal to be saved in funnel");

        if (!((BlackstonePedestalBlockEntity) context.getBlockEntity(TEST_ORIGIN.add(0, 0, 3))).isLinked())
            throw new GameTestException("Expected pedestal to be linked");

        context.complete();
    }

    @GameTest(templateName = "conjuring_testmod:linking_weaver", tickLimit = 20)
    public void linkingWeaver(TestContext context) {
        var helper = new TestInteractionHelper(context);

        helper.setHandItem(new ItemStack(ConjuringItems.CONJURING_SCEPTER));

        helper.interactItemAtBlock(TEST_ORIGIN.add(0, 0, 3), true);
        helper.interactItemAtBlock(TEST_ORIGIN, true);

        if (!((SoulWeaverBlockEntity) context.getBlockEntity(TEST_ORIGIN)).getPedestalPositions().contains(context.getAbsolutePos(TEST_ORIGIN.add(0, 0, 3))))
            throw new GameTestException("Expected pedestal to be saved in weaver");

        if (!((BlackstonePedestalBlockEntity) context.getBlockEntity(TEST_ORIGIN.add(0, 0, 3))).isLinked())
            throw new GameTestException("Expected pedestal to be linked");

        context.complete();
    }

    @GameTest(templateName = "conjuring_testmod:pedestal_interaction", tickLimit = 20)
    public void pedestalInteraction(TestContext context) {
        var helper = new TestInteractionHelper(context);
        var pedestal = (BlackstonePedestalBlockEntity) context.getBlockEntity(TEST_ORIGIN);

        helper.clickBlock(TEST_ORIGIN, false);
        if (!helper.getPlayer().getStackInHand(Hand.MAIN_HAND).isOf(ConjuringItems.SOUL_ALLOY))
            throw new GameTestException("Expected some tasty soul alloy in my hjand");

        helper.clickBlock(TEST_ORIGIN, false);
        if (!pedestal.getItem().isOf(ConjuringItems.SOUL_ALLOY))
            throw new GameTestException("Expected soul alloy on pedestal");

        context.complete();
    }

    @GameTest(templateName = "conjuring_testmod:magnet", tickLimit = 200)
    public void magnet(TestContext context) {
        var helper = new TestInteractionHelper(context);
        var playerPos = Vec3d.ofCenter(context.getAbsolutePos(new BlockPos(1, 1, 0)));

        helper.setHandItem(ItemStack.EMPTY);

        helper.getPlayer().updatePositionAndAngles(playerPos.x, playerPos.y, playerPos.z, 0, 0);
        helper.getPlayer().setWorld(context.getWorld());

        helper.setHandItem(new ItemStack(ConjuringItems.SOUL_ALLOY_SHOVEL));
        helper.useItem(false);

        context.runAtTick(10, () -> helper.useItem(false));

        context.runAtTick(100, () -> {
            context.expectItemAt(ConjuringItems.IGNORANCE_GEM, TEST_ORIGIN, 2);
            context.complete();
        });
    }

    @GameTest(templateName = "conjuring_testmod:digger_veinmining")
    public void diggerVeinmining(TestContext context) {
        var helper = new TestInteractionHelper(context);
        var playerPos = Vec3d.ofCenter(context.getAbsolutePos(new BlockPos(1, 1, 0)));

        helper.setHandItem(ItemStack.EMPTY);

        helper.getPlayer().updatePositionAndAngles(playerPos.x, playerPos.y, playerPos.z, 0, 0);
        helper.getPlayer().setWorld(context.getWorld());

        final var stack = new ItemStack(ConjuringItems.SOUL_ALLOY_PICKAXE);
        SoulAlloyTool.toggleEnabledState(stack);
        helper.setHandItem(stack);
        helper.useItem(false);

        context.runAtTick(30, () -> {
            context.expectItemAt(Items.DIAMOND, new BlockPos(1, 1, 4), 2);
            context.complete();
        });
    }

    @GameTest(templateName = "conjuring_testmod:forge_crafting", tickLimit = 150)
    public void forgeCrafting(TestContext context) {
        context.pushButton(new BlockPos(0, 2, 1));

        context.runAtTick(130, () -> {
            var forge = (SoulfireForgeBlockEntity) context.getBlockEntity(TEST_ORIGIN);
            if (!forge.getItems().get(9).isOf(ConjuringItems.PIZZA))
                throw new GameTestException("Expected a freshly baked grandiosa");
            context.complete();
        });
    }

    @GameTest(templateName = "conjuring_testmod:gem_tinkerer_crafting", tickLimit = 150)
    public void gemTinkererCrafting(TestContext context) {
        var helper = new TestInteractionHelper(context);
        helper.clickBlock(TEST_ORIGIN, true);

        context.runAtTick(110, () -> {
            var tinkerer = (GemTinkererBlockEntity) context.getBlockEntity(TEST_ORIGIN);
            if (!tinkerer.getInventory().get(0).isOf(ConjuringItems.ABUNDANCE_GEM))
                throw new GameTestException("Expected crafting result to be an abundance gem");
            context.complete();
        });
    }

    @GameTest(templateName = "conjuring_testmod:gem_tinkerer_aspecting", tickLimit = 150)
    public void gemTinkererAspecting(TestContext context) {
        var helper = new TestInteractionHelper(context);
        helper.clickBlock(TEST_ORIGIN, true);

        context.runAtTick(110, () -> {
            var sword = ((GemTinkererBlockEntity) context.getBlockEntity(TEST_ORIGIN)).getInventory().get(0);

            if (!(sword.getItem() instanceof SoulAlloyTool)) throw new GameTestException("Sword somehow stopped being a soul alloy tool :knok:");
            if (SoulAlloyTool.getModifierLevel(sword, SoulAlloyTool.SoulAlloyModifier.ABUNDANCE) != 3)
                throw new GameTestException("Expected sword to have three abundance modifiers");
            if (SoulAlloyTool.getModifierLevel(sword, SoulAlloyTool.SoulAlloyModifier.IGNORANCE) != 1)
                throw new GameTestException("Expected sword to have one ignorance modifier");
            context.complete();
        });
    }

    @GameTest(templateName = "conjuring_testmod:extraction_ritual")
    public void extractionRitual(TestContext context) {
        final var funnelPos = new BlockPos(3, 1, 3);
        final var helper = new TestInteractionHelper(context);

        linkPedestals(context, funnelPos);

        helper.setHandItem(new ItemStack(ConjuringItems.CONJURING_SCEPTER));
        helper.clickBlock(funnelPos, false);

        context.runAtTick(90, () -> {
            var funnel = (SoulFunnelBlockEntity) context.getBlockEntity(funnelPos);

            if (!funnel.getItem().isEmpty()) throw new GameTestException("Expected soul funnel item to be empty");
            if (funnel.getCachedState().get(SoulFunnelBlock.FILLED)) throw new GameTestException("Expected soul funnel to be empty");
            context.expectItemAt(ConjuringItems.CONJURING_FOCUS, new BlockPos(3, 1, 3), 5);
            assertEmptyPedestals(helper);
            context.complete();
        });
    }

    @GameTest(templateName = "conjuring_testmod:weaving_ritual", tickLimit = 200)
    public void weavingRitual(TestContext context) {
        final var weaverPos = new BlockPos(3, 1, 3);
        final var helper = new TestInteractionHelper(context);

        linkPedestals(context, weaverPos);

        helper.setHandItem(new ItemStack(ConjuringItems.CONJURING_SCEPTER));
        helper.clickBlock(weaverPos, false);

        context.runAtTick(175, () -> {
            var weaver = (SoulWeaverBlockEntity) context.getBlockEntity(weaverPos);

            if (!weaver.getItem().isOf(ConjuringItems.DISTILLED_SPIRIT)) throw new GameTestException("Expected crafting result to be distilled spirit");
            assertEmptyPedestals(helper);
            context.complete();
        });
    }


    private void linkPedestals(TestContext context, BlockPos corePos) {
        var core = (RitualCore) context.getBlockEntity(corePos);
        core.getPedestalPositions().clear();
        forEachPedestal((integer, blockPos) -> {
            core.linkPedestal(context.getAbsolutePos(blockPos));
            ((BlackstonePedestalBlockEntity) context.getBlockEntity(blockPos)).setLinkedFunnel(context.getAbsolutePos(corePos));
        });
    }

    private void assertEmptyPedestals(TestInteractionHelper helper) {
        forEachPedestal((integer, blockPos) -> {
            if (!((BlackstonePedestalBlockEntity) helper.ctx.getBlockEntity(blockPos)).getItem().isEmpty())
                throw new GameTestException("Expected pedestal at " + helper.ctx.getAbsolutePos(blockPos) + " to be empty");
        });
    }

    private void forEachPedestal(BiConsumer<Integer, BlockPos> action) {
        for (int i = 0; i < PEDESTAL_OFFSETS.size(); i++) {
            action.accept(i, PEDESTAL_OFFSETS.get(i));
        }
    }

    static {
        PEDESTAL_OFFSETS = new ArrayList<>();
        PEDESTAL_OFFSETS.add(new BlockPos(3, 1, 0));
        PEDESTAL_OFFSETS.add(new BlockPos(0, 1, 3));
        PEDESTAL_OFFSETS.add(new BlockPos(3, 1, 6));
        PEDESTAL_OFFSETS.add(new BlockPos(6, 1, 3));
    }
}
