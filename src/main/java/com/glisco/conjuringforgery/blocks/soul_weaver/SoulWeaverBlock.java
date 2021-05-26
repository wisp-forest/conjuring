package com.glisco.conjuringforgery.blocks.soul_weaver;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.blocks.BlackstonePedestalTileEntity;
import com.glisco.conjuringforgery.items.ConjuringScepter;
import com.glisco.owo.ItemOps;
import com.glisco.owo.client.ClientParticles;
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
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Stream;

public class SoulWeaverBlock extends Block {

    private static final VoxelShape SHAPE = Stream.of(
            Block.makeCuboidShape(2, 0, 2, 14, 2, 14),
            Block.makeCuboidShape(4, 2, 4, 12, 4, 12),
            Block.makeCuboidShape(10, 4, 4, 12, 9, 6),
            Block.makeCuboidShape(10, 4, 10, 12, 9, 12),
            Block.makeCuboidShape(4, 14, 4, 12, 16, 12),
            Block.makeCuboidShape(5, 9, 5, 11, 14, 11),
            Block.makeCuboidShape(4, 9, 11, 12, 12, 12),
            Block.makeCuboidShape(4, 9, 4, 12, 12, 5),
            Block.makeCuboidShape(11, 9, 5, 12, 12, 11),
            Block.makeCuboidShape(4, 9, 5, 5, 12, 11),
            Block.makeCuboidShape(4, 4, 4, 6, 9, 6),
            Block.makeCuboidShape(4, 4, 10, 6, 9, 12)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    public SoulWeaverBlock() {
        super(Properties.from(Blocks.BLACKSTONE).notSolid());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SoulWeaverTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        SoulWeaverTileEntity weaver = (SoulWeaverTileEntity) world.getTileEntity(pos);

        if (weaver.isRunning()) return ActionResultType.PASS;

        final ItemStack playerStack = player.getHeldItem(hand);
        final ItemStack weaverItem = weaver.getItem();

        if (playerStack.getItem().equals(ConjuringForgery.CONJURATION_ESSENCE.get()) && !weaver.isLit()) {
            weaver.setLit(true);
            if (!ItemOps.emptyAwareDecrement(playerStack)) player.setHeldItem(hand, ItemStack.EMPTY);
            return ActionResultType.SUCCESS;
        }

        if (playerStack.getItem() instanceof ConjuringScepter) {
            weaver.tryStartRitual(player);
            return ActionResultType.SUCCESS;
        }


        if (weaverItem.isEmpty()) {
            if (playerStack.isEmpty()) return ActionResultType.PASS;

            weaver.setItem(ItemOps.singleCopy(playerStack));

            if (!ItemOps.emptyAwareDecrement(playerStack)) player.setHeldItem(hand, ItemStack.EMPTY);
        } else {
            if (playerStack.isEmpty()) {
                player.setHeldItem(hand, weaverItem);
            } else if (ItemOps.canStack(playerStack, weaverItem)) {
                playerStack.grow(1);
            } else {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY() + 1f, pos.getZ(), weaverItem);
            }
            weaver.setItem(ItemStack.EMPTY);
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity blockEntity = world.getTileEntity(pos);
            if (blockEntity instanceof SoulWeaverTileEntity) {
                SoulWeaverTileEntity weaverEntity = (SoulWeaverTileEntity) blockEntity;
                weaverEntity.onBroken();
            }
            super.onReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        SoulWeaverTileEntity weaver = (SoulWeaverTileEntity) world.getTileEntity(pos);

        for (BlockPos p : weaver.getPedestalPositions()) {
            if (random.nextDouble() > 0.5f) continue;
            BlackstonePedestalTileEntity pedestal = (BlackstonePedestalTileEntity) world.getTileEntity(p);
            if (pedestal == null) continue;
            if (pedestal.getLinkedFunnel() == null) continue;
            if (pedestal.getLinkedFunnel().compareTo(pos) != 0) return;

            ClientParticles.spawnEnchantParticles(world, Vector3d.copy(p).add(0.5, 0.5, 0.5), Vector3d.copy(pos).add(0.5, 1.5, 0.5), 0);
        }
    }
}
