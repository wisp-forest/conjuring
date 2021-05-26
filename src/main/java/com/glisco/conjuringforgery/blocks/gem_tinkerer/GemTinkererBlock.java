package com.glisco.conjuringforgery.blocks.gem_tinkerer;

import com.glisco.owo.ItemOps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;

public class GemTinkererBlock extends Block {

    private static final VoxelShape SHAPE = Block.makeCuboidShape(6, 3.6, 6, 10, 14.8, 10);
    private static final HashMap<Direction, Integer> SIDE_TO_INDEX = new HashMap<>();

    static {
        SIDE_TO_INDEX.put(Direction.EAST, 4);
        SIDE_TO_INDEX.put(Direction.NORTH, 2);
        SIDE_TO_INDEX.put(Direction.WEST, 1);
        SIDE_TO_INDEX.put(Direction.SOUTH, 3);
        SIDE_TO_INDEX.put(Direction.UP, 0);
        SIDE_TO_INDEX.put(Direction.DOWN, 0);
    }

    public GemTinkererBlock() {
        super(Properties.from(Blocks.BLACKSTONE).notSolid());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {

        GemTinkererBlockEntity tinkerer = (GemTinkererBlockEntity) world.getTileEntity(pos);
        final Integer sideIndex = SIDE_TO_INDEX.get(hit.getFace());

        if (tinkerer.isRunning()) {
            if (!tinkerer.isCraftingComplete() || !player.getHeldItem(hand).isEmpty()) return ActionResultType.PASS;

            player.setHeldItem(hand, tinkerer.getInventory().get(sideIndex));
            tinkerer.getInventory().set(sideIndex, ItemStack.EMPTY);
            tinkerer.markDirty();
            return ActionResultType.SUCCESS;
        }

        ItemStack playerStack = player.getHeldItem(hand);
        NonNullList<ItemStack> tinkererInventory = tinkerer.getInventory();

        if (playerStack.isEmpty()) {

            if (player.isSneaking()) {

                if (hit.getFace() == Direction.UP) {
                    for (int i = 1; i < 5; i++) {

                        if (tinkererInventory.get(i).isEmpty()) continue;
                        player.inventory.placeItemBackInInventory(world, tinkererInventory.get(i));

                        tinkererInventory.set(i, ItemStack.EMPTY);
                    }

                    tinkerer.markDirty();

                    return ActionResultType.SUCCESS;
                }

                return tinkerer.onUse(player);
            }

            ItemStack sideStack = tinkererInventory.get(sideIndex);
            if (sideStack.isEmpty()) return ActionResultType.PASS;

            player.setHeldItem(hand, sideStack);

            tinkererInventory.set(sideIndex, ItemStack.EMPTY);
            tinkerer.markDirty();
        } else {
            ItemStack sideStack = tinkererInventory.get(sideIndex);

            if (hit.getFace() == Direction.UP && !tinkererInventory.get(sideIndex).isEmpty() && !ItemStack.areItemsEqual(sideStack, playerStack)) {

                for (int i = 1; i < 5; i++) {
                    if (!tinkererInventory.get(i).isEmpty()) continue;
                    tinkererInventory.set(i, ItemOps.singleCopy(playerStack));

                    if (!ItemOps.emptyAwareDecrement(playerStack)) {
                        player.setHeldItem(hand, ItemStack.EMPTY);
                        break;
                    }
                }

                tinkerer.markDirty();
            } else {
                if (sideStack.isEmpty()) {
                    tinkererInventory.set(sideIndex, ItemOps.singleCopy(playerStack));
                    tinkerer.markDirty();

                    if (!ItemOps.emptyAwareDecrement(playerStack)) player.setHeldItem(hand, ItemStack.EMPTY);
                } else {
                    if (!ItemOps.canStack(playerStack, sideStack)) return ActionResultType.PASS;

                    tinkererInventory.set(sideIndex, ItemStack.EMPTY);
                    tinkerer.markDirty();

                    playerStack.grow(1);
                }
            }

        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            InventoryHelper.dropItems(world, pos, ((GemTinkererBlockEntity) world.getTileEntity(pos)).getInventory());
            super.onReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new GemTinkererBlockEntity();
    }
}
