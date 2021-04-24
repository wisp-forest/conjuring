package com.glisco.conjuring.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {

    @Accessor("factories")
    <T extends ParticleEffect>
    Int2ObjectMap<ParticleFactory<T>> getFactories();

}
