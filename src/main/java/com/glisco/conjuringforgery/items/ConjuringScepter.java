package com.glisco.conjuringforgery.items;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.blocks.BlackstonePedestalTileEntity;
import com.glisco.conjuringforgery.blocks.RitualCore;
import com.glisco.conjuringforgery.blocks.SoulFunnelTileEntity;
import com.glisco.owo.client.ClientParticles;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

public class ConjuringScepter extends Item {

    public ConjuringScepter(Properties properties) {
        super(properties);
    }

    public ConjuringScepter() {
        this(new Properties().group(ConjuringForgery.CONJURING_GROUP).maxStackSize(1).rarity(Rarity.UNCOMMON));
    }

    public static boolean isLinking(ItemStack scepter) {
        return scepter.getOrCreateTag().contains("LinkingFrom");
    }

    public static BlockPos getLinkingFrom(ItemStack scepter) {
        int[] intPos = scepter.getOrCreateTag().getIntArray("LinkingFrom");
        return intPos.length == 0 ? null : new BlockPos(intPos[0], intPos[1], intPos[2]);
    }

    public static void startLinking(ItemStack scepter, BlockPos pedestal) {
        CompoundNBT stackTag = scepter.getOrCreateTag();
        stackTag.putIntArray("LinkingFrom", new int[]{pedestal.getX(), pedestal.getY(), pedestal.getZ()});
        scepter.setTag(stackTag);
    }

    public static String finishLinking(World world, ItemStack scepter, BlockPos core) {
        CompoundNBT stackTag = scepter.getOrCreateTag();
        if (!isLinking(scepter)) return "INVALID_SCEPTER";
        BlockPos pedestal = getLinkingFrom(scepter);

        stackTag.remove("LinkingFrom");
        scepter.setTag(stackTag);

        if (!(world.getTileEntity(pedestal) instanceof BlackstonePedestalTileEntity)) return "NO_PEDESTAL";
        if (!(world.getTileEntity(core) instanceof RitualCore)) return "NO_FUNNEL";

        if (core.manhattanDistance(pedestal) != 3 || core.getY() != pedestal.getY()) {
            return "INCORRECT_POSITION";
        }

        if (((RitualCore) world.getTileEntity(core)).linkPedestal(pedestal)) {
            ((BlackstonePedestalTileEntity) world.getTileEntity(pedestal)).setLinkedFunnel(core);
            return "SUCCESS";
        } else {
            return "PEDESTAL_LIMIT_REACHED";
        }
    }

    public static ActionResultType onBlockUse(ItemUseContext context) {
        if (!context.getPlayer().isSneaking()) return ActionResultType.PASS;

        BlockPos pos = context.getPos();
        World world = context.getWorld();
        ItemStack scepter = context.getItem();

        if (world.getTileEntity(pos) instanceof BlackstonePedestalTileEntity) {
            startLinking(scepter, pos);
            return ActionResultType.SUCCESS;
        } else if (world.getTileEntity(pos) instanceof RitualCore) {
            String result = finishLinking(world, scepter, pos);
            switch (result) {
                case "SUCCESS":
                    context.getPlayer().sendStatusMessage(new TranslationTextComponent("item.conjuring.conjuring_scepter.linking_success").mergeStyle(TextFormatting.GREEN), true);
                    return ActionResultType.SUCCESS;
                case "PEDESTAL_LIMIT_REACHED":
                    context.getPlayer().sendStatusMessage(new TranslationTextComponent("item.conjuring.conjuring_scepter.max_pedestals").mergeStyle(TextFormatting.RED), true);
                    return ActionResultType.PASS;
                case "INCORRECT_POSITION":
                    context.getPlayer().sendStatusMessage(new TranslationTextComponent("item.conjuring.conjuring_scepter.incorrect_position").mergeStyle(TextFormatting.RED), true);
                    return ActionResultType.PASS;
                default:
                    return ActionResultType.PASS;
            }
        } else {
            return ActionResultType.PASS;
        }
    }

    public static ActionResult<ItemStack> onUse(PlayerEntity user, Hand hand) {
        ItemStack scepter = user.getHeldItem(hand);

        if (!user.isSneaking()) return ActionResult.resultPass(scepter);
        if (!isLinking(scepter)) return ActionResult.resultPass(scepter);

        CompoundNBT stackTag = scepter.getOrCreateTag();
        stackTag.remove("LinkingFrom");
        scepter.setTag(stackTag);

        return ActionResult.resultSuccess(scepter);
    }


    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        return onBlockUse(context);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity user, Hand hand) {
        return onUse(user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!selected) return;
        if (entity instanceof ServerPlayerEntity) return;

        if (!isLinking(stack)) return;

        BlockPos pedestal = getLinkingFrom(stack);
        ClientPlayerEntity player = (ClientPlayerEntity) entity;

        IParticleData particle = new RedstoneParticleData(1, 1, 1, 1);
        ClientParticles.spawnWithOffsetFromBlock(particle, world, pedestal, new Vector3d(0.5, 1.25, 0.5), 0.15);

        IFormattableTextComponent linkingFrom = new TranslationTextComponent("item.conjuring.conjuring_scepter.linking");

        IFormattableTextComponent text = new StringTextComponent("§7[§3" + pedestal.getZ() + " " + pedestal.getY() + " " + pedestal.getX() + "§7]");

        ITextComponent message = linkingFrom.appendSibling(text);
        player.sendStatusMessage(message, true);
    }
}
