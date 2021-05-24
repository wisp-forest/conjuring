package com.glisco.conjuringforgery.blocks;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SoulFunnelBlock extends Block {

    private static VoxelShape PILLAR1 = Block.makeCuboidShape(12, 0, 0, 16, 9, 4);
    private static VoxelShape PILLAR2 = Block.makeCuboidShape(12, 0, 12, 16, 9, 16);
    private static VoxelShape PILLAR3 = Block.makeCuboidShape(0, 0, 12, 4, 9, 16);
    private static VoxelShape PILLAR4 = Block.makeCuboidShape(0, 0, 0, 4, 9, 4);

    private static VoxelShape WALL1 = Block.makeCuboidShape(4, 0, 0, 12, 6, 4);
    private static VoxelShape WALL2 = Block.makeCuboidShape(12, 0, 4, 16, 6, 12);
    private static VoxelShape WALL3 = Block.makeCuboidShape(4, 0, 12, 12, 6, 16);
    private static VoxelShape WALL4 = Block.makeCuboidShape(0, 0, 4, 4, 6, 12);

    private static VoxelShape SOUL_SAND = Block.makeCuboidShape(4, 0, 4, 12, 5, 12);

    private static VoxelShape SHAPE = VoxelShapes.or(SOUL_SAND, PILLAR1, PILLAR2, PILLAR3, PILLAR4, WALL1, WALL2, WALL3, WALL4);

    public static BooleanProperty FILLED = BooleanProperty.create("filled");


    //Construction stuff
    public SoulFunnelBlock() {
        super(Properties.from(Blocks.BLACKSTONE)
                .notSolid()
                .setOpaque((p_test_1_, p_test_2_, p_test_3_) -> false));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SoulFunnelTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    //BlockState shit
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FILLED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(FILLED, false);
    }

    //Actual Logic
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {

        //Pedestal highlighting logic
        if (player.getHeldItem(hand).equals(ItemStack.EMPTY) && player.isSneaking()) {

            if (world.isRemote) {

                List<BlockPos> possiblePedestals = new ArrayList<>();
                possiblePedestals.add(pos.add(3, 0, 0));
                possiblePedestals.add(pos.add(-3, 0, 0));
                possiblePedestals.add(pos.add(0, 0, 3));
                possiblePedestals.add(pos.add(0, 0, -3));

                for (BlockPos pedestal : possiblePedestals) {
                    if (world.getTileEntity(pedestal) instanceof BlackstonePedestalTileEntity) continue;
                    for (int i = 0; i < 50; i++) {
                        WorldHelper.spawnParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, world, pedestal, 0.5f, 0.75f, 0.5f, 0, 0, 0, 0.5f, 0.75f, 0.5f);
                    }
                }
            }
            return ActionResultType.SUCCESS;
        }

        //Filling logic
        if (player.getHeldItem(hand).getItem().equals(Items.SOUL_SAND) && !state.get(FILLED)) {
            world.setBlockState(pos, state.with(FILLED, true));

            ItemStack playerStack = player.getHeldItem(hand);
            playerStack.shrink(1);
            if (playerStack.getCount() == 0) playerStack = ItemStack.EMPTY;

            player.setHeldItem(hand, playerStack);

            if (!world.isRemote()) {
                WorldHelper.playSound(world, pos, 20, SoundEvents.BLOCK_SOUL_SAND_PLACE, SoundCategory.BLOCKS, 1, 1);
            }
            return ActionResultType.SUCCESS;
        }

        //Ritual logic
        if (player.getHeldItem(hand).getItem().equals(ConjuringForgery.CONJURING_SCEPTER.get()) || player.getHeldItem(hand).getItem().equals(ConjuringForgery.SUPERIOR_CONJURING_SCEPTER.get())) {
            if (runRitualChecks(world, pos)) return ActionResultType.SUCCESS;
        }

        //Focus placing logic
        if (!state.get(FILLED)) return ActionResultType.PASS;

        SoulFunnelTileEntity funnel = (SoulFunnelTileEntity) world.getTileEntity(pos);
        ItemStack funnelFocus = funnel.getItem();

        if (funnelFocus == null) {
            if (!player.getHeldItem(hand).getItem().equals(ConjuringForgery.CONJURING_FOCUS.get()) || !player.getHeldItem(hand).getOrCreateTag().getCompound("Entity").isEmpty())
                return ActionResultType.PASS;

            if (!world.isRemote()) {
                funnel.setItem(player.getHeldItem(hand).copy());
                player.setHeldItem(hand, ItemStack.EMPTY);
            }
        } else {
            if (!world.isRemote() && !funnel.isRitualRunning()) {
                if (player.getHeldItem(hand).equals(ItemStack.EMPTY)) {
                    player.setHeldItem(hand, funnelFocus);
                } else {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY() + 0.55d, pos.getZ(), funnelFocus);
                }
                funnel.setItem(null);
            }
        }

        return ActionResultType.SUCCESS;
    }

    private boolean runRitualChecks(World world, BlockPos pos) {
        SoulFunnelTileEntity blockEntity = (SoulFunnelTileEntity) world.getTileEntity(pos);
        if (blockEntity.getItem() == null) return false;

        if (world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos, pos.add(1, 3, 1))).isEmpty()) return false;
        Entity e = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos, pos.add(1, 3, 1))).get(0);
        if (!(e instanceof MobEntity) || e instanceof WitherEntity || e instanceof EnderDragonEntity) return false;

        if (!world.isRemote()) {
            blockEntity.startRitual(e.getUniqueID());
        }
        return true;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (state.get(FILLED)) InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.SOUL_SAND));

            TileEntity blockEntity = world.getTileEntity(pos);
            if (blockEntity instanceof SoulFunnelTileEntity) {
                SoulFunnelTileEntity funnel = (SoulFunnelTileEntity) blockEntity;

                funnel.onBroken();

            }
            super.onReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        SoulFunnelTileEntity funnel = (SoulFunnelTileEntity) world.getTileEntity(pos);

        for (BlockPos p : funnel.getPedestalPositions()) {
            if (random.nextDouble() > 0.5f) continue;
            BlackstonePedestalTileEntity pedestal = (BlackstonePedestalTileEntity) world.getTileEntity(p);
            if (pedestal == null) continue;
            if (pedestal.getLinkedFunnel() == null) continue;
            if (pedestal.getLinkedFunnel().compareTo(pos) != 0) return;

            WorldHelper.spawnEnchantParticle(world, p, pos.add(0, 1, 0), 0, 0.75f, 0, 0.35f);
        }
    }
}
