package com.glisco.conjuring.mixin;

import com.glisco.conjuring.blocks.soulfireForge.SoulfireForgeBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(FlintAndSteelItem.class)
public class FlintAndSteelMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    public void onBlockUse(ItemUsageContext context, CallbackInfoReturnable<ActionResult> callback) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();

        if (!(world.getBlockState(pos).getBlock() instanceof SoulfireForgeBlock)) return;
        if (world.getBlockState(pos).get(SoulfireForgeBlock.BURNING)) return;

        world.setBlockState(pos, world.getBlockState(pos).with(SoulfireForgeBlock.BURNING, true));
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1f, 1f, true);

        context.getStack().damage(1, context.getPlayer(), (Consumer<LivingEntity>) ((p) -> {
            p.sendToolBreakStatus(context.getHand());
        }));

        callback.setReturnValue(ActionResult.SUCCESS);
        callback.cancel();

    }
}
