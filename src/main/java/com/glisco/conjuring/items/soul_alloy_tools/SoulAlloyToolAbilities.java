package com.glisco.conjuring.items.soul_alloy_tools;

import com.glisco.conjuring.items.ConjuringItems;
import com.glisco.owo.ops.WorldOps;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class SoulAlloyToolAbilities {

    public static void registerCommonEvents() {
        PlayerBlockBreakEvents.BEFORE.register((world, playerEntity, blockPos, blockState, blockEntity) -> {

            if (!SoulAlloyToolAbilities.canAoeDig(playerEntity)) return true;

            for (BlockPos pos : SoulAlloyToolAbilities.getBlocksToDig(playerEntity)) {
                WorldOps.breakBlockWithItem(world, pos, playerEntity.getMainHandStack());

                playerEntity.getMainHandStack().damage(SoulAlloyTool.getModifierLevel(playerEntity.getMainHandStack(), SoulAlloyTool.SoulAlloyModifier.SCOPE) * 2, playerEntity, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
            }
            return true;
        });
    }

    public static boolean canAoeDig(PlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof SoulAlloyTool && SoulAlloyTool.isSecondaryEnabled(player.getMainHandStack()) && SoulAlloyTool.getModifiers(player.getMainHandStack()).containsKey(SoulAlloyTool.SoulAlloyModifier.SCOPE);
    }

    public static boolean canArmorPierce(PlayerEntity player) {
        return player.getMainHandStack().getItem() == ConjuringItems.SOUL_ALLOY_SWORD && SoulAlloyTool.getModifierLevel(player.getMainHandStack(), SoulAlloyTool.SoulAlloyModifier.IGNORANCE) > 0;
    }

    public static boolean canAoeHit(PlayerEntity player) {
        return player.getMainHandStack().getItem() == ConjuringItems.SOUL_ALLOY_SWORD && SoulAlloyTool.isSecondaryEnabled(player.getMainHandStack()) && SoulAlloyTool.getModifierLevel(player.getMainHandStack(), SoulAlloyTool.SoulAlloyModifier.SCOPE) > 0;
    }

    public static List<BlockPos> getBlocksToDig(PlayerEntity player) {

        if (!(player.getMainHandStack().getItem() instanceof SoulAlloyPickaxe || player.getMainHandStack().getItem() instanceof SoulAlloyShovel))
            return new ArrayList<>();

        List<BlockPos> blocksToDig = new ArrayList<>();

        HitResult target = player.raycast(player.getAbilities().creativeMode ? 5.0F : 4.5F, 0, false);
        if (target.getType() != HitResult.Type.BLOCK) return blocksToDig;

        BlockPos hit = ((BlockHitResult) target).getBlockPos();
        BlockPos origin = hit;
        Direction side = ((BlockHitResult) target).getSide();
        int scopeLevel = SoulAlloyTool.getModifiers(player.getMainHandStack()).get(SoulAlloyTool.SoulAlloyModifier.SCOPE);

        if (player.getMainHandStack().getItem().getMiningSpeedMultiplier(player.getMainHandStack(), player.world.getBlockState(hit)) == 1) return blocksToDig;

        switch (side.getAxis()) {
            case X -> {
                hit = hit.add(0, -1 * scopeLevel, -1 * scopeLevel);
                for (int i = 0; i < 1 + 2 * scopeLevel; i++) {
                    for (int j = 0; j < 1 + 2 * scopeLevel; j++) {
                        blocksToDig.add(hit.add(0, i, j));
                    }
                }
            }
            case Y -> {
                hit = hit.add(-1 * scopeLevel, 0, -1 * scopeLevel);
                for (int i = 0; i < 1 + 2 * scopeLevel; i++) {
                    for (int j = 0; j < 1 + 2 * scopeLevel; j++) {
                        blocksToDig.add(hit.add(j, 0, i));
                    }
                }
            }
            case Z -> {
                hit = hit.add(-1 * scopeLevel, -1 * scopeLevel, 0);
                for (int i = 0; i < 1 + 2 * scopeLevel; i++) {
                    for (int j = 0; j < 1 + 2 * scopeLevel; j++) {
                        blocksToDig.add(hit.add(j, i, 0));
                    }
                }
            }
        }

        blocksToDig.removeIf(blockPos -> player.world.getBlockState(blockPos).getHardness(player.world, blockPos) <= 0 || player.getMainHandStack().getItem().getMiningSpeedMultiplier(player.getMainHandStack(), player.world.getBlockState(blockPos)) <= 1);
        blocksToDig.remove(origin);

        return blocksToDig;
    }

}
