package com.glisco.conjuringforgery.items;

import com.glisco.conjuringforgery.ConjuringForgery;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

public class EnchiridionItem extends Item {

    private final ResourceLocation BOOK_ID = new ResourceLocation("conjuring", "conjuring_guide");

    public EnchiridionItem() {
        super(new Properties().maxStackSize(1).group(ConjuringForgery.CONJURING_GROUP));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        Book book = BookRegistry.INSTANCE.books.get(BOOK_ID);

        if (!world.isRemote()) {
            PatchouliAPI.get().openBookGUI((ServerPlayerEntity) player, book.id);
            player.playSound(PatchouliSounds.getSound(book.openSound, PatchouliSounds.book_open), 1, (float) (0.7 + Math.random() * 0.4));
        }

        return ActionResult.resultSuccess(stack);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (!context.getPlayer().isSneaking()) return ActionResultType.PASS;
        if (!context.getWorld().getBlockState(context.getPos()).matchesBlock(Blocks.SNOW_BLOCK)) return ActionResultType.PASS;

        final CompoundNBT stackTag = context.getItem().getOrCreateTag();
        stackTag.putBoolean("Sandwich", !stackTag.getBoolean("Sandwich"));

        return ActionResultType.SUCCESS;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("Sandwich") ? new StringTextComponent("Ice Cream Sandwich") : super.getDisplayName(stack);
    }
}
