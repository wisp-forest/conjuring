package com.glisco.owo;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.function.Consumer;

public class ServerParticles {

    public static final Gson NETWORK_GSON = new Gson();

    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("owo", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void initialize() {
        NETWORK_CHANNEL.registerMessage(0, ParticleSystemPacket.class, ParticleSystemPacket::encode, ParticleSystemPacket::decode, (packet, contextSupplier) -> {
            contextSupplier.get().setPacketHandled(true);
        });
    }

    private static HashMap<ResourceLocation, ParticlePacketHandler> handlerRegistry = new HashMap<>();

    /**
     * Registers a handler that can react to particle events sent by the server
     *
     * @param id      The handler's id, <b>must be unique</b>
     * @param handler The handler itself
     */
    public static void registerClientSideHandler(ResourceLocation id, ParticlePacketHandler handler) {
        if (handlerRegistry.containsKey(id)) throw new IllegalStateException("A handler with id " + id + " already exists");
        handlerRegistry.put(id, handler);
    }

    /**
     * Issues a particle event for the corresponding handler on all clients in range
     *
     * @param world         The world the event is happening in
     * @param pos           The position the event should happen at
     * @param handlerId     The client-side handler for this event
     * @param dataProcessor Optional consumer to add data to the sent packet
     */
    public static void issueEvent(ServerWorld world, BlockPos pos, ResourceLocation handlerId, Consumer<PacketBuffer> dataProcessor) {
        NETWORK_CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new ParticleSystemPacket(handlerId, pos, dataProcessor));
    }

    /**
     * Issues a particle event for the corresponding handler of the given player
     *
     * @param player        The world the event is happening in
     * @param pos           The position the event should happen at
     * @param handlerId     The client-side handler for this event
     * @param dataProcessor Optional consumer to add data to the sent packet
     */
    public static void issueEvent(ServerPlayerEntity player, BlockPos pos, ResourceLocation handlerId, Consumer<PacketBuffer> dataProcessor) {
        NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ParticleSystemPacket(handlerId, pos, dataProcessor));
    }

    static ParticlePacketHandler getHandler(ResourceLocation id) {
        return handlerRegistry.getOrDefault(id, null);
    }

    @FunctionalInterface
    public interface ParticlePacketHandler {
        void onPacket(Minecraft client, BlockPos pos, PacketBuffer data);
    }

}
