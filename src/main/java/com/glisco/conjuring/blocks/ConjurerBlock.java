package com.glisco.conjuring.blocks;

import com.glisco.conjuring.items.ConjuringRod;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ConjurerBlock extends BlockWithEntity {

    public ConjurerBlock(Settings settings) {
        super(settings);
    }

    public ConjurerBlock() {
        this(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque());
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new ConjurerBlockEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!(player.getMainHandStack().getItem() instanceof ConjuringRod)) return ActionResult.PASS;

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
            if (blockEntity instanceof ConjurerBlockEntity) {
                ItemScatterer.spawn(world, pos, (ConjurerBlockEntity) blockEntity);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
