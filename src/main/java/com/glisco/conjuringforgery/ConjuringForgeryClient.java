package com.glisco.conjuringforgery;

import com.glisco.conjuringforgery.client.*;
import com.glisco.conjuringforgery.entities.SoulEntityRenderer;
import com.glisco.owo.ServerParticles;
import com.glisco.owo.VectorSerializer;
import com.glisco.owo.client.ClientParticles;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import static com.glisco.conjuringforgery.ConjuringForgery.*;

public class ConjuringForgeryClient {

    public static final KeyBinding TOGGLE_TOOL_MODE_BIND = new KeyBinding("key.conjuring.toggle_tool_mode", GLFW.GLFW_KEY_LEFT_ALT, "category.conjuring");

    @OnlyIn(Dist.CLIENT)
    public static void clientInit(final FMLClientSetupEvent event){
        ScreenManager.registerFactory(CONJURER_CONTAINER_TYPE.get(), ConjurerScreen::new);
        ScreenManager.registerFactory(SOULFIRE_FORGE_CONTAINER_TYPE.get(), SoulfireForgeScreen::new);

        RenderTypeLookup.setRenderLayer(CONJURER.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(SOULFIRE_FORGE.get(), RenderType.getCutout());

        ItemModelsProperties.registerProperty(CONJURING_FOCUS.get(), new ResourceLocation("has_soul"), (stack, world, entity) -> stack.getOrCreateTag().contains("Entity") ? 1.0f : 0f);
        ItemModelsProperties.registerProperty(STABILIZED_FOCUS.get(), new ResourceLocation("has_soul"), (stack, world, entity) -> stack.getOrCreateTag().contains("Entity") ? 1.0f : 0f);
        ItemModelsProperties.registerProperty(PIZZA.get(), new ResourceLocation("is_brinsa"), (stack, world, entity) -> stack.getOrCreateTag().getBoolean("Brinsa") ? 1f : 0f);
        ItemModelsProperties.registerProperty(ENCHIRIDION.get(), new ResourceLocation("is_sandwich"), (stack, world, entity) -> stack.getOrCreateTag().getBoolean("Sandwich") ? 1f : 0f);

        ClientRegistry.bindTileEntityRenderer(CONJURER_TILE.get(), ConjurerTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BLACKSTONE_PEDESTAL_TILE.get(), BlackstonePedestalTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(SOUL_FUNNEL_TILE.get(), SoulFunnelTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(SOUL_WEAVER_TILE.get(), SoulWeaverTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(GEM_TINKERER_TILE.get(), GemTinkererBlockEntityRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(SOUL_PROJECTILE.get(), SoulEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SOUL_DIGGER.get(), SoulEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SOUL_FELLER.get(), SoulEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SOUL_MAGNET.get(), SoulEntityRenderer::new);

        ServerParticles.registerClientSideHandler(new ResourceLocation("conjuring", "break_block"), (minecraft, blockPos, packetBuffer) -> {
            minecraft.execute(() -> {
                ClientParticles.setParticleCount(3);
                ClientParticles.spawnCubeOutline(ParticleTypes.SOUL_FIRE_FLAME, minecraft.world, Vector3d.copy(blockPos).add(0.175, 0.175, 0.175), 0.65f, 0f);
            });
        });

        ServerParticles.registerClientSideHandler(new ResourceLocation("conjuring", "unlink_weaver"), (client, pos, buffer) -> {

            BlockPos pedestal = buffer.readBlockPos();

            client.execute(() -> {
                ClientParticles.setParticleCount(20);
                ClientParticles.spawnLine(ParticleTypes.SMOKE, client.world, Vector3d.copy(pos).add(0.5, 0.4, 0.5), Vector3d.copy(pedestal).add(0.5, 0.5, 0.5), 0);

                ClientParticles.setParticleCount(30);
                ClientParticles.spawnWithinBlock(ParticleTypes.SMOKE, client.world, pedestal);
            });
        });

        ServerParticles.registerClientSideHandler(new ResourceLocation("conjuring", "line"), (client, pos, data) -> {

            JsonObject object = ServerParticles.NETWORK_GSON.fromJson(data.readString(), JsonObject.class);
            Vector3d start = VectorSerializer.fromJson(object, "start");
            Vector3d end = VectorSerializer.fromJson(object, "end");

            client.execute(() -> {
                ClientParticles.setParticleCount(15);
                ClientParticles.spawnLine(ParticleTypes.ENCHANTED_HIT, client.world, start, end, 0.1f);
            });

        });

        ClientRegistry.registerKeyBinding(TOGGLE_TOOL_MODE_BIND.getKeyBinding());
    }

}
