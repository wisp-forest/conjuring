package com.glisco.conjuring.blocks;

import com.glisco.conjuring.WorldHelper;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SoulWeaverBlock extends BlockWithEntity {

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
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        for (int i = 0; i < 2; i++) {
            WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 0.35f, 0.5f, 0, 0, 0, 0.15f);
        }

        WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 0.35f, 0.5f, 0.1f, 0.1f, 0, 0.15f);
        WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 0.35f, 0.5f, -0.1f, 0.1f, 0, 0.15f);
        WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 0.35f, 0.5f, 0, 0.1f, 0.1f, 0.15f);
        WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 0.35f, 0.5f, 0, 0.1f, -0.1f, 0.15f);
    }
}
