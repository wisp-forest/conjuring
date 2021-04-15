package com.glisco.conjuring.blocks.conjurer;

import com.glisco.conjuring.ConjurerScreenHandler;
import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.blocks.ImplementedInventory;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.World;

public class ConjurerBlockEntity extends BlockEntity implements Tickable, ImplementedInventory, NamedScreenHandlerFactory, SidedInventory, BlockEntityClientSerializable {

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(5, ItemStack.EMPTY);

    private final ModifiedMobSpawnerLogic logic = new ModifiedMobSpawnerLogic() {
        public void sendStatus(int status) {
            ConjurerBlockEntity.this.world.addSyncedBlockEvent(ConjurerBlockEntity.this.pos, ConjuringCommon.CONJURER_BLOCK, status, 0);
        }

        public World getWorld() {
            return ConjurerBlockEntity.this.world;
        }

        public BlockPos getPos() {
            return ConjurerBlockEntity.this.pos;
        }

        public void setSpawnEntry(MobSpawnerEntry spawnEntry) {
            super.setSpawnEntry(spawnEntry);
            if (this.getWorld() != null) {
                BlockState blockState = this.getWorld().getBlockState(this.getPos());
                this.getWorld().updateListeners(ConjurerBlockEntity.this.pos, blockState, blockState, 4);
            }

        }
    };

    public ConjurerBlockEntity() {
        super(ConjuringCommon.CONJURER_BLOCK_ENTITY);
    }

    //Conjurer Logic
    public void tick() {
        this.logic.update();
    }

    public ModifiedMobSpawnerLogic getLogic() {
        return logic;
    }

    @Override
    public void markDirty() {
        ConjurerHelper.updateConjurerProperties(this);

        super.markDirty();
        if (world instanceof ServerWorld) {
            this.sync();
        }
    }

    public boolean isActive() {
        return logic.isActive();
    }

    public void setActive(boolean active) {
        this.logic.setActive(active);
    }

    public void setRequiresPlayer(boolean requiresPlayer) {
        this.logic.setRequiresPlayer(requiresPlayer);
    }

    //NBT Logic
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.logic.fromTag(tag);
        Inventories.fromTag(tag, items);
    }

    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        this.logic.toTag(tag);
        Inventories.toTag(tag, items);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(null, tag);
    }

    public CompoundTag toClientTag(CompoundTag tag) {
        tag = this.toTag(tag);
        tag.remove("SpawnPotentials");
        tag.remove("Items");
        return tag;
    }


    //Interface Logic
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ConjurerScreenHandler(syncId, playerInventory, this);
    }


    //Inventory Logic
    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("");
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
}
