package com.glisco.conjuring.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class TooltipMixin {

    @Shadow
    public abstract boolean isEmpty();

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract CompoundTag getTag();

    @Inject(method = "getTooltip", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void getTooltipdone(PlayerEntity playerIn, TooltipContext advanced, CallbackInfoReturnable<List> ci, List<Text> list) {

        try {
            if (!this.isEmpty() && this.getItem() == Items.SPAWNER) {
                CompoundTag tag = this.getTag();
                if (tag != null) {
                    CompoundTag spawnData = tag.getCompound("BlockEntityTag").getList("SpawnPotentials", 10).getCompound(0).getCompound("Entity");

                    String name = spawnData.getString("id").replace(':', '.');
                    MutableText translate = new TranslatableText("entity." + name);

                    list.add(Math.min(1, list.size()), translate.setStyle(Style.EMPTY.withColor(TextColor.parse("gray"))));

                    //TODO remove armor and make it show spawner stats instead

                    /*if (Screen.hasShiftDown()) {

                        LiteralText mobText = (LiteralText) new LiteralText("Mob Type: ").setStyle(Style.EMPTY.withColor(TextColor.parse("dark_aqua")));
                        mobText.append(translate.setStyle(Style.EMPTY.withColor(TextColor.parse("gray"))));

                        list.add(Math.min(1, list.size()), mobText);
                        list.add(Math.min(2, list.size()), new LiteralText(""));

                        for (int i = 0; i < 4; i++) {
                            CompoundTag armor = armorData.getCompound(i);
                            Text armorName = Registry.ITEM.get(new Identifier(armor.getString("id"))).getName();

                            if (armorName.getString().equals("Air")) {
                                LiteralText text = (LiteralText) new LiteralText(SpawnertooltipClient.armor.get(i)).setStyle(Style.EMPTY.withColor(TextColor.parse("dark_aqua")));
                                text.append(new LiteralText("--").setStyle(Style.EMPTY.withColor(TextColor.parse("gray"))));

                                list.add(Math.min(i + 3, list.size()), text);
                            } else {
                                LiteralText text = (LiteralText) new LiteralText(SpawnertooltipClient.armor.get(i)).setStyle(Style.EMPTY.withColor(TextColor.parse("dark_aqua")));
                                text.append(new LiteralText(armorName.getString()).setStyle(Style.EMPTY.withColor(TextColor.parse("gray"))));
                                list.add(Math.min(i + 3, list.size()), text);
                            }
                        }

                    } else {

                    }*/
                }
            }
        } catch (NullPointerException ex) {
            System.out.println("NPE in getTooltipdone");
            try {
                Item item = this.getItem();
                if (item == null) {
                    System.out.println("item is null");
                } else {
                    System.out.println("item is " + this.getItem().getTranslationKey());
                }
            } catch (NullPointerException ex2) {

            }
        }
    }
}
