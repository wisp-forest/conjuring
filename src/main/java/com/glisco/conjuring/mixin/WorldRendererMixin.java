package com.glisco.conjuring.mixin;

import com.glisco.conjuring.items.SoulAlloyToolAbilities;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow
    private static void drawShapeOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
    }

    @Inject(method = "drawBlockOutline", at = @At("TAIL"))
    public void drawOtherOutlines(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {

        final MinecraftClient client = MinecraftClient.getInstance();
        if (!SoulAlloyToolAbilities.canAoeDig(client.player)) return;

        for (BlockPos pos : SoulAlloyToolAbilities.getBlocksToDig(client.player)) {
            blockState = client.world.getBlockState(pos);
            if (!blockState.isAir()) {
                drawShapeOutline(matrixStack, vertexConsumer, blockState.getOutlineShape(client.world, pos, ShapeContext.of(entity)), (double) pos.getX() - d, (double) pos.getY() - e, (double) pos.getZ() - f, 0.0F, 0.0F, 0.0F, 0.4F);
            }
        }


    }

}
