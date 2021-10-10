package com.glisco.conjuring.entities;

import com.glisco.conjuring.Conjuring;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class EntityCreatePacket {

    public static final Identifier ID = Conjuring.id("entity_create");

    public static Packet<?> create(Entity e) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeVarInt(Registry.ENTITY_TYPE.getRawId(e.getType()));
        buffer.writeVarInt(e.getId());
        buffer.writeUuid(e.getUuid());
        buffer.writeDouble(e.getX());
        buffer.writeDouble(e.getY());
        buffer.writeDouble(e.getZ());
        buffer.writeByte(MathHelper.floor(e.getPitch() * 256f / 360f));
        buffer.writeByte(MathHelper.floor(e.getYaw() * 256f / 360f));
        return ServerPlayNetworking.createS2CPacket(ID, buffer);
    }

    @Environment(EnvType.CLIENT)
    public static void onPacket(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buffer, PacketSender sender) {

        EntityType<?> entityType = Registry.ENTITY_TYPE.get(buffer.readVarInt());
        int entityID = buffer.readVarInt();
        UUID uuid = buffer.readUuid();
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        float pitch = (buffer.readByte() * 360f) / 256f;
        float yaw = (buffer.readByte() * 360f) / 256f;

        ClientWorld world = client.world;
        Entity e = entityType.create(world);

        client.execute(() -> {
            if (e != null) {
                e.setId(entityID);
                e.setUuid(uuid);
                e.setPosition(x, y, z);
                e.updateTrackedPosition(x, y, z);
                e.setPitch(pitch);
                e.setYaw(yaw);

                world.addEntity(entityID, e);
            }
        });

    }
}
