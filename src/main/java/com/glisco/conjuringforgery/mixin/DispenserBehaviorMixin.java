package com.glisco.conjuringforgery.mixin;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.dispenser.IDispenseItemBehavior$18")
public class DispenserBehaviorMixin {

    @Inject(method = "dispenseStack(Lnet/minecraft/dispenser/IBlockSource;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void checkFroge(IBlockSource pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        Direction direction = pointer.getWorld().getBlockState(pointer.getBlockPos()).get(DispenserBlock.FACING);
        BlockState state = pointer.getWorld().getBlockState(pointer.getBlockPos().offset(direction));
        if (!state.getBlock().equals(ConjuringForgery.SOULFIRE_FORGE.get())) return;

        if (state.get(SoulfireForgeBlock.BURNING)) return;

        pointer.getWorld().setBlockState(pointer.getBlockPos().offset(direction), state.with(SoulfireForgeBlock.BURNING, true));
        stack.attemptDamageItem(1, pointer.getWorld().getRandom(), null);

        cir.setReturnValue(stack.getDamage() > stack.getMaxDamage() ? ItemStack.EMPTY : stack);

    }

}
