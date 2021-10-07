package com.glisco.conjuring.items;

import com.glisco.conjuring.ConjuringCommon;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

public class EnchiridionItem extends Item {

    private final Identifier BOOK_ID = new Identifier("conjuring", "conjuring_guide");

    public EnchiridionItem() {
        super(new Settings().maxCount(1).group(ConjuringCommon.CONJURING_GROUP));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        Book book = BookRegistry.INSTANCE.books.get(BOOK_ID);

        if (!world.isClient()) {
            PatchouliAPI.get().openBookGUI((ServerPlayerEntity) player, book.id);
            player.playSound(PatchouliSounds.getSound(book.openSound, PatchouliSounds.book_open), 1, (float) (0.7 + Math.random() * 0.4));
        }

        return TypedActionResult.success(stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getPlayer().isSneaking()) return ActionResult.PASS;
        if (!context.getWorld().getBlockState(context.getBlockPos()).isOf(Blocks.SNOW_BLOCK)) return ActionResult.PASS;

        final NbtCompound stackTag = context.getStack().getOrCreateNbt();
        stackTag.putBoolean("Sandwich", !stackTag.getBoolean("Sandwich"));

        return ActionResult.SUCCESS;
    }

    @Override
    public Text getName(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean("Sandwich") ? new LiteralText("Ice Cream Sandwich") : super.getName(stack);
    }
}
