package com.glisco.conjuring.entities;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.util.Identifier;

public class SoulProjectileEntityRenderer extends EntityRenderer<SoulProjectile> {


    public SoulProjectileEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public Identifier getTexture(SoulProjectile entity) {
        return new Identifier("");
    }
}
