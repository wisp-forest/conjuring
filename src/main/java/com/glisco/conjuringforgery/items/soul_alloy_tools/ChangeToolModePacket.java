package com.glisco.conjuringforgery.items.soul_alloy_tools;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeToolModePacket {

    public static void encode(ChangeToolModePacket packet, PacketBuffer buffer) { }

    public static ChangeToolModePacket decode(PacketBuffer buffer){
        return new ChangeToolModePacket();
    }

    public static void handle(ChangeToolModePacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        final NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            final ServerPlayerEntity player = context.getSender();
            if(!player.isSneaking()) return;
            if(!(player.getHeldItemMainhand().getItem() instanceof SoulAlloyTool)) return;
            SoulAlloyTool.toggleEnabledState(player.getHeldItemMainhand());
            player.playSound(SoulAlloyTool.isSecondaryEnabled(player.getHeldItemMainhand()) ? SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN : SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.PLAYERS, 1,  1.5f + player.world.rand.nextFloat() * 0.5f);
        });
        context.setPacketHandled(true);
    }
}
