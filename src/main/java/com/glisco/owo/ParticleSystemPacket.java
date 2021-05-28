package com.glisco.owo;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class ParticleSystemPacket {

    private static final Logger LOGGER = LogManager.getLogger();

    public final BlockPos pos;
    public final ResourceLocation handlerId;
    public final Consumer<PacketBuffer> packetProcessor;

    public ParticleSystemPacket(ResourceLocation handlerId, BlockPos pos, Consumer<PacketBuffer> packetProcessor) {
        this.pos = pos;
        this.handlerId = handlerId;
        this.packetProcessor = packetProcessor;
    }

    static void encode(ParticleSystemPacket packet, PacketBuffer buffer) {
        buffer.writeResourceLocation(packet.handlerId);
        buffer.writeBlockPos(packet.pos);
        packet.packetProcessor.accept(buffer);
    }

    static ParticleSystemPacket decode(PacketBuffer buffer) {

        ResourceLocation handlerID = buffer.readResourceLocation();
        BlockPos pos = buffer.readBlockPos();

        ServerParticles.ParticlePacketHandler handler = ServerParticles.getHandler(handlerID);
        if (handler == null) {
            LOGGER.warn("Received particle packet for unknown handler \"" + handlerID + "\"");
        } else {
            handler.onPacket(Minecraft.getInstance(), pos, buffer);
        }

        return new ParticleSystemPacket(handlerID, pos, buf -> {});
    }


}
