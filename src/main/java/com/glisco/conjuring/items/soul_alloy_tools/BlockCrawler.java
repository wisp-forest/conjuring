package com.glisco.conjuring.items.soul_alloy_tools;

import com.glisco.conjuring.util.ConjuringParticleEvents;
import io.wispforest.owo.ops.WorldOps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;

public class BlockCrawler {

    public static final BiFunction<Block, BlockState, Boolean> IDENTITY_PREDICATE = (block, blockState) -> block == blockState.getBlock();
    private static final ConcurrentLinkedQueue<CrawlData> blocksToCrawl = new ConcurrentLinkedQueue<>();

    public static void crawl(World world, BlockPos firstBlock, ItemStack breakStack, UUID miner, int maxBlocks) {
        crawl(world, firstBlock, breakStack, miner, maxBlocks, IDENTITY_PREDICATE);
    }

    public static void crawl(World world, BlockPos firstBlock, ItemStack breakStack, UUID miner, int maxBlocks, BiFunction<Block, BlockState, Boolean> predicate) {

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
                for (BlockPos pos : BlockPos.iterate(foundBlock.add(-1, -1, -1), foundBlock.add(1, 1, 1))) {
                    if (foundBlocks.size() >= maxBlocks) break outerLoop;
                    if (!predicate.apply(blockType, world.getBlockState(pos)) || foundBlocks.contains(pos)) continue;

                    var immutable = pos.toImmutable();
                    foundBlocks.add(immutable);
                    scanBlocks.add(immutable);

                }
            }

            counter++;
        } while (!scanBlocks.isEmpty() && counter < 25);

        blocksToCrawl.add(new CrawlData(world.getRegistryKey(), breakStack, foundBlocks, miner));

    }

    public static void tick(ServerWorld world) {
        if (world.getTime() % 2 != 0) return;

        for (CrawlData data : blocksToCrawl) {
            if (!data.world.getValue().equals(world.getRegistryKey().getValue())) continue;

            if (data.isEmpty()) {
                blocksToCrawl.remove(data);
                continue;
            }

            BlockPos pos = data.getFirstAndRemove();
            WorldOps.breakBlockWithItem(world, pos, data.mineItem, world.getEntity(data.miner));
            ConjuringParticleEvents.BREAK_BLOCK.spawn(world, Vec3d.of(pos), null);
        }
    }

    private record CrawlData(RegistryKey<World> world, ItemStack mineItem, List<BlockPos> blocksToMine, UUID miner) {

        public boolean isEmpty() {
            return this.blocksToMine.isEmpty();
        }

        public BlockPos getFirstAndRemove() {
            return this.blocksToMine.remove(0);
        }
    }

}
