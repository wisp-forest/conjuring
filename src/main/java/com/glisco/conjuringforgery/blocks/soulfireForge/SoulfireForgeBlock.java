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

public class SoulfireForgeBlock extends Block {

    public static final EnumProperty<Direction.Axis> AXIS;
    public static final BooleanProperty BURNING = BooleanProperty.create("burning");

    private static VoxelShape BASE = Block.makeCuboidShape(0, 0, 0, 16, 1, 16);
    private static VoxelShape PLATE = Block.makeCuboidShape(2, 10, 2, 14, 11, 14);
    private static VoxelShape SOUL_SOIL = Block.makeCuboidShape(2, 1, 2, 14, 3, 14);

    private static VoxelShape PILLAR_1_X = Block.makeCuboidShape(2, 1, 0, 5, 13, 2);
    private static VoxelShape PILLAR_2_X = Block.makeCuboidShape(11, 1, 0, 14, 13, 2);
    private static VoxelShape PILLAR_3_X = Block.makeCuboidShape(2, 1, 14, 5, 13, 16);
    private static VoxelShape PILLAR_4_X = Block.makeCuboidShape(11, 1, 14, 14, 13, 16);
    private static VoxelShape PILLAR_1_Z = Block.makeCuboidShape(14, 1, 2, 16, 13, 5);
    private static VoxelShape PILLAR_2_Z = Block.makeCuboidShape(14, 1, 11, 16, 13, 14);
    private static VoxelShape PILLAR_3_Z = Block.makeCuboidShape(0, 1, 2, 2, 13, 5);
    private static VoxelShape PILLAR_4_Z = Block.makeCuboidShape(0, 1, 11, 2, 13, 14);

    private static VoxelShape SUPPORT_1_X = Block.makeCuboidShape(2, 9, 2, 5, 10, 4);
    private static VoxelShape SUPPORT_2_X = Block.makeCuboidShape(11, 9, 2, 14, 10, 4);
    private static VoxelShape SUPPORT_3_X = Block.makeCuboidShape(2, 9, 12, 5, 10, 14);
    private static VoxelShape SUPPORT_4_X = Block.makeCuboidShape(11, 9, 12, 14, 10, 14);
    private static VoxelShape SUPPORT_1_Z = Block.makeCuboidShape(12, 9, 2, 14, 10, 5);
    private static VoxelShape SUPPORT_2_Z = Block.makeCuboidShape(12, 9, 11, 14, 10, 14);
    private static VoxelShape SUPPORT_3_Z = Block.makeCuboidShape(2, 9, 2, 4, 10, 5);
    private static VoxelShape SUPPORT_4_Z = Block.makeCuboidShape(2, 9, 11, 4, 10, 14);

    private static VoxelShape SHAPE_X;
    private static VoxelShape SHAPE_Z;

    static {
        AXIS = BlockStateProperties.HORIZONTAL_AXIS;
        SHAPE_X = VoxelShapes.or(BASE, SOUL_SOIL, PLATE, PILLAR_1_X, PILLAR_2_X, PILLAR_3_X, PILLAR_4_X, SUPPORT_1_X, SUPPORT_2_X, SUPPORT_3_X, SUPPORT_4_X);
        SHAPE_Z = VoxelShapes.or(BASE, SOUL_SOIL, PLATE, PILLAR_1_Z, PILLAR_2_Z, PILLAR_3_Z, PILLAR_4_Z, SUPPORT_1_Z, SUPPORT_2_Z, SUPPORT_3_Z, SUPPORT_4_Z);
    }

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
        if (state.get(AXIS).getString().equals("x")) {
            return SHAPE_X;
        } else {
            return SHAPE_Z;
        }
    }

    private static ToIntFunction<BlockState> getLightLevel() {
        return (blockState) -> blockState.get(BURNING) ? 13 : 0;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        switch (rot) {
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(AXIS, context.getPlacementHorizontalFacing().getAxis()).with(BURNING, false);
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
        builder.add(AXIS);
    }
}
