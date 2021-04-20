package com.glisco.conjuring.items;

import net.minecraft.block.Block;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TreeCrawler {

    public static ConcurrentLinkedQueue<MutablePair<Integer, List<BlockPos>>> treesToFell = new ConcurrentLinkedQueue<>();

    public static void crawlTree(World world, BlockPos firstLog) {

        Block logType = world.getBlockState(firstLog).getBlock();

        List<BlockPos> foundLogs = new ArrayList<>(Collections.singletonList(firstLog));



        ConcurrentLinkedQueue<BlockPos> scanLogs = new ConcurrentLinkedQueue<>(foundLogs);
        List<BlockPos> nextScanLogs = new ArrayList<>();

        boolean anyFound;
        int counter = 0;
        int scanCounter = 0;

        do {
            anyFound = false;

            nextScanLogs.clear();

            //Scan current layer
            for (BlockPos foundLog : scanLogs) {

                scanLogs.remove(foundLog);

                //Scan neighbours
                for (BlockPos pos : getNeighbors(foundLog)) {

                    scanCounter++;
                    System.out.println(scanCounter);
                    if (!world.getBlockState(pos).getBlock().equals(logType) || foundLogs.contains(pos)) continue;

                    anyFound = true;
                    foundLogs.add(pos);
                    nextScanLogs.add(pos);

                }
            }

            scanLogs = new ConcurrentLinkedQueue<>(nextScanLogs);
            counter++;
        } while (anyFound && counter < 10);

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
        return Arrays.asList(center.up(), center.down(), center.east(), center.west(), center.south(), center.north());
    }

}
