package com.glisco.conjuring.client;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.entities.EntityCreatePacket;
import com.glisco.conjuring.entities.SoulEntityRenderer;
import com.glisco.conjuring.items.soul_alloy_tools.ChangeToolModePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ConjuringClient implements ClientModInitializer {

    public static final SoundEvent FROGE_SOUND = new SoundEvent(new Identifier("conjuring", "gui.gem_tinkerer.froge"));

    public static KeyBinding TOGGLE_TOOL_MODE;

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(ConjuringCommon.CONJURER_BLOCK_ENTITY, ConjurerBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ConjuringCommon.BLACKSTONE_PEDESTAL_BLOCK_ENTITY, BlackstonePedestalBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ConjuringCommon.SOUL_FUNNEL_BLOCK_ENTITY, SoulFunnelBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ConjuringCommon.SOUL_WEAVER_BLOCK_ENTITY, SoulWeaverBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ConjuringCommon.GEM_TINKERER_BLOCK_ENTITY, GemTinkererBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ConjuringCommon.CONJURER_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ConjuringCommon.SOULFIRE_FORGE_BLOCK, RenderLayer.getCutout());

        EntityRendererRegistry.INSTANCE.register(ConjuringCommon.SOUL_PROJECTILE, (dispatcher, context) -> new SoulEntityRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(ConjuringCommon.SOUL_DIGGER, (dispatcher, context) -> new SoulEntityRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(ConjuringCommon.SOUL_FELLER, (dispatcher, context) -> new SoulEntityRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(ConjuringCommon.SOUL_MAGNET, (dispatcher, context) -> new SoulEntityRenderer(dispatcher));

        ClientPlayNetworking.registerGlobalReceiver(EntityCreatePacket.ID, EntityCreatePacket::onPacket);

        FabricModelPredicateProviderRegistry.register(ConjuringCommon.CONJURING_FOCUS, new Identifier("has_soul"), (stack, world, entity) -> stack.getOrCreateTag().contains("Entity") ? 1f : 0f);
        FabricModelPredicateProviderRegistry.register(ConjuringCommon.STABILIZED_CONJURING_FOCUS, new Identifier("has_soul"), (stack, world, entity) -> stack.getOrCreateTag().contains("Entity") ? 1f : 0f);

        ScreenRegistry.register(ConjuringCommon.CONJURER_SCREEN_HANDLER_TYPE, ConjurerScreen::new);
        ScreenRegistry.register(ConjuringCommon.SOULFIRE_FORGE_SCREEN_HANDLER_TYPE, SoulfireForgeScreen::new);

        Registry.register(Registry.SOUND_EVENT, new Identifier("conjuring", "gui.gem_tinkerer.froge"), FROGE_SOUND);

        TOGGLE_TOOL_MODE = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.conjuring.toggle_tool_mode", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "category.conjuring"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_TOOL_MODE.wasPressed()) {
                client.getNetworkHandler().sendPacket(ChangeToolModePacket.create());
            }
        });

    }

}
