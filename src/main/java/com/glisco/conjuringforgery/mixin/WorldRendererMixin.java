package com.glisco.conjuringforgery.mixin;

import com.glisco.conjuringforgery.items.soul_alloy_tools.SoulAlloyToolAbilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow
    private static void drawShape(MatrixStack matrixStackIn, IVertexBuilder bufferIn, VoxelShape shapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
    }

    @Inject(method = "drawSelectionBox", at = @At("TAIL"))
    public void drawOtherOutlines(MatrixStack matrixStack, IVertexBuilder vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {

        final Minecraft client = Minecraft.getInstance();
        if (!SoulAlloyToolAbilities.canAoeDig(client.player)) return;

        for (BlockPos pos : SoulAlloyToolAbilities.getBlocksToDig(client.player)) {
            blockState = client.world.getBlockState(pos);
            if (!blockState.isAir()) {
                drawShape(matrixStack, vertexConsumer, blockState.getShape(client.world, pos, ISelectionContext.forEntity(entity)), (double) pos.getX() - d, (double) pos.getY() - e, (double) pos.getZ() - f, 0.0F, 0.0F, 0.0F, 0.4F);
            }
        }
    }

}
