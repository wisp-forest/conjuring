package com.glisco.conjuring.client;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.entities.EntityCreatePacket;
import com.glisco.conjuring.entities.SoulProjectileEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ConjuringClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(ConjuringCommon.CONJURER_BLOCK_ENTITY, ConjurerBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ConjuringCommon.BLACKSTONE_PEDSTAL_BLOCK_ENTITY, BlackstonePedestalBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ConjuringCommon.SOUL_FUNNEL_BLOCK_ENTITY, SoulFunnelBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ConjuringCommon.CONJURER_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ConjuringCommon.SOULFIRE_FORGE_BLOCK, RenderLayer.getCutout());

        EntityRendererRegistry.INSTANCE.register(ConjuringCommon.SOUL_PROJECTILE, (dispatcher, context) -> {
            return new SoulProjectileEntityRenderer(dispatcher);
        });
        ClientSidePacketRegistry.INSTANCE.register(EntityCreatePacket.ID, EntityCreatePacket::onPacket);

        FabricModelPredicateProviderRegistry.register(ConjuringCommon.CONJURING_FOCUS, new Identifier("has_soul"), (stack, world, entity) -> stack.getOrCreateTag().contains("Entity") ? 1f : 0f);

        ScreenRegistry.register(ConjuringCommon.CONJURER_SCREEN_HANDLER_TYPE, ConjurerScreen::new);
        ScreenRegistry.register(ConjuringCommon.SOULFIRE_FORGE_SCREEN_HANDLER_TYPE, SoulfireForgeScreen::new);
    }

}
