package com.glisco.conjuring.items.soul_alloy_tools;

import com.glisco.conjuring.util.ConjuringParticleEvents;
import io.wispforest.owo.ops.WorldOps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;

public class BlockCrawler {

    public static final BiFunction<Block, BlockState, Boolean> IDENTITY_PREDICATE = (block, blockState) -> block == blockState.getBlock();
    private static final ConcurrentLinkedQueue<CrawlData> blocksToCrawl = new ConcurrentLinkedQueue<>();

    public static void crawl(World world, BlockPos firstBlock, ItemStack breakStack, int maxBlocks) {
        crawl(world, firstBlock, breakStack, maxBlocks, IDENTITY_PREDICATE);
    }

    public static void crawl(World world, BlockPos firstBlock, ItemStack breakStack, int maxBlocks, BiFunction<Block, BlockState, Boolean> predicate) {

        if (world.isClient()) return;

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

                    if (foundBlocks.size() >= maxBlocks) break outerLoop;
                    if (!predicate.apply(blockType, world.getBlockState(pos)) || foundBlocks.contains(pos)) continue;

                    foundBlocks.add(pos);
                    scanBlocks.add(pos);

                }
            }

            counter++;
        } while (!scanBlocks.isEmpty() && counter < 25);

        blocksToCrawl.add(new CrawlData(world.getRegistryKey(), breakStack, foundBlocks));

    }

    public static void tick(World world) {

        if (world.getTime() % 2 != 0) return;

        for (CrawlData data : blocksToCrawl) {

            if (!data.world.getValue().equals(world.getRegistryKey().getValue())) continue;

            if (data.isEmpty()) {
                blocksToCrawl.remove(data);
                continue;
            }

            BlockPos pos = data.getFirstAndRemove();

            WorldOps.breakBlockWithItem(world, pos, data.mineItem);

            ConjuringParticleEvents.BREAK_BLOCK.spawn(world, Vec3d.of(pos), null);
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

    private static class CrawlData {

        public final RegistryKey<World> world;
        public final ItemStack mineItem;
        private final List<BlockPos> blocksToMine;

        public CrawlData(RegistryKey<World> world, ItemStack mineItem, List<BlockPos> blocksToMine) {
            this.world = world;
            this.mineItem = mineItem;
            this.blocksToMine = blocksToMine;
        }

        public boolean isEmpty() {
            return blocksToMine.isEmpty();
        }

        public BlockPos getFirstAndRemove() {
            BlockPos pos = blocksToMine.get(0);
            blocksToMine.remove(0);
            return pos;
        }

    }

}
