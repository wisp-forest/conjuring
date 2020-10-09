package com.glisco.conjuring.blocks.soulfireForge;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.SoulfireForgeScreenHandler;
import com.glisco.conjuring.blocks.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class SoulfireForgeBlockEntity extends BlockEntity implements ImplementedInventory, SidedInventory, NamedScreenHandlerFactory, Tickable {

    private DefaultedList<ItemStack> items = DefaultedList.ofSize(10, ItemStack.EMPTY);

    private int progress;
    private int smeltTime;
    private int targetSmeltTime;

    public SoulfireForgeBlockEntity() {
        super(ConjuringCommon.SOULFIRE_FORGE_BLOCK_ENTITY);
    }

    private final PropertyDelegate properties = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return progress;
        }

        @Override
        public void set(int index, int value) {
            progress = value;
        }

        @Override
        public int size() {
            return 1;
        }
    };


    //Tick Logic
    @Override
    public void tick() {
        if (!this.world.isClient()) {
            SoulfireForgeRecipe currentRecipe = SoulfireForgeRecipeHelper.getMatchingRecipe(this);
            BlockPos below = this.pos.subtract(new Vec3i(0, 1, 0));

            if (currentRecipe != null && world.getBlockState(pos).get(SoulfireForgeBlock.BURNING)) {
                if (checkOutput(currentRecipe.getResult())) {
                    targetSmeltTime = currentRecipe.getSmeltTime();

                    if (smeltTime == targetSmeltTime) {
                        this.decrementCraftingItems();
                        this.incrementOutput(currentRecipe.getResult());
                        this.markDirty();

                        progress = 0;
                        smeltTime = 0;

                        world.setBlockState(pos, world.getBlockState(pos).with(SoulfireForgeBlock.BURNING, false));
                    } else {
                        //TODO make this client sided
                        this.world.syncWorldEvent(9001, pos, 0);
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
                double x = (double) pos.getX() + 0.5D + (world.random.nextDouble() - 0.5D) * 0.6;
                double y = (double) pos.getY() + 0.05D;
                double z = (double) pos.getZ() + 0.5D + (world.random.nextDouble() - 0.5D) * 0.6;
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
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, items);
        tag.putInt("Progress", progress);
        tag.putInt("SmeltTime", smeltTime);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        Inventories.fromTag(tag, items);
        this.progress = tag.getInt("Progress");
        this.smeltTime = tag.getInt("SmeltTime");
    }


    public boolean isRunning() {
        return smeltTime > 0;
    }

    public void finishInstantly() {
        if (!this.world.isClient()) {
            this.smeltTime = targetSmeltTime;

            this.world.syncWorldEvent(9004, this.pos, 0);
        }
    }

    //Inventory Logic
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new SoulfireForgeScreenHandler(syncId, inv, this, properties);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    private void decrementCraftingItems() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = items.get(i);
            stack.setCount(stack.getCount() - 1);
            if (stack.getCount() == 0) {
                items.set(i, ItemStack.EMPTY);
            }
        }
    }

    private void incrementOutput(ItemStack craftingResult) {
        if (items.get(9).getItem() == Items.AIR) {
            items.set(9, craftingResult);
        } else {
            items.get(9).setCount(items.get(9).getCount() + craftingResult.getCount());
        }
    }

    private boolean checkOutput(ItemStack toCompare) {
        return (items.get(9).getItem() == toCompare.getItem() && (items.get(9).getCount() + toCompare.getCount() < items.get(9).getMaxCount())) || items.get(9) == ItemStack.EMPTY;
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("Soulfire Forge");
    }
}
