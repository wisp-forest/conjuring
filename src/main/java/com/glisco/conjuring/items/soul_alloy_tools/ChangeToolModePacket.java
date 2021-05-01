package com.glisco.conjuring.items.soul_alloy_tools;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ChangeToolModePacket {

    public static final Identifier ID = new Identifier("conjuring", "toggle_tool_mode");

    public static void onPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender sender) {
        server.execute(() -> {
            if (!player.isSneaking()) return;
            if (!(player.getMainHandStack().getItem() instanceof SoulAlloyTool)) return;
            SoulAlloyTool.toggleEnabledState(player.getMainHandStack());
        });
    }

    public static Packet<?> create() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        return ClientPlayNetworking.createC2SPacket(ID, buffer);
    }
}
