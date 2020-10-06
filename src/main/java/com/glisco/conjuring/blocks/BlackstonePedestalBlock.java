package com.glisco.conjuring.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
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

    private static VoxelShape BASE1 = Block.createCuboidShape(0, 0, 0, 16, 2, 16);
    private static VoxelShape BASE2 = Block.createCuboidShape(2, 2, 2, 14, 4, 14);
    private static VoxelShape PILLAR = Block.createCuboidShape(4, 3, 4, 12, 16, 12);

    private static VoxelShape ARM1 = Block.createCuboidShape(3, 10, 7, 4, 18, 9);
    private static VoxelShape ARM2 = Block.createCuboidShape(2, 13, 7, 3, 20, 9);
    private static VoxelShape ARM3 = Block.createCuboidShape(1, 16, 7, 2, 22, 9);
    private static VoxelShape ARM4 = Block.createCuboidShape(7, 10, 3, 9, 18, 4);
    private static VoxelShape ARM5 = Block.createCuboidShape(7, 13, 2, 9, 20, 3);
    private static VoxelShape ARM6 = Block.createCuboidShape(7, 16, 1, 9, 22, 2);
    private static VoxelShape ARM7 = Block.createCuboidShape(12, 10, 7, 13, 18, 9);
    private static VoxelShape ARM8 = Block.createCuboidShape(13, 13, 7, 14, 20, 9);
    private static VoxelShape ARM9 = Block.createCuboidShape(14, 16, 7, 15, 22, 9);
    private static VoxelShape ARM10 = Block.createCuboidShape(7, 10, 12, 9, 18, 13);
    private static VoxelShape ARM11 = Block.createCuboidShape(7, 13, 13, 9, 20, 14);
    private static VoxelShape ARM12 = Block.createCuboidShape(7, 16, 14, 9, 22, 15);

    private static VoxelShape SHAPE = VoxelShapes.union(BASE1, BASE2, PILLAR, ARM1, ARM2, ARM3, ARM4, ARM5, ARM6, ARM7, ARM8, ARM9, ARM10, ARM11, ARM12);


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
        if (pedestal == null) {
            player.sendMessage(new LiteralText("Null BlockEntity?"), true);
            return ActionResult.PASS;
        }

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
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BlackstonePedestalBlockEntity) {
                if (((BlackstonePedestalBlockEntity) blockEntity).getRenderedItem() != null) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), ((BlackstonePedestalBlockEntity) blockEntity).getRenderedItem());
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
