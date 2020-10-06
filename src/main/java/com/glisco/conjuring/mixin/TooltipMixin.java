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
    private void generateSpawnerTooltip(PlayerEntity playerIn, TooltipContext advanced, CallbackInfoReturnable<List> ci, List<Text> list) {
        try {
            if (!this.isEmpty() && this.getItem() == Items.SPAWNER) {
                CompoundTag tag = this.getTag();
                if (tag != null) {
                    CompoundTag spawnData = tag.getCompound("BlockEntityTag").getList("SpawnPotentials", 10).getCompound(0).getCompound("Entity");

                    String name = spawnData.getString("id").replace(':', '.');
                    MutableText translate = new TranslatableText("entity." + name);

                    list.add(Math.min(1, list.size()), translate.setStyle(Style.EMPTY.withColor(TextColor.parse("gray"))));


                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
