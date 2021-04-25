package com.glisco.conjuring.mixin;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.blocks.soulfireForge.SoulfireForgeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {"net.minecraft.block.dispenser.DispenserBehavior$10"})
public class DispenserBehaviorMixin {

    @Inject(method = "dispenseSilently", at = @At("HEAD"), cancellable = true)
    public void checkFroge(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        Direction direction = pointer.getWorld().getBlockState(pointer.getBlockPos()).get(DispenserBlock.FACING);
        BlockState state = pointer.getWorld().getBlockState(pointer.getBlockPos().offset(direction));
        if (!state.getBlock().equals(ConjuringCommon.SOULFIRE_FORGE_BLOCK)) return;

        if (state.get(SoulfireForgeBlock.BURNING)) return;

        pointer.getWorld().setBlockState(pointer.getBlockPos().offset(direction), state.with(SoulfireForgeBlock.BURNING, true));
        stack.damage(1, pointer.getWorld().getRandom(), null);

        cir.setReturnValue(stack.getDamage() > stack.getMaxDamage() ? ItemStack.EMPTY : stack);

    }

}
