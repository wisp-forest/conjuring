package com.glisco.conjuringforgery.blocks.soulfireForge;

import com.glisco.conjuringforgery.SoulfireForgeContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

public class SoulfireForgeBlock extends Block {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty BURNING = BooleanProperty.create("burning");

    private static final VoxelShape BASE = Stream.of(
            Block.makeCuboidShape(0, 0, 0, 16, 3, 16),
            Block.makeCuboidShape(3, 3, 3, 13, 6, 13),
            Block.makeCuboidShape(2, 6, 2, 14, 9, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape N = Stream.of(
            Block.makeCuboidShape(3, 9, 1, 13, 11, 3),
            Block.makeCuboidShape(3, 9, 3, 13, 10, 13),
            Block.makeCuboidShape(12, 10, 7, 13, 16, 9),
            Block.makeCuboidShape(13, 9, 1, 15, 16, 13),
            Block.makeCuboidShape(7, 10, 12, 9, 16, 13),
            Block.makeCuboidShape(3, 10, 7, 4, 16, 9),
            Block.makeCuboidShape(1, 9, 13, 15, 16, 15),
            Block.makeCuboidShape(1, 9, 1, 3, 16, 13),
            Block.makeCuboidShape(2, 16, 4, 14, 19, 14),
            Block.makeCuboidShape(4, 19, 5, 12, 20, 11)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape S = Stream.of(
            Block.makeCuboidShape(3, 9, 13, 13, 11, 15),
            Block.makeCuboidShape(3, 9, 3, 13, 10, 13),
            Block.makeCuboidShape(3, 10, 7, 4, 16, 9),
            Block.makeCuboidShape(1, 9, 3, 3, 16, 15),
            Block.makeCuboidShape(7, 10, 3, 9, 16, 4),
            Block.makeCuboidShape(12, 10, 7, 13, 16, 9),
            Block.makeCuboidShape(1, 9, 1, 15, 16, 3),
            Block.makeCuboidShape(13, 9, 3, 15, 16, 15),
            Block.makeCuboidShape(2, 16, 2, 14, 19, 12),
            Block.makeCuboidShape(4, 19, 5, 12, 20, 11)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape W = Stream.of(
            Block.makeCuboidShape(1, 9, 3, 3, 11, 13),
            Block.makeCuboidShape(3, 9, 3, 13, 10, 13),
            Block.makeCuboidShape(7, 10, 3, 9, 16, 4),
            Block.makeCuboidShape(1, 9, 1, 13, 16, 3),
            Block.makeCuboidShape(12, 10, 7, 13, 16, 9),
            Block.makeCuboidShape(7, 10, 12, 9, 16, 13),
            Block.makeCuboidShape(13, 9, 1, 15, 16, 15),
            Block.makeCuboidShape(1, 9, 13, 13, 16, 15),
            Block.makeCuboidShape(4, 16, 2, 14, 19, 14),
            Block.makeCuboidShape(5, 19, 4, 11, 20, 12)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape E = Stream.of(
            Block.makeCuboidShape(13, 9, 3, 15, 11, 13),
            Block.makeCuboidShape(3, 9, 3, 13, 10, 13),
            Block.makeCuboidShape(7, 10, 12, 9, 16, 13),
            Block.makeCuboidShape(3, 9, 13, 15, 16, 15),
            Block.makeCuboidShape(3, 10, 7, 4, 16, 9),
            Block.makeCuboidShape(7, 10, 3, 9, 16, 4),
            Block.makeCuboidShape(1, 9, 1, 3, 16, 15),
            Block.makeCuboidShape(3, 9, 1, 15, 16, 3),
            Block.makeCuboidShape(2, 16, 2, 12, 19, 14),
            Block.makeCuboidShape(5, 19, 4, 11, 20, 12)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_N = VoxelShapes.or(BASE, N);
    private static final VoxelShape SHAPE_S = VoxelShapes.or(BASE, S);
    private static final VoxelShape SHAPE_W = VoxelShapes.or(BASE, W);
    private static final VoxelShape SHAPE_E = VoxelShapes.or(BASE, E);

    //Construction stuff
    public SoulfireForgeBlock() {
        super(Properties.create(Material.ROCK)
                .setRequiresTool()
                .hardnessAndResistance(5.0F, 1200.0F)
                .sound(SoundType.STONE)
                .notSolid()
                .setOpaque((p_test_1_, p_test_2_, p_test_3_) -> false)
                .setLightLevel(getLightLevel()));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SoulfireForgeTileEntity();
    }

    //BlockState shit

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
            case EAST:
                return SHAPE_E;
            case WEST:
                return SHAPE_W;
            case SOUTH:
                return SHAPE_S;
            default:
                return SHAPE_N;
        }
    }

    private static ToIntFunction<BlockState> getLightLevel() {
        return (blockState) -> blockState.get(BURNING) ? 13 : 0;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()).with(BURNING, false);
    }


    //Actual Logic
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {

        if (player.getHeldItem(hand).getItem().equals(Items.FLINT_AND_STEEL) && !world.getBlockState(pos).get(BURNING)) {
            if (!world.isRemote()) {
                world.setBlockState(pos, world.getBlockState(pos).with(SoulfireForgeBlock.BURNING, true));
                player.getHeldItem(hand).damageItem(1, player, (Consumer<LivingEntity>) ((p) -> {
                    p.sendBreakAnimation(hand);
                }));
            }
            return ActionResultType.SUCCESS;
        }

        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (!(tile instanceof SoulfireForgeTileEntity)) throw new IllegalStateException("TileEntity missing!");
            INamedContainerProvider containerProvider = new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return new TranslationTextComponent("conjuring.gui.soulfire_forge");
                }

                @Override
                public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity player) {
                    return new SoulfireForgeContainer(windowID, playerInventory, world, pos);
                }
            };
            NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, pos);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        if (!(world.getTileEntity(pos) instanceof SoulfireForgeTileEntity)) return 0;
        return Math.round(((SoulfireForgeTileEntity) world.getTileEntity(pos)).getProgress() * 0.46875f);
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity blockEntity = world.getTileEntity(pos);
            if (blockEntity instanceof SoulfireForgeTileEntity) {
                InventoryHelper.dropInventoryItems(world, pos, ((SoulfireForgeTileEntity) blockEntity).getInventory());
            }
            super.onReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BURNING);
        builder.add(FACING);
    }
}
