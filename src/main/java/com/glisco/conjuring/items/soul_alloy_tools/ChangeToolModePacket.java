package com.glisco.conjuring.items.soul_alloy_tools;

import com.glisco.conjuring.Conjuring;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ChangeToolModePacket {

    public static final Identifier ID = Conjuring.id("toggle_tool_mode");

    public static void onPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender sender) {
        server.execute(() -> {
            if (!player.isSneaking()) return;
            if (!(player.getMainHandStack().getItem() instanceof SoulAlloyTool)) return;
            SoulAlloyTool.toggleEnabledState(player.getMainHandStack());
            player.playSound(SoulAlloyTool.isSecondaryEnabled(player.getMainHandStack()) ? SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN : SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.PLAYERS, 1, 1.5f + player.world.random.nextFloat() * 0.5f);
        });
    }

    public static Packet<?> create() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        return ClientPlayNetworking.createC2SPacket(ID, buffer);
    }
}
