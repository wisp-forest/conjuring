package com.glisco.conjuring.blocks;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.items.SoulAlloyTool;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;

public class GemTinkererBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private int processTick = 0;

    public GemTinkererBlockEntity() {
        super(ConjuringCommon.GEM_TINKERER_BLOCK_ENTITY);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        Inventories.fromTag(tag, inventory);
        processTick = tag.getInt("ProcessTick");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, inventory);
        tag.putInt("ProcessTick", processTick);
        return super.toTag(tag);
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(this.getCachedState(), compoundTag);
    }

    public ActionResult onUse() {
        if (!(inventory.get(0).getItem() instanceof SoulAlloyTool)) return ActionResult.PASS;
        if (!SoulAlloyTool.canAddModifiers(inventory.get(0))) return ActionResult.PASS;

        if (!world.isClient()) {
            processTick = 1;
            markDirty();
        }

        return ActionResult.SUCCESS;
    }


    @Override
    public void tick() {
        if (processTick > 0) {

            if (processTick == 100) {

                if (!world.isClient) {
                    for (int i = 1; i < inventory.size(); i++) {
                        inventory.set(i, ItemStack.EMPTY);
                    }
                    markDirty();
                }
            }

            if (processTick > 200) {
                processTick = 0;
                return;
            }

            processTick++;
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }

    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    public boolean isRunning() {
        return processTick > 0;
    }

    public double getScalar() {
        return processTick < 100 ? 1 + processTick / 4d : 1 + ((200 - processTick) / 4d);
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (!world.isClient()) {
            sync();
        }
    }
}
