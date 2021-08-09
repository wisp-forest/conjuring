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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ConjurerBlockEntity extends BlockEntity implements ImplementedInventory, NamedScreenHandlerFactory, SidedInventory, BlockEntityClientSerializable {

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(5, ItemStack.EMPTY);

    private final ConjurerLogic logic = new ConjurerLogic() {
        @Override
        public void sendStatus(World world, BlockPos pos, int i) {
            ConjurerBlockEntity.this.world.addSyncedBlockEvent(ConjurerBlockEntity.this.pos, ConjuringCommon.CONJURER_BLOCK, i, 0);
        }

        public World getWorld() {
            return ConjurerBlockEntity.this.world;
        }

        public BlockPos getPos() {
            return ConjurerBlockEntity.this.pos;
        }
    };

    public ConjurerBlockEntity(BlockPos pos, BlockState state) {
        super(ConjuringCommon.CONJURER_BLOCK_ENTITY, pos, state);
    }

    //TODO separate client and server ticks, like hard
    public static void tick(World world, BlockPos pos, BlockState state, ConjurerBlockEntity conjurer) {
        if (world.isClient) {
            conjurer.getLogic().clientTick(world, pos);
        } else {
            conjurer.getLogic().serverTick((ServerWorld) world, pos);
        }
    }

    public ConjurerLogic getLogic() {
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

        if (this.world == null) return;
        this.world.setBlockState(pos, this.world.getBlockState(pos).with(ConjurerBlock.ACTIVE, active));
    }

    public void setRequiresPlayer(boolean requiresPlayer) {
        this.logic.setRequiresPlayer(requiresPlayer);
    }

    //NBT Logic
    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        Inventories.readNbt(tag, items);
        this.logic.readNbt(world, pos, tag);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        Inventories.writeNbt(tag, items);
        this.logic.writeNbt(world, pos, tag);
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        this.readNbt(tag);
    }

    public NbtCompound toClientTag(NbtCompound tag) {
        tag = this.writeNbt(tag);
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
