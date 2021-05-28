package com.glisco.owo;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldOps {

    public static void breakBlockWithItem(World world, BlockPos pos, ItemStack breakItem) {
        TileEntity breakEntity = world.getBlockState(pos).getBlock().hasTileEntity(world.getBlockState(pos)) ? world.getTileEntity(pos) : null;
        Block.spawnDrops(world.getBlockState(pos), world, pos, breakEntity, null, breakItem);
        world.destroyBlock(pos, false, null);
    }

}
