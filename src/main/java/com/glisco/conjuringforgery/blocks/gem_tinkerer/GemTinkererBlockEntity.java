package com.glisco.conjuringforgery.blocks.gem_tinkerer;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.items.GemItem;
import com.glisco.conjuringforgery.items.soul_alloy_tools.SoulAlloyTool;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GemTinkererBlockEntity extends TileEntity implements ITickableTileEntity {

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
    private int processTick = 0;
    private GemTinkererRecipe cachedRecipe;

    boolean particlesShown = false;

    public GemTinkererBlockEntity() {
        super(ConjuringForgery.GEM_TINKERER_TILE.get());
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        inventory.clear();
        ItemStackHelper.loadAllItems(tag, inventory);
        processTick = tag.getInt("ProcessTick");
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        ItemStackHelper.saveAllItems(tag, inventory);
        tag.putInt("ProcessTick", processTick);
        return super.write(tag);
    }

    public boolean verifyRecipe() {

        Inventory testInventory = new Inventory(5);
        for (int i = 0; i < inventory.size(); i++) {
            testInventory.setInventorySlotContents(i, inventory.get(i));
        }

        Optional<GemTinkererRecipe> recipeOptional = world.getRecipeManager().getRecipe(GemTinkererRecipe.Type.INSTANCE, testInventory, world);

        if (!recipeOptional.isPresent()) return false;

        cachedRecipe = recipeOptional.get();
        return true;
    }

    public ActionResultType onUse(PlayerEntity player) {

        if (verifyRecipe()) {
            if (!world.isRemote()) {
                processTick = 1;
                markDirty();
            }

            return ActionResultType.SUCCESS;
        }

        if (!(inventory.get(0).getItem() instanceof SoulAlloyTool)) return ActionResultType.PASS;

        List<SoulAlloyTool.SoulAlloyModifier> presentModifiers = new ArrayList<>();

        for (int i = 1; i < inventory.size(); i++) {
            if (!inventory.get(i).isEmpty()) {

                if (!(inventory.get(i).getItem() instanceof GemItem)) return ActionResultType.PASS;

                final SoulAlloyTool.SoulAlloyModifier modifier = ((GemItem) inventory.get(i).getItem()).getModifier();
                presentModifiers.add(modifier);
            }
        }

        if (presentModifiers.size() < 1) return ActionResultType.PASS;
        if (!SoulAlloyTool.canAddModifiers(inventory.get(0), presentModifiers)) return ActionResultType.PASS;

        if (!world.isRemote()) {
            processTick = 1;
            markDirty();
            //TODO criterion
            //ConjuringForgery.GEM_TINKERING_CRITERION.trigger((ServerPlayerEntity) player);
        }

        return ActionResultType.SUCCESS;
    }


    @Override
    public void tick() {
        if (processTick > 0) {

            if (processTick == 1 && !world.isRemote()) {
                world.playSound(null, pos, SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK, SoundCategory.BLOCKS, 0.25f, 0);

            }

            if (processTick == 100) {

                if (!world.isRemote) {

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
                        inventory.set(0, cachedRecipe.getRecipeOutput());
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
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(getBlockState(), pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getPos(), 0, this.write(new CompoundNBT()));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    public NonNullList<ItemStack> getInventory() {
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

    public boolean isCraftingComplete(){
        return processTick > 100;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }
}
