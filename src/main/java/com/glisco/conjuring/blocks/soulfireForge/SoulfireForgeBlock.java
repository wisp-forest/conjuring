package com.glisco.conjuring.blocks.soulfireForge;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public class SoulfireForgeBlock extends BlockWithEntity {

    public static final EnumProperty<Direction.Axis> AXIS;
    public static final BooleanProperty BURNING = BooleanProperty.of("burning");

    private static VoxelShape BASE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
    private static VoxelShape PLATE = Block.createCuboidShape(2, 10, 2, 14, 11, 14);
    private static VoxelShape SOUL_SOIL = Block.createCuboidShape(2, 1, 2, 14, 3, 14);

    private static VoxelShape PILLAR_1_X = Block.createCuboidShape(2, 1, 0, 5, 13, 2);
    private static VoxelShape PILLAR_2_X = Block.createCuboidShape(11, 1, 0, 14, 13, 2);
    private static VoxelShape PILLAR_3_X = Block.createCuboidShape(2, 1, 14, 5, 13, 16);
    private static VoxelShape PILLAR_4_X = Block.createCuboidShape(11, 1, 14, 14, 13, 16);
    private static VoxelShape PILLAR_1_Z = Block.createCuboidShape(14, 1, 2, 16, 13, 5);
    private static VoxelShape PILLAR_2_Z = Block.createCuboidShape(14, 1, 11, 16, 13, 14);
    private static VoxelShape PILLAR_3_Z = Block.createCuboidShape(0, 1, 2, 2, 13, 5);
    private static VoxelShape PILLAR_4_Z = Block.createCuboidShape(0, 1, 11, 2, 13, 14);

    private static VoxelShape SUPPORT_1_X = Block.createCuboidShape(2, 9, 2, 5, 10, 4);
    private static VoxelShape SUPPORT_2_X = Block.createCuboidShape(11, 9, 2, 14, 10, 4);
    private static VoxelShape SUPPORT_3_X = Block.createCuboidShape(2, 9, 12, 5, 10, 14);
    private static VoxelShape SUPPORT_4_X = Block.createCuboidShape(11, 9, 12, 14, 10, 14);
    private static VoxelShape SUPPORT_1_Z = Block.createCuboidShape(12, 9, 2, 14, 10, 5);
    private static VoxelShape SUPPORT_2_Z = Block.createCuboidShape(12, 9, 11, 14, 10, 14);
    private static VoxelShape SUPPORT_3_Z = Block.createCuboidShape(2, 9, 2, 4, 10, 5);
    private static VoxelShape SUPPORT_4_Z = Block.createCuboidShape(2, 9, 11, 4, 10, 14);

    private static VoxelShape SHAPE_X;
    private static VoxelShape SHAPE_Z;

    static {
        AXIS = Properties.HORIZONTAL_AXIS;
        SHAPE_X = VoxelShapes.union(BASE, SOUL_SOIL, PLATE, PILLAR_1_X, PILLAR_2_X, PILLAR_3_X, PILLAR_4_X, SUPPORT_1_X, SUPPORT_2_X, SUPPORT_3_X, SUPPORT_4_X);
        SHAPE_Z = VoxelShapes.union(BASE, SOUL_SOIL, PLATE, PILLAR_1_Z, PILLAR_2_Z, PILLAR_3_Z, PILLAR_4_Z, SUPPORT_1_Z, SUPPORT_2_Z, SUPPORT_3_Z, SUPPORT_4_Z);
    }

    //Construction stuff
    public SoulfireForgeBlock() {
        super(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(5.0F, 1200.0F).sounds(BlockSoundGroup.STONE).nonOpaque().luminance(getLightLevel()));
        setDefaultState(getStateManager().getDefaultState().with(BURNING, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new SoulfireForgeBlockEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    //BlockState shit
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(AXIS).asString().equals("x")) {
            return SHAPE_X;
        } else {
            return SHAPE_Z;
        }
    }

    private static ToIntFunction<BlockState> getLightLevel() {
        return (blockState) -> blockState.get(BURNING) ? 13 : 0;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (state.get(AXIS)) {
                    case X:
                        return state.with(AXIS, Direction.Axis.Z);
                    case Z:
                        return state.with(AXIS, Direction.Axis.X);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(AXIS, ctx.getPlayerFacing().getAxis());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BURNING);
        builder.add(AXIS);
    }

    //Actual Logic
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (player.getStackInHand(hand).getItem().equals(Items.FLINT_AND_STEEL) && !world.getBlockState(pos).get(BURNING)) {
            if (!world.isClient()) {
                world.setBlockState(pos, world.getBlockState(pos).with(SoulfireForgeBlock.BURNING, true));
                player.getStackInHand(hand).damage(1, player, (Consumer<LivingEntity>) ((p) -> {
                    p.sendToolBreakStatus(hand);
                }));
            }
            return ActionResult.SUCCESS;
        }

        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }

        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SoulfireForgeBlockEntity) {
                ItemScatterer.spawn(world, pos, (SoulfireForgeBlockEntity) blockEntity);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

}
