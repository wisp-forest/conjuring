package com.glisco.conjuringforgery.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface RitualCore {

    boolean linkPedestal(BlockPos pedestal);

    boolean removePedestal(BlockPos pedestal, boolean pedestalActive);

    List<BlockPos> getPedestalPositions();

    boolean tryStartRitual(PlayerEntity player);

    default CompoundNBT savePedestals(CompoundNBT tag, List<BlockPos> pedestals) {
        ListNBT pedestalsTag = new ListNBT();
        for (BlockPos p : pedestals) {
            pedestalsTag.add(new IntArrayNBT(new int[]{p.getX(), p.getY(), p.getZ()}));
        }
        tag.put("Pedestals", pedestalsTag);
        return tag;
    }

    default CompoundNBT loadPedestals(CompoundNBT tag, List<BlockPos> pedestals) {
        ListNBT pedestalsTag = tag.getList("Pedestals", 11);
        pedestals.clear();
        for (INBT pedestal : pedestalsTag) {
            int[] intPos = ((IntArrayNBT) pedestal).getIntArray();
            pedestals.add(new BlockPos(intPos[0], intPos[1], intPos[2]));
        }
        return tag;
    }
}
