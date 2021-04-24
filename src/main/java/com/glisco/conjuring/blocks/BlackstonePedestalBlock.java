package com.glisco.conjuring.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlackstonePedestalBlock extends BlockWithEntity {

    private static final VoxelShape BASE1 = Block.createCuboidShape(0, 0, 0, 16, 2, 16);
    private static final VoxelShape BASE2 = Block.createCuboidShape(2, 2, 2, 14, 4, 14);
    private static final VoxelShape PILLAR = Block.createCuboidShape(4, 4, 4, 12, 17, 12);

    private static final VoxelShape PLATE_SOUTH = Block.createCuboidShape(4, 12, 12, 12, 16, 13);
    private static final VoxelShape PLATE_NORTH = Block.createCuboidShape(4, 12, 3, 12, 16, 4);
    private static final VoxelShape PLATE_WEST = Block.createCuboidShape(12, 12, 4, 13, 16, 12);
    private static final VoxelShape PLATE_EAST = Block.createCuboidShape(3, 12, 4, 4, 16, 12);

    private static final VoxelShape ARM_NW = Block.createCuboidShape(12, 11, 2, 14, 20, 4);
    private static final VoxelShape ARM_SW = Block.createCuboidShape(12, 11, 12, 14, 20, 14);
    private static final VoxelShape ARM_SE = Block.createCuboidShape(2, 11, 12, 4, 20, 14);
    private static final VoxelShape ARM_NE = Block.createCuboidShape(2, 11, 2, 4, 20, 4);

    private static final VoxelShape SHAPE = VoxelShapes.union(BASE1, BASE2, PILLAR, PLATE_SOUTH, PLATE_NORTH, PLATE_WEST, PLATE_EAST, ARM_NW, ARM_SW, ARM_SE, ARM_NE);


    //Construction stuff
    public BlackstonePedestalBlock() {
        super(AbstractBlock.Settings.copy(Blocks.BLACKSTONE).nonOpaque());
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BlackstonePedestalBlockEntity();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


    //Actual Logic
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlackstonePedestalBlockEntity pedestal = (BlackstonePedestalBlockEntity) world.getBlockEntity(pos);
        if (pedestal.isActive()) return ActionResult.PASS;

        ItemStack pedestalItem = pedestal.getRenderedItem();

        if (pedestalItem == null) {
            if (player.getStackInHand(hand).equals(ItemStack.EMPTY)) return ActionResult.PASS;

            ItemStack playerItem = player.getStackInHand(hand).copy();
            playerItem.setCount(1);

            pedestal.setRenderedItem(playerItem);

            playerItem = player.getStackInHand(hand).copy();
            playerItem.decrement(1);
            if (playerItem.isEmpty()) playerItem = ItemStack.EMPTY;
            player.setStackInHand(hand, playerItem);
        } else {
            ItemStack playerItemSingleton = player.getStackInHand(hand).copy();
            playerItemSingleton.setCount(1);

            if (player.getStackInHand(hand).equals(ItemStack.EMPTY)) {
                player.setStackInHand(hand, pedestalItem);
            } else if (ItemStack.areEqual(playerItemSingleton, pedestalItem) && player.getStackInHand(hand).getCount() + 1 <= player.getStackInHand(hand).getMaxCount()) {
                ItemStack playerItem = player.getStackInHand(hand);
                playerItem.setCount(playerItem.getCount() + 1);
                player.setStackInHand(hand, playerItem);
            } else {
                ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1f, pos.getZ(), pedestalItem);
            }
            pedestal.setRenderedItem(null);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {

            BlackstonePedestalBlockEntity pedestalEntity = (BlackstonePedestalBlockEntity) world.getBlockEntity(pos);

            if (pedestalEntity.getRenderedItem() != null) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), pedestalEntity.getRenderedItem());
            }

            if (pedestalEntity.isLinked()) {
                if (world.getBlockEntity(pedestalEntity.getLinkedFunnel()) instanceof RitualCore) {
                    ((RitualCore) world.getBlockEntity(pedestalEntity.getLinkedFunnel())).removePedestal(pos, pedestalEntity.isActive());
                }
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
