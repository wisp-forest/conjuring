package com.glisco.conjuring.client;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.entities.EntityCreatePacket;
import com.glisco.conjuring.entities.SoulEntityRenderer;
import com.glisco.conjuring.items.soul_alloy_tools.ChangeToolModePacket;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyToolAbilities;
import com.glisco.conjuring.mixin.WorldRendererInvoker;
import com.glisco.owo.particles.ClientParticles;
import com.glisco.owo.particles.ServerParticles;
import com.glisco.owo.util.VectorSerializer;
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
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ConjuringClient implements ClientModInitializer {

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

        EntityRendererRegistry.INSTANCE.register(ConjuringCommon.SOUL_PROJECTILE, SoulEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(ConjuringCommon.SOUL_DIGGER, SoulEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(ConjuringCommon.SOUL_FELLER, SoulEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(ConjuringCommon.SOUL_MAGNET, SoulEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(EntityCreatePacket.ID, EntityCreatePacket::onPacket);

        FabricModelPredicateProviderRegistry.register(ConjuringCommon.CONJURING_FOCUS, new Identifier("has_soul"), (stack, world, entity, seed) -> stack.getOrCreateTag().contains("Entity") ? 1f : 0f);
        FabricModelPredicateProviderRegistry.register(ConjuringCommon.STABILIZED_CONJURING_FOCUS, new Identifier("has_soul"), (stack, world, entity, seed) -> stack.getOrCreateTag().contains("Entity") ? 1f : 0f);
        FabricModelPredicateProviderRegistry.register(ConjuringCommon.ENCHIRIDION, new Identifier("is_sandwich"), (stack, world, entity, seed) -> stack.getOrCreateTag().getBoolean("Sandwich") ? 1f : 0f);
        FabricModelPredicateProviderRegistry.register(ConjuringCommon.PIZZA, new Identifier("is_brinsa"), (stack, world, entity, seed) -> stack.getOrCreateTag().contains("Brinsa") ? 1f : 0f);

        ScreenRegistry.register(ConjuringCommon.CONJURER_SCREEN_HANDLER_TYPE, ConjurerScreen::new);
        ScreenRegistry.register(ConjuringCommon.SOULFIRE_FORGE_SCREEN_HANDLER_TYPE, SoulfireForgeScreen::new);

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

            NbtCompound ntb = data.readNbt();
            Vec3d start = VectorSerializer.get(ntb, "start");
            Vec3d end = VectorSerializer.get(ntb, "end");

            client.execute(() -> {
                ClientParticles.setParticleCount(15);
                ClientParticles.spawnLine(ParticleTypes.ENCHANTED_HIT, client.world, start, end, 0.1f);
            });

        });

        WorldRenderEvents.BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {
            final MinecraftClient client = MinecraftClient.getInstance();
            if (!SoulAlloyToolAbilities.canAoeDig(client.player)) {
                return true;
            }

            BlockState blockState;
            for (BlockPos pos : SoulAlloyToolAbilities.getBlocksToDig(client.player)) {
                blockState = client.world.getBlockState(pos);
                if (!blockState.isAir()) {
                    WorldRendererInvoker.invokeDrawShapeOutline(worldRenderContext.matrixStack(), blockOutlineContext.vertexConsumer(), blockState.getOutlineShape(client.world, pos, ShapeContext.of(blockOutlineContext.entity())), (double) pos.getX() - blockOutlineContext.cameraX(), (double) pos.getY() - blockOutlineContext.cameraY(), (double) pos.getZ() - blockOutlineContext.cameraZ(), 0.0F, 0.0F, 0.0F, 0.4F);
                }
            }
            return true;
        });

    }

}
