package com.glisco.conjuringforgery.blocks.soul_weaver;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.blocks.BlackstonePedestalTileEntity;
import com.glisco.conjuringforgery.blocks.RitualCore;
import com.glisco.owo.ServerParticles;
import com.glisco.owo.VectorRandomUtils;
import com.glisco.owo.client.ClientParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.network.play.server.SStopSoundPacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SoulWeaverTileEntity extends TileEntity implements RitualCore, ITickableTileEntity {

    List<BlockPos> pedestals = new ArrayList<>();
    private ItemStack item = ItemStack.EMPTY;
    private int ritualTick = 0;
    private boolean lit = false;

    SoulWeaverRecipe cachedRecipe = null;

    public SoulWeaverTileEntity() {
        super(ConjuringForgery.SOUL_WEAVER_TILE.get());
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        loadPedestals(tag, pedestals);
        CompoundNBT item = tag.getCompound("Item");
        if (!item.isEmpty()) {
            this.item = ItemStack.read(tag.getCompound("Item"));
        } else {
            this.item = ItemStack.EMPTY;
        }
        ritualTick = tag.getInt("RitualTick");
        lit = tag.getBoolean("Lit");
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        savePedestals(tag, pedestals);
        CompoundNBT itemTag = new CompoundNBT();
        if (!item.isEmpty()) item.write(itemTag);
        tag.put("Item", itemTag);
        tag.putInt("RitualTick", ritualTick);
        tag.putBoolean("Lit", lit);
        return super.write(tag);
    }

    public boolean linkPedestal(BlockPos pedestal) {
        if (pedestals.size() >= 4) return false;

        if (!pedestals.contains(pedestal)) pedestals.add(pedestal);
        if (world.isRemote) {
            ClientParticles.setParticleCount(25);
            ClientParticles.spawnLine(ParticleTypes.WITCH, world, Vector3d.copy(pos).add(0.5, 0.4, 0.5), Vector3d.copy(pedestal).add(0.5, 0.75, 0.5), 0);
        }
        this.markDirty();
        return true;
    }

    public boolean removePedestal(BlockPos pedestal, boolean pedestalActive) {
        boolean returnValue = pedestals.remove(pedestal);
        this.markDirty();

        if (!world.isRemote()) {
            ServerParticles.issueEvent((ServerWorld) world, pos, new ResourceLocation("conjuring", "unlink_weaver"), buffer -> {
                buffer.writeBlockPos(pedestal);
            });
        }

        cancelRitual();

        return returnValue;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack stack) {
        item = stack;
        markDirty();
    }

    @Override
    public List<BlockPos> getPedestalPositions() {
        return pedestals;
    }

    @Override
    public boolean tryStartRitual(PlayerEntity player) {

        if (!verifyRecipe() || !isLit()) return false;

        if (world.rand.nextDouble() < 0.014) {
            world.playSound(null, pos, ConjuringForgery.WEEE.get(), SoundCategory.BLOCKS, 1, 1);
        } else {
            world.playSound(null, pos, SoundEvents.ENTITY_EVOKER_CAST_SPELL, SoundCategory.BLOCKS, 1, 0);
        }
        ritualTick = 1;
        markDirty();
        return true;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(null, pkt.getNbtCompound());
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

    public boolean verifyRecipe() {

        if (item.isEmpty()) return false;

        Inventory testInventory = new Inventory(5);
        testInventory.setInventorySlotContents(0, item);

        int index = 1;
        for (BlockPos pedestal : pedestals) {
            BlackstonePedestalTileEntity entity = (BlackstonePedestalTileEntity) world.getTileEntity(pedestal);
            if (entity.getItem().isEmpty()) return false;
            testInventory.setInventorySlotContents(index, entity.getItem());
            index++;
        }

        Optional<SoulWeaverRecipe> recipeOptional = world.getRecipeManager().getRecipe(SoulWeaverRecipe.Type.INSTANCE, testInventory, world);

        if (!recipeOptional.isPresent()) {
            this.cachedRecipe = null;
            return false;
        }

        this.cachedRecipe = recipeOptional.get();

        return true;
    }

    @Override
    public void tick() {
        if (ritualTick > 0 && ritualTick < 5) {

            if (world.isRemote) {
                for (BlockPos pos : pedestals) {
                    if (!(world.getTileEntity(pos) instanceof BlackstonePedestalTileEntity)) continue;
                    BlockPos pVector = pos.subtract(this.pos);

                    ClientParticles.setVelocity(new Vector3d(pVector.getX() * 0.115, 0, pVector.getZ() * 0.115));
                    ClientParticles.spawnWithMaxAge(ParticleTypes.SOUL_FIRE_FLAME, world, Vector3d.copy(this.pos).add(0.5, 0.4, 0.5), 10);
                }

            }

        } else if (ritualTick == 10) {

            if (world.isRemote) {
                ClientParticles.setParticleCount(16);
                ClientParticles.persist();
            }

            for (BlockPos pedestal : pedestals) {
                if (world.isRemote) {
                    ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.LAVA, world, pedestal, new Vector3d(0.5, 1.25, 0.5), 0.1);
                } else {
                    ((BlackstonePedestalTileEntity) world.getTileEntity(pedestal)).setActive(true);
                    ((BlackstonePedestalTileEntity) world.getTileEntity(pedestal)).setItem(ItemStack.EMPTY);
                }
            }

            if (world.isRemote) {
                ClientParticles.reset();
            }
        } else if (ritualTick > 10 && ritualTick < 165) {

            if (ritualTick == 15) {
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 1.5f, 0.7f);
            }

            if (ritualTick == 127) {
                world.playSound(null, pos, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.BLOCKS, 0.15f, 2f);
            }

            if (world.isRemote && ritualTick % 2 == 0) {
                if (ritualTick < 140) {
                    for (BlockPos pos : pedestals) {
                        if (!(world.getTileEntity(pos) instanceof BlackstonePedestalTileEntity)) continue;
                        if (!((BlackstonePedestalTileEntity) world.getTileEntity(pos)).isActive()) continue;

                        BlockPos p = pos.add(0, 1, 0);
                        BlockPos pVector = pos.subtract(this.pos);
                        Vector3d particleOrigin = VectorRandomUtils.getRandomOffset(world, new Vector3d(0.5, 0.3, 0.5).add(Vector3d.copy(p)), 0.1);

                        ClientParticles.setVelocity(new Vector3d(pVector.getX() * -0.055f, -0.05f, pVector.getZ() * -0.05f));
                        ClientParticles.spawnWithMaxAge(ParticleTypes.SOUL, world, particleOrigin, 30);
                    }
                }

                if (ritualTick > 33) {

                    ClientParticles.setParticleCount(2);
                    ClientParticles.spawnCenteredOnBlock(ParticleTypes.SOUL_FIRE_FLAME, world, pos.add(0, 1, 0), 0.45f);

                    ClientParticles.setVelocity(new Vector3d(0.05, 0.03, 0));
                    ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.SOUL_FIRE_FLAME, world, pos, new Vector3d(0.5, 0.3, 0.5), 0.1);

                    ClientParticles.setVelocity(new Vector3d(-0.05, 0.03, 0));
                    ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.SOUL_FIRE_FLAME, world, pos, new Vector3d(0.5, 0.3, 0.5), 0.1);

                    ClientParticles.setVelocity(new Vector3d(0, 0.03, 0.05));
                    ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.SOUL_FIRE_FLAME, world, pos, new Vector3d(0.5, 0.3, 0.5), 0.1);

                    ClientParticles.setVelocity(new Vector3d(0, 0.03, -0.05));
                    ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.SOUL_FIRE_FLAME, world, pos, new Vector3d(0.5, 0.3, 0.5), 0.1);
                }
            }
        } else if (ritualTick > 165) {
            if (!world.isRemote) {

                world.playSound(null, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.BLOCKS, 1, 0);

                for (BlockPos pedestal : pedestals) {
                    ((BlackstonePedestalTileEntity) world.getTileEntity(pedestal)).setActive(false);
                }

                if (cachedRecipe != null) {
                    if (cachedRecipe.transferTag) {
                        ItemStack output = cachedRecipe.getRecipeOutput();
                        output.setTag(getItem().getOrCreateTag());
                        setItem(output);
                    } else {
                        setItem(cachedRecipe.getRecipeOutput());
                    }
                }

                setLit(false);
                cachedRecipe = null;
            } else {
                try {
                    Minecraft.getInstance().getSoundHandler().stop(new ResourceLocation("minecraft", "block.beacon.ambient"), SoundCategory.BLOCKS);
                } catch (Exception ignored) {
                }

                IParticleData particle = new BlockParticleData(ParticleTypes.BLOCK, Blocks.GILDED_BLACKSTONE.getDefaultState());

                ClientParticles.setParticleCount(30);
                ClientParticles.spawnWithOffsetFromBlock(particle, world, pos, new Vector3d(0.5, 1.3, 0.5), 0.2);

                ClientParticles.setParticleCount(40);
                ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.LAVA, world, pos, new Vector3d(0.5, 1.25, 0.5), 0.15);
            }
            ritualTick = 0;
        }

        if (ritualTick > 0) ritualTick++;
    }

    public void onBroken() {
        if (!item.isEmpty()){
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY() + 1.25, pos.getZ(), item);
        }
        cancelRitual();
    }

    public void cancelRitual() {

        if (ritualTick == 0) return;

        for (BlockPos pedestal : pedestals) {
            ((BlackstonePedestalTileEntity) world.getTileEntity(pedestal)).setActive(false);
        }

        ((ServerWorld) world).getServer().getPlayerList().sendToAllNearExcept(null, pos.getX(), pos.getY(), pos.getZ(), 25, world.getDimensionKey(),
                new SSpawnParticlePacket(ParticleTypes.LARGE_SMOKE, false, pos.getX() + 0.5, pos.getY() + 1.15, pos.getZ() + 0.5, 0.15f, 0.15f, 0.15f, 0.05f, 15));

        ((ServerWorld) world).getServer().getPlayerList().sendToAllNearExcept(null, pos.getX(), pos.getY(), pos.getZ(), 25, world.getDimensionKey(),
                new SStopSoundPacket(new ResourceLocation("minecraft", "block.beacon.ambient"), SoundCategory.BLOCKS));

        world.playSound(null, pos, SoundEvents.ENTITY_WITHER_HURT, SoundCategory.BLOCKS, 1f, 0f);

        ritualTick = 0;
        cachedRecipe = null;
        setLit(false);
        setItem(ItemStack.EMPTY);
    }

    public double getShakeScaleFactor() {
        return ritualTick > 40 ? ritualTick * 0.001 : 0;
    }

    public boolean isLit() {
        return lit;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
        markDirty();
    }

    public boolean isRunning() {
        return ritualTick > 0;
    }
}
