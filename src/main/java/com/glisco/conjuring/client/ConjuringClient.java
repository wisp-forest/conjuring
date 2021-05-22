package com.glisco.conjuring.client;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.entities.EntityCreatePacket;
import com.glisco.conjuring.entities.SoulEntityRenderer;
import com.glisco.conjuring.items.soul_alloy_tools.ChangeToolModePacket;
import com.glisco.owo.ServerParticles;
import com.glisco.owo.VectorSerializer;
import com.glisco.owo.client.ClientParticles;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
        FabricModelPredicateProviderRegistry.register(ConjuringCommon.ENCHIRIDION, new Identifier("is_sandwich"), (stack, world, entity) -> stack.getOrCreateTag().getBoolean("Sandwich") ? 1f : 0f);
        FabricModelPredicateProviderRegistry.register(ConjuringCommon.PIZZA, new Identifier("is_brinsa"), (stack, world, entity) -> stack.getOrCreateTag().contains("Brinsa") ? 1f : 0f);

        ScreenRegistry.register(ConjuringCommon.CONJURER_SCREEN_HANDLER_TYPE, ConjurerScreen::new);
        ScreenRegistry.register(ConjuringCommon.SOULFIRE_FORGE_SCREEN_HANDLER_TYPE, SoulfireForgeScreen::new);

        Registry.register(Registry.SOUND_EVENT, new Identifier("conjuring", "gui.gem_tinkerer.froge"), FROGE_SOUND);

        TOGGLE_TOOL_MODE = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.conjuring.toggle_tool_mode", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "category.conjuring"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_TOOL_MODE.wasPressed()) {
                client.getNetworkHandler().sendPacket(ChangeToolModePacket.create());
            }
        });

        ServerParticles.registerClientSideHandler(new Identifier("conjuring", "unlink_weaver"), (client, pos, buffer) -> {

            BlockPos pedestal = buffer.readBlockPos();

            client.execute(() -> {
                ClientParticles.setParticleCount(20);
                ClientParticles.spawnLine(ParticleTypes.SMOKE, client.world, Vec3d.of(pos).add(0.5, 0.4, 0.5), Vec3d.of(pedestal).add(0.5, 0.5, 0.5), 0);

                ClientParticles.setParticleCount(30);
                ClientParticles.spawnWithinBlock(ParticleTypes.SMOKE, client.world, pedestal);
            });
        });

        ServerParticles.registerClientSideHandler(new Identifier("conjuring", "break_block"), (client, pos, data) -> {
            client.execute(() -> {
                ClientParticles.setParticleCount(3);
                ClientParticles.spawnCubeOutline(ParticleTypes.SOUL_FIRE_FLAME, client.world, Vec3d.of(pos).add(0.175, 0.175, 0.175), 0.65f, 0f);
            });
        });

        ServerParticles.registerClientSideHandler(new Identifier("conjuring", "line"), (client, pos, data) -> {

            JsonObject object = new Gson().fromJson(data.readString(), JsonObject.class);
            Vec3d start = VectorSerializer.fromJson(object, "start");
            Vec3d end = VectorSerializer.fromJson(object, "end");

            client.execute(() -> {
                ClientParticles.setParticleCount(15);
                ClientParticles.spawnLine(ParticleTypes.ENCHANTED_HIT, client.world, start, end, 0.1f);
            });

        });

    }

}
