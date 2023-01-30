package com.glisco.conjuring.items;

import com.glisco.conjuring.Conjuring;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

public class EnchiridionItem extends Item {

    private final Identifier BOOK_ID = Conjuring.id("conjuring_guide");

    public EnchiridionItem() {
        super(new OwoItemSettings().maxCount(1).group(Conjuring.CONJURING_GROUP));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient()) {
            if (FabricLoader.getInstance().isModLoaded("patchouli")) {
                this.openPatchouliBook(player);
            } else {
                player.sendMessage(
                        Text.literal("You don't currently have ")
                                .append(link("Patchouli", "https://modrinth.com/mod/patchouli"))
                                .append(Text.literal(" installed. You can view "))
                                .append(link("the online Enchiridion", "https://guides.wispforest.io/conjuring/"))
                                .append(Text.literal(" instead"))
                );
            }
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }

    private MutableText link(String text, String url) {
        return Text.literal(text).styled(style -> {
            return style.withFormatting(Formatting.BLUE)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(url)));
        });
    }

    private void openPatchouliBook(PlayerEntity player) {
        Book book = BookRegistry.INSTANCE.books.get(BOOK_ID);

        PatchouliAPI.get().openBookGUI((ServerPlayerEntity) player, book.id);
        player.playSound(PatchouliSounds.getSound(book.openSound, PatchouliSounds.BOOK_OPEN), 1, (float) (0.7 + Math.random() * 0.4));
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
