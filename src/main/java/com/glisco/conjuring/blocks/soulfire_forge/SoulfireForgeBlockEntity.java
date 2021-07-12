package com.glisco.conjuring.blocks.soulfire_forge;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.SoulfireForgeScreenHandler;
import com.glisco.conjuring.blocks.ImplementedInventory;
import com.glisco.owo.ops.ItemOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;

public class SoulfireForgeBlockEntity extends BlockEntity implements ImplementedInventory, SidedInventory, NamedScreenHandlerFactory {

    private DefaultedList<ItemStack> items = DefaultedList.ofSize(10, ItemStack.EMPTY);

    private final int[] SIDE_AND_TOP_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    private final int[] BOTTOM_SLOT = new int[]{9};

    private int progress;
    private int smeltTime;
    private int targetSmeltTime;

    public SoulfireForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ConjuringCommon.SOULFIRE_FORGE_BLOCK_ENTITY, pos, state);
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


    public static void ticker(World world, BlockPos pos, BlockState state, SoulfireForgeBlockEntity forge){
        forge.tick();
    }

    //Tick Logic
    public void tick() {
        if (!this.world.isClient()) {

            Optional<SoulfireForgeRecipe> currentRecipe = world.getRecipeManager().getFirstMatch(SoulfireForgeRecipe.Type.INSTANCE, this, world);

            if (currentRecipe.isPresent() && getCachedState().get(SoulfireForgeBlock.BURNING)) {
                if (items.get(9).isEmpty() || ItemOps.canStack(items.get(9), currentRecipe.get().getOutput())) {
                    targetSmeltTime = currentRecipe.get().getSmeltTime();

                    if (smeltTime == targetSmeltTime) {
                        this.decrementCraftingItems();
                        this.incrementOutput(currentRecipe.get().getOutput());
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

                    world.updateComparators(pos, ConjuringCommon.SOULFIRE_FORGE_BLOCK);

                } else {
                    smeltTime = 0;
                    progress = 0;
                }
            } else {
                smeltTime = 0;
                progress = 0;
            }
        } else if (getCachedState().get(SoulfireForgeBlock.BURNING)) {
            for (int i = 0; i < 4; i++) {
                double x = (double) pos.getX() + 0.5D + (world.random.nextDouble() - 0.5D) * 0.3;
                double y = (double) pos.getY() + 0.6D;
                double z = (double) pos.getZ() + 0.5D + (world.random.nextDouble() - 0.5D) * 0.3;
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
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        Inventories.writeNbt(tag, items);
        tag.putInt("Progress", progress);
        tag.putInt("SmeltTime", smeltTime);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        Inventories.readNbt(tag, items);
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
        return side == Direction.DOWN ? BOTTOM_SLOT : SIDE_AND_TOP_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return slot != 9 && items.get(slot).isEmpty();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 9;
    }

    private void decrementCraftingItems() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = items.get(i);
            if (!ItemOps.emptyAwareDecrement(stack)) items.set(i, ItemStack.EMPTY);
        }
    }

    private void incrementOutput(ItemStack craftingResult) {
        if (items.get(9).isEmpty()) {
            items.set(9, craftingResult);
        } else {
            items.get(9).increment(craftingResult.getCount());
        }
    }

    public int getProgress() {
        return progress;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("conjuring.gui.soulfire_forge");
    }
}
