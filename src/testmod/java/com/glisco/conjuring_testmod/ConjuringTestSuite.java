package com.glisco.conjuring_testmod;

import com.glisco.conjuring.items.ConjuringItems;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class ConjuringTestSuite {

    @GameTest(structureName = "conjuring_testmod:extraction_ritual", tickLimit = 1000)
    public void extractionRitual(TestContext context) {
        final var funnelPos = new BlockPos(3, 1, 3);
        final var helper = new TestInteractionHelper(context);

        context.runAtTick(0, link(0, helper, new BlockPos(0, 1, 3)));
        context.runAtTick(30, link(40, helper, new BlockPos(3, 1, 0)));
        context.runAtTick(60, link(40, helper, new BlockPos(3, 1, 6)));
        context.runAtTick(90, link(40, helper, new BlockPos(6, 1, 3)));

        context.runAtTick(100, () -> {
            context.spawnMob(EntityType.CREEPER, new BlockPos(3, 2, 3));

            helper.setHandItem(new ItemStack(Items.GUNPOWDER, 4));
            helper.clickBlock(new BlockPos(3, 1, 0), false);
            helper.clickBlock(new BlockPos(0, 1, 3), false);
            helper.clickBlock(new BlockPos(3, 1, 6), false);
            helper.clickBlock(new BlockPos(6, 1, 3), false);

            helper.setHandItem(new ItemStack(Items.SOUL_SAND));
            helper.clickBlock(funnelPos, false);

            helper.setHandItem(new ItemStack(ConjuringItems.CONJURING_FOCUS));
            helper.clickBlock(funnelPos, false);

            helper.setHandItem(new ItemStack(ConjuringItems.CONJURING_SCEPTER));
            helper.clickBlock(funnelPos, false);
        });

        context.runAtTick(200, () -> {
            context.expectItemAt(ConjuringItems.CONJURING_FOCUS, new BlockPos(3, 1, 3), 5);
            context.complete();
        });
    }

    private Runnable link(int tick, TestInteractionHelper helper, BlockPos pedestal) {
        return () -> {
            helper.setHandItem(new ItemStack(ConjuringItems.CONJURING_SCEPTER));
            helper.interactItemAtBlock(pedestal, true);
            helper.ctx.runAtTick(tick + 10, () -> helper.interactItemAtBlock(new BlockPos(3, 1, 3), true));
        };
    }

}
