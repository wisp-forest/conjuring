package com.glisco.conjuring.blocks.soul_weaver;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.blocks.BlackstonePedestalBlockEntity;
import com.glisco.conjuring.blocks.RitualCore;
import com.glisco.owo.VectorRandomUtils;
import com.glisco.owo.client.ClientParticles;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SoulWeaverBlockEntity extends BlockEntity implements RitualCore, BlockEntityClientSerializable, Tickable {

    List<BlockPos> pedestals = new ArrayList<>();
    @NotNull
    private ItemStack item = ItemStack.EMPTY;
    private int ritualTick = 0;
    private boolean lit = false;

    SoulWeaverRecipe cachedRecipe = null;

    public SoulWeaverBlockEntity() {
        super(ConjuringCommon.SOUL_WEAVER_BLOCK_ENTITY);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        loadPedestals(tag, pedestals);
        CompoundTag item = tag.getCompound("Item");
        if (!item.isEmpty()) {
            this.item = ItemStack.fromTag(tag.getCompound("Item"));
        } else {
            this.item = ItemStack.EMPTY;
        }
        ritualTick = tag.getInt("RitualTick");
        lit = tag.getBoolean("Lit");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        savePedestals(tag, pedestals);
        CompoundTag itemTag = new CompoundTag();
        if (!item.isEmpty()) item.toTag(itemTag);
        tag.put("Item", itemTag);
        tag.putInt("RitualTick", ritualTick);
        tag.putBoolean("Lit", lit);
        return super.toTag(tag);
    }

    //TODO make this good
    public boolean linkPedestal(BlockPos pedestal) {
        if (pedestals.size() >= 4) return false;

        if (!pedestals.contains(pedestal)) pedestals.add(pedestal);
        if (world.isClient) {
            BlockPos offset = pedestal.subtract(pos);

            float offsetX = 0.5f + offset.getX() / 8f;
            float offsetY = 0.35f;
            float offsetZ = 0.5f + offset.getZ() / 8f;

            ClientParticles.setParticleCount(20);
            ClientParticles.spawnPrecise(ParticleTypes.WITCH, world, new Vec3d(offsetX, offsetY, offsetZ).add(Vec3d.of(pos)), offset.getZ() / 12d, 0.1f, offset.getX() / 12d);
        }
        this.markDirty();
        return true;
    }

    public boolean removePedestal(BlockPos pedestal, boolean pedestalActive) {
        boolean returnValue = pedestals.remove(pedestal);
        this.markDirty();

        BlockPos offset = pedestal.subtract(pos);
        if (offset.getX() != 0) {
            world.syncWorldEvent(9010, pos, offset.getX());
        } else {
            world.syncWorldEvent(9011, pos, offset.getZ());
        }

        cancelRitual();

        return returnValue;
    }

    @NotNull
    public ItemStack getItem() {
        return item;
    }

    public void setItem(@NotNull ItemStack stack) {
        item = stack;
        markDirty();
    }

    @Override
    public List<BlockPos> getPedestalPositions() {
        return pedestals;
    }

    @Override
    public boolean tryStartRitual() {

        if (!verifyRecipe() || !isLit()) return false;

        world.playSound(null, pos, SoundEvents.ENTITY_EVOKER_CAST_SPELL, SoundCategory.BLOCKS, 1, 0);
        ritualTick = 1;
        markDirty();
        return true;
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (!world.isClient()) {
            sync();
        }
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(getCachedState(), compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }

    public boolean verifyRecipe() {

        if (item.isEmpty()) return false;

        Inventory testInventory = new SimpleInventory(5);
        testInventory.setStack(0, item);

        int index = 1;
        for (BlockPos pedestal : pedestals) {
            BlackstonePedestalBlockEntity entity = (BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal);
            if (entity.getItem().isEmpty()) return false;
            testInventory.setStack(index, entity.getItem());
            index++;
        }

        Optional<SoulWeaverRecipe> recipeOptional = world.getRecipeManager().getFirstMatch(SoulWeaverRecipe.Type.INSTANCE, testInventory, world);

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

            if (world.isClient) {
                for (BlockPos pos : pedestals) {
                    if (!(world.getBlockEntity(pos) instanceof BlackstonePedestalBlockEntity)) continue;
                    BlockPos pVector = pos.subtract(this.pos);

                    ClientParticles.setVelocity(new Vec3d(pVector.getX() * 0.115, 0, pVector.getZ() * 0.115));
                    ClientParticles.spawnWithMaxAge(ParticleTypes.SOUL_FIRE_FLAME, world, Vec3d.of(this.pos).add(0.5, 0.4, 0.5), 10);
                }

            }

        } else if (ritualTick == 10) {

            ClientParticles.setParticleCount(8);
            ClientParticles.persist();

            for (BlockPos pedestal : pedestals) {
                if (world.isClient) {
                    ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.LAVA, world, pedestal, new Vec3d(0.5, 1.25, 0.5), 0.1);
                } else {
                    ((BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal)).setActive(true);
                    ((BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal)).setItem(ItemStack.EMPTY);
                }
            }

            ClientParticles.reset();
        } else if (ritualTick > 10 && ritualTick < 165) {

            if (ritualTick == 15) {
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 1.5f, 0.7f);
            }

            if (ritualTick == 127) {
                world.playSound(null, pos, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.BLOCKS, 0.15f, 2f);
            }

            if (world.isClient && ritualTick % 2 == 0) {
                if (ritualTick < 140) {
                    for (BlockPos pos : pedestals) {
                        if (!(world.getBlockEntity(pos) instanceof BlackstonePedestalBlockEntity)) continue;
                        if (!((BlackstonePedestalBlockEntity) world.getBlockEntity(pos)).isActive()) continue;

                        BlockPos p = pos.add(0, 1, 0);
                        BlockPos pVector = pos.subtract(this.pos);
                        Vec3d particleOrigin = VectorRandomUtils.getRandomOffset(world, new Vec3d(0.5, 0.3, 0.5).add(Vec3d.of(p)), 0.1);

                        ClientParticles.setVelocity(new Vec3d(pVector.getX() * -0.055f, -0.05f, pVector.getZ() * -0.05f));
                        ClientParticles.spawnWithMaxAge(ParticleTypes.SOUL, world, particleOrigin, 30);
                    }
                }

                if (ritualTick > 33) {

                    ClientParticles.setParticleCount(2);
                    ClientParticles.spawnCenteredOnBlock(ParticleTypes.SOUL_FIRE_FLAME, world, pos.add(0, 1, 0), 0.45f);

                    ClientParticles.setVelocity(new Vec3d(0.05, 0.03, 0));
                    ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.SOUL_FIRE_FLAME, world, pos, new Vec3d(0.5, 0.3, 0.5), 0.1);

                    ClientParticles.setVelocity(new Vec3d(-0.05, 0.03, 0));
                    ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.SOUL_FIRE_FLAME, world, pos, new Vec3d(0.5, 0.3, 0.5), 0.1);

                    ClientParticles.setVelocity(new Vec3d(0, 0.03, 0.05));
                    ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.SOUL_FIRE_FLAME, world, pos, new Vec3d(0.5, 0.3, 0.5), 0.1);

                    ClientParticles.setVelocity(new Vec3d(0, 0.03, -0.05));
                    ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.SOUL_FIRE_FLAME, world, pos, new Vec3d(0.5, 0.3, 0.5), 0.1);
                }
            }
        } else if (ritualTick > 165) {
            if (!world.isClient) {

                world.playSound(null, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.BLOCKS, 1, 0);
                try {
                    MinecraftClient.getInstance().getSoundManager().stopSounds(new Identifier("minecraft", "block.beacon.ambient"), SoundCategory.BLOCKS);
                } catch (Exception ignored) {
                }

                for (BlockPos pedestal : pedestals) {
                    ((BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal)).setActive(false);
                }

                if (cachedRecipe != null) {
                    if (cachedRecipe.transferTag) {
                        ItemStack output = cachedRecipe.getOutput();
                        output.setTag(getItem().getOrCreateTag());
                        setItem(output);
                    } else {
                        setItem(cachedRecipe.getOutput());
                    }
                }

                setLit(false);
                cachedRecipe = null;
            } else {
                ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.GILDED_BLACKSTONE.getDefaultState());

                ClientParticles.setParticleCount(30);
                ClientParticles.spawnWithOffsetFromBlock(particle, world, pos, new Vec3d(0.5, 1.3, 0.5), 0.2);

                ClientParticles.setParticleCount(40);
                ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.LAVA, world, pos, new Vec3d(0.5, 1.25, 0.5), 0.15);
            }
            ritualTick = 0;
        }

        if (ritualTick > 0) ritualTick++;
    }

    public void cancelRitual() {

        if (ritualTick == 0) return;

        for (BlockPos pedestal : pedestals) {
            ((BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal)).setActive(false);
        }

        ((ServerWorld) world).getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 25, world.getRegistryKey(),
                new ParticleS2CPacket(ParticleTypes.LARGE_SMOKE, false, pos.getX() + 0.5, pos.getY() + 1.15, pos.getZ() + 0.5, 0.15f, 0.15f, 0.15f, 0.05f, 15));

        ((ServerWorld) world).getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 25, world.getRegistryKey(),
                new StopSoundS2CPacket(new Identifier("minecraft", "block.beacon.ambient"), SoundCategory.BLOCKS));

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
