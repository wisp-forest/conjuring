package com.glisco.conjuring.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface RitualCore {

    boolean linkPedestal(BlockPos pedestal);

    boolean removePedestal(BlockPos pedestal, boolean pedestalActive);

    List<BlockPos> getPedestalPositions();

    boolean tryStartRitual(PlayerEntity player);

    default CompoundTag savePedestals(CompoundTag tag, List<BlockPos> pedestals) {
        ListTag pedestalsTag = new ListTag();
        for (BlockPos p : pedestals) {
            pedestalsTag.add(new IntArrayTag(new int[]{p.getX(), p.getY(), p.getZ()}));
        }
        tag.put("Pedestals", pedestalsTag);
        return tag;
    }

    default CompoundTag loadPedestals(CompoundTag tag, List<BlockPos> pedestals) {
        ListTag pedestalsTag = tag.getList("Pedestals", 11);
        pedestals.clear();
        for (Tag pedestal : pedestalsTag) {
            int[] intPos = ((IntArrayTag) pedestal).getIntArray();
            pedestals.add(new BlockPos(intPos[0], intPos[1], intPos[2]));
        }
        return tag;
    }
}
