package com.glisco.conjuring.items;

import net.minecraft.block.Block;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BlockCrawler {

    public static ConcurrentLinkedQueue<MutablePair<Integer, List<BlockPos>>> blocksToCrawl = new ConcurrentLinkedQueue<>();

    public static void crawl(World world, BlockPos firstBlock) {

        Block blockType = world.getBlockState(firstBlock).getBlock();

        List<BlockPos> foundBlocks = new ArrayList<>(Collections.singletonList(firstBlock));
        ConcurrentLinkedQueue<BlockPos> scanBlocks = new ConcurrentLinkedQueue<>(foundBlocks);

        int counter = 0;
        do {

            //Scan current layer
            outerLoop:
            for (BlockPos foundBlock : scanBlocks) {

                scanBlocks.remove(foundBlock);

                //Scan neighbours
                for (BlockPos pos : getNeighbors(foundBlock)) {

                    if (foundBlocks.size() >= 128) break outerLoop;
                    if (!world.getBlockState(pos).getBlock().equals(blockType) || foundBlocks.contains(pos)) continue;

                    foundBlocks.add(pos);
                    scanBlocks.add(pos);

                }
            }

            counter++;
        } while (!scanBlocks.isEmpty() && counter < 25);

        blocksToCrawl.add(new MutablePair<>(0, foundBlocks));

    }

    public static void tick(World world) {

        for (MutablePair<Integer, List<BlockPos>> pair : blocksToCrawl) {
            if (pair.left > 0) {
                pair.left--;
                continue;
            }

            if (pair.getRight().isEmpty()) {
                blocksToCrawl.remove(pair);
                continue;
            }

            world.breakBlock(pair.getRight().get(0), true);

            BlockPos pos = pair.getRight().get(0);

            world.getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 50, world.getRegistryKey(),
                    new ParticleS2CPacket(ParticleTypes.SOUL_FIRE_FLAME, false, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.2f, 0.2f, 0.2f, 0.01f, 5));

            pair.getRight().remove(0);
            pair.left = 2;

        }
    }

    public static List<BlockPos> getNeighbors(BlockPos center) {

        ArrayList<BlockPos> list = new ArrayList<>();
        BlockPos original = center;

        center = center.up();

        for (int i = 0; i < 3; i++) {
            list.add(center);
            list.add(center.east());
            list.add(center.west());
            list.add(center.north());
            list.add(center.south());

            list.add(center.south().west());
            list.add(center.south().east());

            list.add(center.north().west());
            list.add(center.north().east());

            center = center.down();
        }

        list.remove(original);
        return list;
    }

}
