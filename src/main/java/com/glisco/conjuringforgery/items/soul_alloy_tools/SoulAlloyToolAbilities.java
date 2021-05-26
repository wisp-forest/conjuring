package com.glisco.conjuringforgery.items.soul_alloy_tools;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

import java.util.ArrayList;
import java.util.List;

public class SoulAlloyToolAbilities {

    public static boolean canAoeDig(PlayerEntity player) {
        return player.getHeldItemMainhand().getItem() instanceof SoulAlloyTool && SoulAlloyTool.isSecondaryEnabled(player.getHeldItemMainhand()) && SoulAlloyTool.getModifiers(player.getHeldItemMainhand()).containsKey(SoulAlloyTool.SoulAlloyModifier.SCOPE);
    }

    public static boolean canArmorPierce(PlayerEntity player) {
        return player.getHeldItemMainhand().getItem() == ConjuringForgery.SOUL_ALLOY_SWORD.get() && SoulAlloyTool.getModifierLevel(player.getHeldItemMainhand(), SoulAlloyTool.SoulAlloyModifier.IGNORANCE) > 0;
    }

    public static boolean canAoeHit(PlayerEntity player) {
        return player.getHeldItemMainhand().getItem() == ConjuringForgery.SOUL_ALLOY_SWORD.get() && SoulAlloyTool.isSecondaryEnabled(player.getHeldItemMainhand()) && SoulAlloyTool.getModifierLevel(player.getHeldItemMainhand(), SoulAlloyTool.SoulAlloyModifier.SCOPE) > 0;
    }

    public static List<BlockPos> getBlocksToDig(PlayerEntity player) {

        if (!(player.getHeldItemMainhand().getItem() instanceof SoulAlloyPickaxe || player.getHeldItemMainhand().getItem() instanceof SoulAlloyShovel))
            return new ArrayList<>();

        List<BlockPos> blocksToDig = new ArrayList<>();

        RayTraceResult target = player.pick(player.abilities.isCreativeMode ? 5.0F : 4.5F, 0, false);
        if (target.getType() != RayTraceResult.Type.BLOCK) return blocksToDig;

        BlockPos hit = ((BlockRayTraceResult) target).getPos();
        BlockPos origin = hit;
        Direction side = ((BlockRayTraceResult) target).getFace();
        int scopeLevel = SoulAlloyTool.getModifiers(player.getHeldItemMainhand()).get(SoulAlloyTool.SoulAlloyModifier.SCOPE);

        if (player.getHeldItemMainhand().getItem().getDestroySpeed(player.getHeldItemMainhand(), player.world.getBlockState(hit)) == 1) return blocksToDig;

        switch (side.getAxis()) {
            case X:

                hit = hit.add(0, -1 * scopeLevel, -1 * scopeLevel);

                for (int i = 0; i < 1 + 2 * scopeLevel; i++) {
                    for (int j = 0; j < 1 + 2 * scopeLevel; j++) {
                        blocksToDig.add(hit.add(0, i, j));
                    }
                }

                break;
            case Y:
                hit = hit.add(-1 * scopeLevel, 0, -1 * scopeLevel);

                for (int i = 0; i < 1 + 2 * scopeLevel; i++) {
                    for (int j = 0; j < 1 + 2 * scopeLevel; j++) {
                        blocksToDig.add(hit.add(j, 0, i));
                    }
                }
                break;
            case Z:
                hit = hit.add(-1 * scopeLevel, -1 * scopeLevel, 0);

                for (int i = 0; i < 1 + 2 * scopeLevel; i++) {
                    for (int j = 0; j < 1 + 2 * scopeLevel; j++) {
                        blocksToDig.add(hit.add(j, i, 0));
                    }
                }
                break;
        }


        blocksToDig.removeIf(blockPos -> player.world.getBlockState(blockPos).getBlockHardness(player.world, blockPos) <= 0 || player.getHeldItemMainhand().getItem().getDestroySpeed(player.getHeldItemMainhand(), player.world.getBlockState(blockPos)) <= 1);
        blocksToDig.remove(origin);

        return blocksToDig;
    }

}
