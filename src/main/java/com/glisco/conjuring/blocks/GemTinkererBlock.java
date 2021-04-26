package com.glisco.conjuring.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GemTinkererBlock extends BlockWithEntity {

    private static final VoxelShape SHAPE = Block.createCuboidShape(6, 3.6, 6, 10, 14.8, 10);

    public GemTinkererBlock() {
        super(Settings.copy(Blocks.BLACKSTONE).nonOpaque());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        GemTinkererBlockEntity tinkerer = (GemTinkererBlockEntity) world.getBlockEntity(pos);

        if (tinkerer.isRunning()) return ActionResult.PASS;

        ItemStack playerStack = player.getStackInHand(hand);
        DefaultedList<ItemStack> tinkererInventory = tinkerer.getInventory();

        if (playerStack.isEmpty()) {

            if (player.isSneaking()) {
                return tinkerer.onUse();
            }

            int lastStackIndex = -1;

            for (int i = tinkererInventory.size() - 1; i >= 0; i--) {
                if (tinkererInventory.get(i).isEmpty()) continue;
                lastStackIndex = i;
                break;
            }

            if (lastStackIndex == -1) return ActionResult.PASS;

            player.setStackInHand(hand, tinkererInventory.get(lastStackIndex));

            tinkererInventory.set(lastStackIndex, ItemStack.EMPTY);
            tinkerer.markDirty();
        } else {

            int firstEmptyStackIndex = -1;

            for (int i = 0; i < tinkererInventory.size(); i++) {
                if (!tinkererInventory.get(i).isEmpty()) continue;
                firstEmptyStackIndex = i;
                break;
            }

            if (firstEmptyStackIndex == -1) return ActionResult.PASS;

            ItemStack single = playerStack.copy();
            single.setCount(1);

            tinkererInventory.set(firstEmptyStackIndex, single);
            tinkerer.markDirty();

            playerStack.decrement(1);
            if (playerStack.isEmpty()) player.setStackInHand(hand, ItemStack.EMPTY);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            ItemScatterer.spawn(world, pos, ((GemTinkererBlockEntity) world.getBlockEntity(pos)).getInventory());
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new GemTinkererBlockEntity();
    }
}
