package com.glisco.conjuring.mixin;

import com.glisco.conjuring.items.BlockCrawler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickTreeCrawler(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        BlockCrawler.tick((World) (Object) this);
    }

}
