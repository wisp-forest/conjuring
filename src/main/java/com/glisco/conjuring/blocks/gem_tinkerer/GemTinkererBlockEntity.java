package com.glisco.conjuring.blocks.gem_tinkerer;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.items.GemItem;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyTool;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GemTinkererBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private int processTick = 0;
    private GemTinkererRecipe cachedRecipe;

    boolean particlesShown = false;

    public GemTinkererBlockEntity() {
        super(ConjuringCommon.GEM_TINKERER_BLOCK_ENTITY);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        inventory.clear();
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

    public boolean verifyRecipe() {

        Inventory testInventory = new SimpleInventory(5);
        for (int i = 0; i < inventory.size(); i++) {
            testInventory.setStack(i, inventory.get(i));
        }

        Optional<GemTinkererRecipe> recipeOptional = world.getRecipeManager().getFirstMatch(GemTinkererRecipe.Type.INSTANCE, testInventory, world);

        if (!recipeOptional.isPresent()) return false;

        cachedRecipe = recipeOptional.get();
        return true;
    }

    public ActionResult onUse() {

        if (verifyRecipe()) {
            if (!world.isClient()) {
                processTick = 1;
                markDirty();
            }

            return ActionResult.SUCCESS;
        }

        if (!(inventory.get(0).getItem() instanceof SoulAlloyTool)) return ActionResult.PASS;

        List<SoulAlloyTool.SoulAlloyModifier> presentModifiers = new ArrayList<>();

        for (int i = 1; i < inventory.size(); i++) {
            if (!inventory.get(i).isEmpty() && inventory.get(i).getItem() instanceof GemItem) {
                final SoulAlloyTool.SoulAlloyModifier modifier = ((GemItem) inventory.get(i).getItem()).getModifier();
                presentModifiers.add(modifier);
            }
        }

        if (!SoulAlloyTool.canAddModifiers(inventory.get(0), presentModifiers)) return ActionResult.PASS;

        if (!world.isClient()) {
            processTick = 1;
            markDirty();
        }

        return ActionResult.SUCCESS;
    }


    @Override
    public void tick() {
        if (processTick > 0) {

            if (processTick == 1 && !world.isClient()) {
                world.playSound(null, pos, SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK, SoundCategory.BLOCKS, 0.25f, 0);

            }

            if (processTick == 100) {

                if (!world.isClient) {

                    world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1, 0);
                    verifyRecipe();

                    if (cachedRecipe == null) {
                        for (int i = 1; i < inventory.size(); i++) {
                            if (!inventory.get(i).isEmpty() && SoulAlloyTool.canAddModifier(inventory.get(0), ((GemItem) inventory.get(i).getItem()).getModifier())) {
                                SoulAlloyTool.addModifier(inventory.get(0), ((GemItem) inventory.get(i).getItem()).getModifier());
                                inventory.set(i, ItemStack.EMPTY);
                            }

                        }
                    } else {
                        for (int i = 1; i < inventory.size(); i++) {
                            inventory.set(i, ItemStack.EMPTY);
                        }
                        inventory.set(0, cachedRecipe.getOutput());
                    }

                    markDirty();
                }
            }

            if (processTick > 200) {
                processTick = 0;
                particlesShown = false;
                cachedRecipe = null;
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

    public boolean particles() {
        if (!particlesShown && processTick == 100) {
            particlesShown = true;
            return true;
        }

        return false;
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (!world.isClient()) {
            sync();
        }
    }
}
