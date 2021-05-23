package com.glisco.conjuring.blocks.gem_tinkerer;

import com.glisco.owo.ItemOps;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class GemTinkererBlock extends BlockWithEntity {

    private static final VoxelShape SHAPE = Block.createCuboidShape(6, 3.6, 6, 10, 14.8, 10);
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
        super(Settings.copy(Blocks.BLACKSTONE).nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        GemTinkererBlockEntity tinkerer = (GemTinkererBlockEntity) world.getBlockEntity(pos);
        final Integer sideIndex = SIDE_TO_INDEX.get(hit.getSide());

        if (tinkerer.isRunning()) {
            if (!tinkerer.isCraftingComplete() || !player.getStackInHand(hand).isEmpty()) return ActionResult.PASS;

            player.setStackInHand(hand, tinkerer.getInventory().get(sideIndex));
            tinkerer.getInventory().set(sideIndex, ItemStack.EMPTY);
            tinkerer.markDirty();
            return ActionResult.SUCCESS;
        }

        ItemStack playerStack = player.getStackInHand(hand);
        DefaultedList<ItemStack> tinkererInventory = tinkerer.getInventory();

        if (playerStack.isEmpty()) {

            if (player.isSneaking()) {

                if (hit.getSide() == Direction.UP) {
                    for (int i = 1; i < 5; i++) {

                        if (tinkererInventory.get(i).isEmpty()) continue;
                        player.inventory.offerOrDrop(world, tinkererInventory.get(i));

                        tinkererInventory.set(i, ItemStack.EMPTY);
                    }

                    tinkerer.markDirty();

                    return ActionResult.SUCCESS;
                }

                return tinkerer.onUse(player);
            }

            ItemStack sideStack = tinkererInventory.get(sideIndex);
            if (sideStack.isEmpty()) return ActionResult.PASS;

            player.setStackInHand(hand, sideStack);

            tinkererInventory.set(sideIndex, ItemStack.EMPTY);
            tinkerer.markDirty();
        } else {
            ItemStack sideStack = tinkererInventory.get(sideIndex);

            if (hit.getSide() == Direction.UP && !tinkererInventory.get(sideIndex).isEmpty() && !ItemStack.areItemsEqual(sideStack, playerStack)) {

                for (int i = 1; i < 5; i++) {
                    if (!tinkererInventory.get(i).isEmpty()) continue;
                    tinkererInventory.set(i, ItemOps.singleCopy(playerStack));

                    if (!ItemOps.emptyAwareDecrement(playerStack)) {
                        player.setStackInHand(hand, ItemStack.EMPTY);
                        break;
                    }
                }

                tinkerer.markDirty();
            } else {
                if (sideStack.isEmpty()) {
                    tinkererInventory.set(sideIndex, ItemOps.singleCopy(playerStack));
                    tinkerer.markDirty();

                    if (!ItemOps.emptyAwareDecrement(playerStack)) player.setStackInHand(hand, ItemStack.EMPTY);
                } else {
                    if (!ItemOps.canStack(playerStack, sideStack)) return ActionResult.PASS;

                    tinkererInventory.set(sideIndex, ItemStack.EMPTY);
                    tinkerer.markDirty();

                    playerStack.increment(1);
                }
            }

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
