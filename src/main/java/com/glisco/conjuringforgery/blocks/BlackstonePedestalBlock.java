package com.glisco.conjuringforgery.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlackstonePedestalBlock extends Block {

    private static VoxelShape BASE1 = Block.makeCuboidShape(0, 0, 0, 16, 2, 16);
    private static VoxelShape BASE2 = Block.makeCuboidShape(2, 2, 2, 14, 4, 14);
    private static VoxelShape PILLAR = Block.makeCuboidShape(4, 4, 4, 12, 17, 12);

    private static VoxelShape PLATE_SOUTH = Block.makeCuboidShape(4, 12, 12, 12, 16, 13);
    private static VoxelShape PLATE_NORTH = Block.makeCuboidShape(4, 12, 3, 12, 16, 4);
    private static VoxelShape PLATE_WEST = Block.makeCuboidShape(12, 12, 4, 13, 16, 12);
    private static VoxelShape PLATE_EAST = Block.makeCuboidShape(3, 12, 4, 4, 16, 12);

    private static VoxelShape ARM_NW = Block.makeCuboidShape(12, 11, 2, 14, 20, 4);
    private static VoxelShape ARM_SW = Block.makeCuboidShape(12, 11, 12, 14, 20, 14);
    private static VoxelShape ARM_SE = Block.makeCuboidShape(2, 11, 12, 4, 20, 14);
    private static VoxelShape ARM_NE = Block.makeCuboidShape(2, 11, 2, 4, 20, 4);

    private static VoxelShape SHAPE = VoxelShapes.or(BASE1, BASE2, PILLAR, PLATE_SOUTH, PLATE_NORTH, PLATE_WEST, PLATE_EAST, ARM_NW, ARM_SW, ARM_SE, ARM_NE);

    //Construction stuff
    public BlackstonePedestalBlock() {
        super(Properties.from(Blocks.BLACKSTONE)
                .notSolid()
                .setOpaque((p_test_1_, p_test_2_, p_test_3_) -> false));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BlackstonePedestalTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


    //Actual Logic
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        BlackstonePedestalTileEntity pedestal = (BlackstonePedestalTileEntity) world.getTileEntity(pos);
        if (pedestal.isActive()) return ActionResultType.PASS;

        ItemStack pedestalItem = pedestal.getRenderedItem();

        if (pedestalItem == null) {
            if (player.getHeldItem(hand).equals(ItemStack.EMPTY)) return ActionResultType.PASS;

            ItemStack playerItem = player.getHeldItem(hand).copy();
            playerItem.setCount(1);

            pedestal.setRenderedItem(playerItem);

            playerItem = player.getHeldItem(hand).copy();
            playerItem.shrink(1);
            if (playerItem.isEmpty()) playerItem = ItemStack.EMPTY;
            player.setHeldItem(hand, playerItem);
        } else {
            ItemStack playerItemSingleton = player.getHeldItem(hand).copy();
            playerItemSingleton.setCount(1);

            if (player.getHeldItem(hand).equals(ItemStack.EMPTY)) {
                player.setHeldItem(hand, pedestalItem);
            } else if (ItemStack.areItemStacksEqual(playerItemSingleton, pedestalItem) && player.getHeldItem(hand).getCount() + 1 <= player.getHeldItem(hand).getMaxStackSize()) {
                ItemStack playerItem = player.getHeldItem(hand);
                playerItem.setCount(playerItem.getCount() + 1);
                player.setHeldItem(hand, playerItem);
            } else {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY() + 1f, pos.getZ(), pedestalItem);
            }
            pedestal.setRenderedItem(null);
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity blockEntity = world.getTileEntity(pos);
            if (blockEntity instanceof BlackstonePedestalTileEntity) {
                BlackstonePedestalTileEntity pedestalEntity = (BlackstonePedestalTileEntity) blockEntity;

                if (pedestalEntity.getRenderedItem() != null) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), pedestalEntity.getRenderedItem());
                }

                if (pedestalEntity.isLinked()) {
                    if (world.getTileEntity(pedestalEntity.getLinkedFunnel()) instanceof SoulFunnelTileEntity) {
                        ((SoulFunnelTileEntity) world.getTileEntity(pedestalEntity.getLinkedFunnel())).removePedestal(pos, pedestalEntity.isActive());
                    }
                }
            }
            super.onReplaced(state, world, pos, newState, moved);
        }
    }
}
