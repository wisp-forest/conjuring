package com.glisco.conjuring.client;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.blocks.ConjuringBlocks;
import com.glisco.conjuring.client.ber.*;
import com.glisco.conjuring.client.ui.ConjurerScreen;
import com.glisco.conjuring.client.ui.SoulfireForgeScreen;
import com.glisco.conjuring.entities.EntityCreatePacket;
import com.glisco.conjuring.entities.SoulEntityRenderer;
import com.glisco.conjuring.items.ConjuringItems;
import com.glisco.conjuring.items.soul_alloy_tools.ChangeToolModePacket;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyTool;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyToolAbilities;
import com.glisco.conjuring.mixin.WorldRendererInvoker;
import com.glisco.conjuring.util.ConjuringParticleEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ConjuringClient implements ClientModInitializer {

    public static KeyBinding TOGGLE_TOOL_MODE;

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ConjuringBlocks.Entities.CONJURER, ConjurerBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ConjuringBlocks.Entities.BLACKSTONE_PEDESTAL, BlackstonePedestalBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ConjuringBlocks.Entities.SOUL_FUNNEL, SoulFunnelBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ConjuringBlocks.Entities.SOUL_WEAVER, SoulWeaverBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ConjuringBlocks.Entities.GEM_TINKERER, GemTinkererBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ConjuringBlocks.CONJURER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ConjuringBlocks.SOULFIRE_FORGE, RenderLayer.getCutout());

        EntityRendererRegistry.register(Conjuring.SOUL_PROJECTILE, SoulEntityRenderer::new);
        EntityRendererRegistry.register(Conjuring.SOUL_DIGGER, SoulEntityRenderer::new);
        EntityRendererRegistry.register(Conjuring.SOUL_FELLER, SoulEntityRenderer::new);
        EntityRendererRegistry.register(Conjuring.SOUL_MAGNET, SoulEntityRenderer::new);
        EntityRendererRegistry.register(Conjuring.SOUL_HARVESTER, SoulEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(EntityCreatePacket.ID, EntityCreatePacket::onPacket);

        FabricModelPredicateProviderRegistry.register(ConjuringItems.CONJURING_FOCUS, new Identifier("has_soul"), (stack, world, entity, seed) -> stack.getOrCreateNbt().contains("Entity") ? 1f : 0f);
        FabricModelPredicateProviderRegistry.register(ConjuringItems.STABILIZED_CONJURING_FOCUS, new Identifier("has_soul"), (stack, world, entity, seed) -> stack.getOrCreateNbt().contains("Entity") ? 1f : 0f);
        FabricModelPredicateProviderRegistry.register(ConjuringItems.ENCHIRIDION, new Identifier("is_sandwich"), (stack, world, entity, seed) -> stack.getOrCreateNbt().getBoolean("Sandwich") ? 1f : 0f);
        FabricModelPredicateProviderRegistry.register(ConjuringItems.PIZZA, new Identifier("is_brinsa"), (stack, world, entity, seed) -> stack.getOrCreateNbt().contains("Brinsa") ? 1f : 0f);

        ScreenRegistry.register(Conjuring.CONJURER_SCREEN_HANDLER_TYPE, ConjurerScreen::new);
        ScreenRegistry.register(Conjuring.SOULFIRE_FORGE_SCREEN_HANDLER_TYPE, SoulfireForgeScreen::new);

        TOGGLE_TOOL_MODE = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.conjuring.toggle_tool_mode", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "category.conjuring"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_TOOL_MODE.wasPressed()) {
                client.getNetworkHandler().sendPacket(ChangeToolModePacket.create());
            }
        });

        ConjuringParticleEvents.Client.registerClientListeners();

        WorldRenderEvents.BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {
            final MinecraftClient client = MinecraftClient.getInstance();
            if (!SoulAlloyToolAbilities.canAoeDig(client.player)) {
                return true;
            }

            BlockState blockState;
            for (BlockPos pos : SoulAlloyToolAbilities.getBlocksToDig(client.player, ((SoulAlloyTool) client.player.getMainHandStack().getItem()).getAoeToolOverridePredicate())) {

                blockState = client.world.getBlockState(pos);
                if (!blockState.isAir()) {
                    WorldRendererInvoker.conjuring_drawShapeOutline(worldRenderContext.matrixStack(), blockOutlineContext.vertexConsumer(), blockState.getOutlineShape(client.world, pos, ShapeContext.of(blockOutlineContext.entity())), (double) pos.getX() - blockOutlineContext.cameraX(), (double) pos.getY() - blockOutlineContext.cameraY(), (double) pos.getZ() - blockOutlineContext.cameraZ(), 0.0F, 0.0F, 0.0F, 0.4F);
                }
            }
            return true;
        });

    }

}
