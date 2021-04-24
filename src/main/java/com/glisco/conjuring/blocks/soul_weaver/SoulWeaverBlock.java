package com.glisco.conjuring.blocks.soul_weaver;

import com.glisco.conjuring.WorldHelper;
import com.glisco.conjuring.items.ConjuringScepter;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.stream.Stream;

public class SoulWeaverBlock extends BlockWithEntity {

    private static final VoxelShape SHAPE = Stream.of(
            Block.createCuboidShape(2, 0, 2, 14, 2, 14),
            Block.createCuboidShape(4, 2, 4, 12, 4, 12),
            Block.createCuboidShape(10, 4, 4, 12, 9, 6),
            Block.createCuboidShape(10, 4, 10, 12, 9, 12),
            Block.createCuboidShape(4, 14, 4, 12, 16, 12),
            Block.createCuboidShape(5, 9, 5, 11, 14, 11),
            Block.createCuboidShape(4, 9, 11, 12, 12, 12),
            Block.createCuboidShape(4, 9, 4, 12, 12, 5),
            Block.createCuboidShape(11, 9, 5, 12, 12, 11),
            Block.createCuboidShape(4, 9, 5, 5, 12, 11),
            Block.createCuboidShape(4, 4, 4, 6, 9, 6),
            Block.createCuboidShape(4, 4, 10, 6, 9, 12)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    public SoulWeaverBlock() {
        super(Settings.copy(Blocks.BLACKSTONE).nonOpaque());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new SoulWeaverBlockEntity();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        for (int i = 0; i < 2; i++) {
            WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 0.35f, 0.5f, 0, 0, 0, 0.15f);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        SoulWeaverBlockEntity weaver = (SoulWeaverBlockEntity) world.getBlockEntity(pos);

        if (player.getStackInHand(hand).getItem() instanceof ConjuringScepter) {
            weaver.tryStartRitual();
            return ActionResult.SUCCESS;
        }

        ItemStack weaverItem = weaver.getItem();

        if (weaverItem == null) {
            if (player.getStackInHand(hand).equals(ItemStack.EMPTY)) return ActionResult.PASS;

            ItemStack playerItem = player.getStackInHand(hand).copy();
            playerItem.setCount(1);

            weaver.setItem(playerItem);

            playerItem = player.getStackInHand(hand).copy();
            playerItem.decrement(1);
            if (playerItem.isEmpty()) playerItem = ItemStack.EMPTY;
            player.setStackInHand(hand, playerItem);
        } else {
            ItemStack playerItemSingleton = player.getStackInHand(hand).copy();
            playerItemSingleton.setCount(1);

            if (player.getStackInHand(hand).equals(ItemStack.EMPTY)) {
                player.setStackInHand(hand, weaverItem);
            } else if (ItemStack.areEqual(playerItemSingleton, weaverItem) && player.getStackInHand(hand).getCount() + 1 <= player.getStackInHand(hand).getMaxCount()) {
                ItemStack playerItem = player.getStackInHand(hand);
                playerItem.setCount(playerItem.getCount() + 1);
                player.setStackInHand(hand, playerItem);
            } else {
                ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1f, pos.getZ(), weaverItem);
            }
            weaver.setItem(null);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SoulWeaverBlockEntity) {
                SoulWeaverBlockEntity weaverEntity = (SoulWeaverBlockEntity) blockEntity;

                if (weaverEntity.getItem() != null) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), weaverEntity.getItem());
                }

            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
