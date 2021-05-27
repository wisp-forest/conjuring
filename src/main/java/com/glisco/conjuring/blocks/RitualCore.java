package com.glisco.conjuring.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface RitualCore {

    boolean linkPedestal(BlockPos pedestal);

    boolean removePedestal(BlockPos pedestal, boolean pedestalActive);

    List<BlockPos> getPedestalPositions();

    boolean tryStartRitual(PlayerEntity player);

    default NbtCompound savePedestals(NbtCompound tag, List<BlockPos> pedestals) {
        NbtList pedestalsTag = new NbtList();
        for (BlockPos p : pedestals) {
            pedestalsTag.add(new NbtIntArray(new int[]{p.getX(), p.getY(), p.getZ()}));
        }
        tag.put("Pedestals", pedestalsTag);
        return tag;
    }

    default NbtCompound loadPedestals(NbtCompound tag, List<BlockPos> pedestals) {
        NbtList pedestalsTag = tag.getList("Pedestals", 11);
        pedestals.clear();
        for (NbtElement pedestal : pedestalsTag) {
            int[] intPos = ((NbtIntArray) pedestal).getIntArray();
            pedestals.add(new BlockPos(intPos[0], intPos[1], intPos[2]));
        }
        return tag;
    }
}
