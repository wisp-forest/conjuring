package com.glisco.conjuring.blocks.gem_tinkerer;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.blocks.ConjuringBlocks;
import com.glisco.conjuring.items.GemItem;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyTool;
import com.glisco.owo.blockentity.LinearProcess;
import com.glisco.owo.blockentity.LinearProcessExecutor;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GemTinkererBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    public static final BlockEntityTicker<GemTinkererBlockEntity> TICKER = (world1, pos1, state, blockEntity) -> blockEntity.executor.tick();

    private static final LinearProcess<GemTinkererBlockEntity> PROCESS = new LinearProcess<>(200);

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private final LinearProcessExecutor<GemTinkererBlockEntity> executor;
    private GemTinkererRecipe cachedRecipe;

    private boolean particlesShown = false;

    public GemTinkererBlockEntity(BlockPos pos, BlockState state) {
        super(ConjuringBlocks.Entities.GEM_TINKERER, pos, state);
        this.executor = PROCESS.createExecutor(this);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        PROCESS.configureExecutor(executor, world.isClient);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        inventory.clear();
        Inventories.readNbt(tag, inventory);
        executor.readState(tag);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        Inventories.writeNbt(tag, inventory);
        executor.writeState(tag);
        return super.writeNbt(tag);
    }

    public boolean verifyRecipe() {

        Inventory testInventory = new SimpleInventory(5);
        for (int i = 0; i < inventory.size(); i++) {
            testInventory.setStack(i, inventory.get(i));
        }

        Optional<GemTinkererRecipe> recipeOptional = world.getRecipeManager().getFirstMatch(GemTinkererRecipe.Type.INSTANCE, testInventory, world);
        if (recipeOptional.isEmpty()) return false;

        cachedRecipe = recipeOptional.get();
        return true;
    }

    public ActionResult onUse(PlayerEntity player) {

        if (verifyRecipe()) {
            if (world.isClient()) return ActionResult.SUCCESS;

            executor.begin();
            markDirty();

            return ActionResult.SUCCESS;
        }

        if (!(inventory.get(0).getItem() instanceof SoulAlloyTool)) return ActionResult.PASS;

        List<SoulAlloyTool.SoulAlloyModifier> presentModifiers = new ArrayList<>();

        for (int i = 1; i < inventory.size(); i++) {
            if (inventory.get(i).isEmpty()) continue;
            if (!(inventory.get(i).getItem() instanceof GemItem)) return ActionResult.PASS;

            final SoulAlloyTool.SoulAlloyModifier modifier = ((GemItem) inventory.get(i).getItem()).getModifier();
            presentModifiers.add(modifier);
        }

        if (presentModifiers.size() < 1) return ActionResult.PASS;
        if (!SoulAlloyTool.canAddModifiers(inventory.get(0), presentModifiers)) return ActionResult.PASS;

        if (!world.isClient()) {
            executor.begin();
            markDirty();
            Conjuring.GEM_TINKERING_CRITERION.trigger((ServerPlayerEntity) player);
        }

        return ActionResult.SUCCESS;
    }

    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    public boolean isRunning() {
        return executor.running();
    }

    public double getScalar() {
        return executor.getProcessTick() < 100 ? 1 + executor.getProcessTick() / 4d : 1 + ((200 - executor.getProcessTick()) / 4d);
    }

    public boolean particles() {
        if (!particlesShown && executor.getProcessTick() == 100) {
            particlesShown = true;
            return true;
        }

        return false;
    }

    public boolean isCraftingComplete() {
        return executor.getProcessTick() > 100;
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (!world.isClient()) sync();
    }

    static {
        PROCESS.whenFinishedClient((executor, tinkerer) -> executor.getTarget().particlesShown = false);

        PROCESS.addServerEvent(1, (executor, tinkerer) -> {
            tinkerer.world.playSound(null, tinkerer.pos, SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK, SoundCategory.BLOCKS, 0.25f, 0);
        });

        PROCESS.addServerEvent(100, (executor, tinkerer) -> {
            final var inventory = tinkerer.getInventory();

            tinkerer.world.playSound(null, tinkerer.pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1, 0);
            tinkerer.verifyRecipe();

            if (tinkerer.cachedRecipe == null) {
                for (int i = 1; i < inventory.size(); i++) {
                    if (!(inventory.get(i).getItem() instanceof GemItem gem)) continue;
                    if (!SoulAlloyTool.canAddModifier(inventory.get(0), gem.getModifier())) continue;

                    SoulAlloyTool.addModifier(inventory.get(0), ((GemItem) inventory.get(i).getItem()).getModifier());
                    inventory.set(i, ItemStack.EMPTY);
                }
            } else {
                inventory.clear();
                inventory.set(0, tinkerer.cachedRecipe.getOutput());
            }

            tinkerer.cachedRecipe = null;
            tinkerer.markDirty();
        });

        PROCESS.finish();
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        this.readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeNbt(tag);
    }
}
