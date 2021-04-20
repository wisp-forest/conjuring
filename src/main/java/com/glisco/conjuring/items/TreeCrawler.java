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

public class TreeCrawler {

    public static ConcurrentLinkedQueue<MutablePair<Integer, List<BlockPos>>> treesToFell = new ConcurrentLinkedQueue<>();

    public static void crawlTree(World world, BlockPos firstLog) {

        Block logType = world.getBlockState(firstLog).getBlock();

        List<BlockPos> foundLogs = new ArrayList<>(Collections.singletonList(firstLog));
        ConcurrentLinkedQueue<BlockPos> scanLogs = new ConcurrentLinkedQueue<>(foundLogs);

        int counter = 0;
        do {

            //Scan current layer
            outerLoop:
            for (BlockPos foundLog : scanLogs) {

                scanLogs.remove(foundLog);

                //Scan neighbours
                for (BlockPos pos : getNeighbors(foundLog)) {

                    if (foundLogs.size() >= 128) break outerLoop;
                    if (!world.getBlockState(pos).getBlock().equals(logType) || foundLogs.contains(pos)) continue;

                    foundLogs.add(pos);
                    scanLogs.add(pos);

                }
            }

            counter++;
        } while (!scanLogs.isEmpty() && counter < 25);

        treesToFell.add(new MutablePair<>(0, foundLogs));

    }

    public static void tick(World world) {

        for (MutablePair<Integer, List<BlockPos>> pair : treesToFell) {
            if (pair.left > 0) {
                pair.left--;
                continue;
            }

            if (pair.getRight().isEmpty()) {
                treesToFell.remove(pair);
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
