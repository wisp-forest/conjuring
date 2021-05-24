package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.blocks.BlackstonePedestalBlockEntity;
import com.glisco.conjuring.blocks.RitualCore;
import com.glisco.owo.client.ClientParticles;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ConjuringScepter extends Item {

    public ConjuringScepter(Settings settings) {
        super(settings);
    }

    public ConjuringScepter() {
        this(new Item.Settings().group(ConjuringCommon.CONJURING_GROUP).maxCount(1).rarity(Rarity.UNCOMMON));
    }

    public static boolean isLinking(ItemStack scepter) {
        return scepter.getOrCreateTag().contains("LinkingFrom");
    }

    public static BlockPos getLinkingFrom(ItemStack scepter) {
        int[] intPos = scepter.getOrCreateTag().getIntArray("LinkingFrom");
        return intPos.length == 0 ? null : new BlockPos(intPos[0], intPos[1], intPos[2]);
    }

    public static void startLinking(ItemStack scepter, BlockPos pedestal) {
        CompoundTag stackTag = scepter.getOrCreateTag();
        stackTag.putIntArray("LinkingFrom", new int[]{pedestal.getX(), pedestal.getY(), pedestal.getZ()});
        scepter.setTag(stackTag);
    }

    public static String finishLinking(World world, ItemStack scepter, BlockPos core) {
        CompoundTag stackTag = scepter.getOrCreateTag();
        if (!isLinking(scepter)) return "INVALID_SCEPTER";
        BlockPos pedestal = getLinkingFrom(scepter);

        stackTag.remove("LinkingFrom");
        scepter.setTag(stackTag);

        if (!(world.getBlockEntity(pedestal) instanceof BlackstonePedestalBlockEntity)) return "NO_PEDESTAL";
        if (!(world.getBlockEntity(core) instanceof RitualCore)) return "NO_FUNNEL";

        if (core.getManhattanDistance(pedestal) != 3 || core.getY() != pedestal.getY()) {
            return "INCORRECT_POSITION";
        }

        if (((RitualCore) world.getBlockEntity(core)).linkPedestal(pedestal)) {
            ((BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal)).setLinkedFunnel(core);
            return "SUCCESS";
        } else {
            return "PEDESTAL_LIMIT_REACHED";
        }
    }

    public static ActionResult onBlockUse(ItemUsageContext context) {
        if (!context.getPlayer().isSneaking()) return ActionResult.PASS;

        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        ItemStack scepter = context.getStack();

        if (world.getBlockEntity(pos) instanceof BlackstonePedestalBlockEntity && !((BlackstonePedestalBlockEntity) world.getBlockEntity(pos)).isActive()) {
            startLinking(scepter, pos);
            return ActionResult.SUCCESS;
        } else if (world.getBlockEntity(pos) instanceof RitualCore) {
            String result = finishLinking(world, scepter, pos);
            switch (result) {
                case "SUCCESS":
                    context.getPlayer().sendMessage(new TranslatableText("item.conjuring.conjuring_scepter.linking_success").formatted(Formatting.GREEN), true);
                    return ActionResult.SUCCESS;
                case "PEDESTAL_LIMIT_REACHED":
                    context.getPlayer().sendMessage(new TranslatableText("item.conjuring.conjuring_scepter.max_pedestals").formatted(Formatting.RED), true);
                    return ActionResult.PASS;
                case "INCORRECT_POSITION":
                    context.getPlayer().sendMessage(new TranslatableText("item.conjuring.conjuring_scepter.incorrect_position").formatted(Formatting.RED), true);
                    return ActionResult.PASS;
                default:
                    return ActionResult.PASS;
            }
        } else {
            return ActionResult.PASS;
        }
    }

    public static TypedActionResult<ItemStack> onUse(PlayerEntity user, Hand hand) {
        ItemStack scepter = user.getStackInHand(hand);

        if (!user.isSneaking()) return TypedActionResult.pass(scepter);
        if (!isLinking(scepter)) return TypedActionResult.pass(scepter);

        CompoundTag stackTag = scepter.getOrCreateTag();
        stackTag.remove("LinkingFrom");
        scepter.setTag(stackTag);

        return TypedActionResult.success(scepter);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return onBlockUse(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return onUse(user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!selected) return;
        if (entity instanceof ServerPlayerEntity) return;

        if (!isLinking(stack)) return;

        BlockPos pedestal = getLinkingFrom(stack);
        ClientPlayerEntity player = (ClientPlayerEntity) entity;

        ParticleEffect particle = new DustParticleEffect(1, 1, 1, 1);
        ClientParticles.spawnWithOffsetFromBlock(particle, world, pedestal, new Vec3d(0.5, 1.25, 0.5), 0.15);

        MutableText linkingFrom = new TranslatableText("item.conjuring.conjuring_scepter.linking");

        MutableText text = new LiteralText("§7[§3" + pedestal.getZ() + " " + pedestal.getY() + " " + pedestal.getX() + "§7]");

        Text message = linkingFrom.append(text);
        player.sendMessage(message, true);
    }
}
