package com.glisco.conjuring.items;

import com.glisco.conjuring.Conjuring;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class EnchiridionItem extends Item {

    private final Identifier BOOK_ID = Conjuring.id("conjuring_guide");

    public EnchiridionItem() {
        super(new Settings().maxCount(1).group(Conjuring.CONJURING_GROUP));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
//        Book book = BookRegistry.INSTANCE.books.get(BOOK_ID);

        if (!world.isClient()) {
            player.sendMessage(Text.literal("Patchouli support is currently not implemented, click this link instead")
                    .styled(style -> style
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://guides.wispforest.io/conjuring/"))
                            .withFormatting(Formatting.UNDERLINE)));

//            PatchouliAPI.get().openBookGUI((ServerPlayerEntity) player, book.id);
//            player.playSound(PatchouliSounds.getSound(book.openSound, PatchouliSounds.BOOK_OPEN), 1, (float) (0.7 + Math.random() * 0.4));
        }


        return TypedActionResult.success(player.getStackInHand(hand));
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
        return stack.getOrCreateNbt().getBoolean("Sandwich") ? Text.literal("Ice Cream Sandwich") : super.getName(stack);
    }
}
