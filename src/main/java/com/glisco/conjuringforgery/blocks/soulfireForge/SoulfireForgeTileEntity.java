package com.glisco.conjuringforgery.blocks.soulfireForge;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.google.common.base.Preconditions;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.NonNullList;

import java.util.Optional;

public class SoulfireForgeTileEntity extends TileEntity implements ITickableTileEntity {

    private final Inventory inventory = new Inventory(10);

    private int progress;
    private int smeltTime;
    private int targetSmeltTime;

    public SoulfireForgeTileEntity() {
        super(ConjuringForgery.SOULFIRE_FORGE_TILE.get());
    }

    private final IntArray properties = new IntArray(1) {
        @Override
        public int get(int index) {
            return progress;
        }

        @Override
        public void set(int index, int value) {
            progress = value;
        }
    };

    public IIntArray getProperties() {
        return properties;
    }

    //Tick Logic
    @Override
    public void tick() {
        if (!this.world.isRemote()) {
            Optional<SoulfireForgeRecipe> currentRecipe = world.getRecipeManager().getRecipe(SoulfireForgeRecipe.Type.INSTANCE, inventory, world);

            if (currentRecipe.isPresent() && world.getBlockState(pos).get(SoulfireForgeBlock.BURNING)) {
                if (checkOutput(currentRecipe.get().getRecipeOutput())) {
                    targetSmeltTime = currentRecipe.get().getSmeltTime();

                    if (smeltTime == targetSmeltTime) {
                        this.decrementCraftingItems();
                        this.incrementOutput(currentRecipe.get().getRecipeOutput());
                        this.markDirty();

                        progress = 0;
                        smeltTime = 0;

                        world.setBlockState(pos, world.getBlockState(pos).with(SoulfireForgeBlock.BURNING, false));
                    } else {
                        //TODO make this client sided
                        this.world.playEvent(9001, pos, 0);
                        smeltTime++;
                        progress = Math.round(((float) smeltTime / (float) targetSmeltTime) * 32);
                    }
                } else {
                    smeltTime = 0;
                    progress = 0;
                }
            } else {
                smeltTime = 0;
                progress = 0;
            }
        } else if (world.getBlockState(pos).get(SoulfireForgeBlock.BURNING)) {
            for (int i = 0; i < 4; i++) {
                double x = (double) pos.getX() + 0.5D + (world.rand.nextDouble() - 0.5D) * 0.6;
                double y = (double) pos.getY() + 0.05D;
                double z = (double) pos.getZ() + 0.5D + (world.rand.nextDouble() - 0.5D) * 0.6;
                this.world.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
            }
        }
    }


    //Data Logic
    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        tag.put("Items", inventory.write());
        ItemStackHelper.saveAllItems(tag, copyFromInv(inventory));
        tag.putInt("Progress", progress);
        tag.putInt("SmeltTime", smeltTime);
        return tag;
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);

        NonNullList<ItemStack> tempInventoryList = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, tempInventoryList);
        copyToInv(tempInventoryList, inventory);

        this.progress = tag.getInt("Progress");
        this.smeltTime = tag.getInt("SmeltTime");
    }


    public boolean isRunning() {
        return smeltTime > 0;
    }

    public void finishInstantly() {
        if (!this.world.isRemote()) {
            this.smeltTime = targetSmeltTime;

            this.world.playEvent(9004, this.pos, 0);
        }
    }

    //Inventory Logic
    public Inventory getInventory() {
        return inventory;
    }

    private static NonNullList<ItemStack> copyFromInv(IInventory inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ret.set(i, inv.getStackInSlot(i));
        }
        return ret;
    }

    private static void copyToInv(NonNullList<ItemStack> src, IInventory dest) {
        Preconditions.checkArgument(src.size() == dest.getSizeInventory());
        for (int i = 0; i < src.size(); i++) {
            dest.setInventorySlotContents(i, src.get(i));
        }
    }

    private void decrementCraftingItems() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            stack.setCount(stack.getCount() - 1);
            if (stack.getCount() == 0) {
                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }
    }

    private void incrementOutput(ItemStack craftingResult) {
        if (inventory.getStackInSlot(9).getItem() == Items.AIR) {
            inventory.setInventorySlotContents(9, craftingResult);
        } else {
            inventory.getStackInSlot(9).setCount(inventory.getStackInSlot(9).getCount() + craftingResult.getCount());
        }
    }

    private boolean checkOutput(ItemStack toCompare) {
        return (inventory.getStackInSlot(9).getItem() == toCompare.getItem() && (inventory.getStackInSlot(9).getCount() + toCompare.getCount() <= inventory.getStackInSlot(9).getMaxStackSize())) || inventory.getStackInSlot(9) == ItemStack.EMPTY;
    }

}
