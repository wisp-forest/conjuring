package com.glisco.owo;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

import static net.minecraft.command.Commands.literal;

@Mod("owo")
public class Owo {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public Owo() {
        MinecraftForge.EVENT_BUS.register(this);
        ServerParticles.initialize();
    }

    @SubscribeEvent
    public void onServerStarting(RegisterCommandsEvent event) {
        event.getDispatcher().register(literal("dumpdata").then(literal("item").executes(context -> {

            if (!context.getSource().asPlayer().getHeldItemMainhand().hasTag()) {
                context.getSource().sendErrorMessage(new StringTextComponent("This item has no tag"));
            } else {
                ITextComponent message = context.getSource().asPlayer().getHeldItemMainhand().getTag().toFormattedComponent();
                context.getSource().asPlayer().sendStatusMessage(message, false);
            }
            return 0;
        })).then(literal("block").executes(context -> {

            final CommandSource source = context.getSource();
            final ServerPlayerEntity player = source.asPlayer();
            RayTraceResult target = player.pick(5, 0, false);

            if (target.getType() != RayTraceResult.Type.BLOCK) {
                source.sendErrorMessage(new StringTextComponent("You're not looking at a block"));
                return 1;
            }

            BlockPos pos = ((BlockRayTraceResult) target).getPos();

            String blockState = player.getServerWorld().getBlockState(pos).toString();
            String blockId = blockState.split(Pattern.quote("["))[0];
            blockId = blockId.substring(6, blockId.length() - 1);

            if (blockState.contains("[")) {
                String stateInfo = "[" + blockState.split(Pattern.quote("["))[1];
                source.sendFeedback(new StringTextComponent("Block ID: " + blockId), false);
                source.sendFeedback(new StringTextComponent("BlockState: " + stateInfo), false);
            } else {
                source.sendFeedback(new StringTextComponent("Block ID: " + blockId), false);
            }

            if (player.getServerWorld().getTileEntity(pos) != null) {
                source.sendFeedback(new StringTextComponent("Tag: ").appendSibling(player.getServerWorld().getTileEntity(pos).write(new CompoundNBT()).toFormattedComponent()), false);
            }

            return 0;
        })));
    }
}
