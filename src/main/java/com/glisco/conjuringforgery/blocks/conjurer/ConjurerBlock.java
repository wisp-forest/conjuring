package com.glisco.conjuringforgery.blocks.conjurer;

import com.glisco.conjuringforgery.ConjurerContainer;
import com.glisco.conjuringforgery.items.ConjuringScepter;
import com.glisco.conjuringforgery.items.SuperiorConjuringScepter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;

public class ConjurerBlock extends Block {

    public ConjurerBlock(Properties properties) {
        super(properties);
    }

    public ConjurerBlock() {
        this(Properties.create(Material.IRON)
                .setRequiresTool()
                .hardnessAndResistance(5.0F)
                .sound(SoundType.METAL)
                .notSolid()
                .setOpaque((p_test_1_, p_test_2_, p_test_3_) -> false)
                .setBlocksVision((p_test_1_, p_test_2_, p_test_3_) -> false)
                .harvestTool(ToolType.PICKAXE));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ConjurerTileEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {

        if (!(player.getHeldItemMainhand().getItem() instanceof ConjuringScepter) && !(player.getHeldItemMainhand().getItem() instanceof SuperiorConjuringScepter))
            return ActionResultType.PASS;

        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (!(tile instanceof ConjurerTileEntity)) throw new IllegalStateException("TileEntity missing!");
            INamedContainerProvider containerProvider = new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return new StringTextComponent("");
                }

                @Override
                public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity player) {
                    return new ConjurerContainer(windowID, playerInventory, world, pos);
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
            if (blockEntity instanceof ConjurerTileEntity) {

                ItemStackHandler inv = ((ConjurerTileEntity) blockEntity).getInventory();
                for (int i = 0; i < inv.getSlots(); i++) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inv.getStackInSlot(i));
                }

            }
            super.onReplaced(state, world, pos, newState, moved);
        }
    }
}
