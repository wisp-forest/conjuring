package com.glisco.conjuring.client;

import com.glisco.conjuring.ConjuringCommon;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class ConjuringClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(ConjuringCommon.CONJURER_BLOCK_ENTITY, ConjurerBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ConjuringCommon.CONJURER_BLOCK, RenderLayer.getCutout());

        ScreenRegistry.register(ConjuringCommon.CONJURER_SCREEN_HANDLER_TYPE, ConjurerScreen::new);
    }

}
