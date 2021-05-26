package com.glisco.conjuringforgery.blocks;

import com.glisco.owo.ItemOps;
import net.minecraft.block.Block;
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

    //Actual Logic
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        BlackstonePedestalTileEntity pedestal = (BlackstonePedestalTileEntity) world.getTileEntity(pos);
        if (pedestal.isActive()) return ActionResultType.PASS;

        final ItemStack playerStack = player.getHeldItem(hand);
        final ItemStack pedestalItem = pedestal.getItem();

        if (pedestalItem.isEmpty()) {
            if (playerStack.isEmpty()) return ActionResultType.PASS;

            pedestal.setItem(ItemOps.singleCopy(playerStack));

            if (!ItemOps.emptyAwareDecrement(playerStack)) player.setHeldItem(hand, ItemStack.EMPTY);
        } else {
            if (playerStack.isEmpty()) {
                player.setHeldItem(hand, pedestalItem);
            } else if (ItemOps.canStack(playerStack, pedestalItem)) {
                playerStack.grow(1);
            } else {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY() + 1f, pos.getZ(), pedestalItem);
            }
            pedestal.setItem(ItemStack.EMPTY);
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {

            BlackstonePedestalTileEntity pedestalEntity = (BlackstonePedestalTileEntity) world.getTileEntity(pos);

            if (!pedestalEntity.getItem().isEmpty()) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), pedestalEntity.getItem());
            }

            if (pedestalEntity.isLinked()) {
                if (world.getTileEntity(pedestalEntity.getLinkedFunnel()) instanceof RitualCore) {
                    ((RitualCore) world.getTileEntity(pedestalEntity.getLinkedFunnel())).removePedestal(pos, pedestalEntity.isActive());
                }
            }

            super.onReplaced(state, world, pos, newState, moved);
        }
    }
}
