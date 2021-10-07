package com.glisco.conjuring.blocks.soul_weaver;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.blocks.BlackstonePedestalBlockEntity;
import com.glisco.conjuring.blocks.RitualCore;
import com.glisco.owo.particles.ClientParticles;
import com.glisco.owo.particles.ServerParticles;
import com.glisco.owo.util.VectorRandomUtils;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SoulWeaverBlockEntity extends BlockEntity implements RitualCore, BlockEntityClientSerializable {

    public static final BlockEntityTicker<SoulWeaverBlockEntity> SERVER_TICKER = (world1, pos1, state, blockEntity) -> blockEntity.tickServer();
    public static final BlockEntityTicker<SoulWeaverBlockEntity> CLIENT_TICKER = (world1, pos1, state, blockEntity) -> blockEntity.tickClient();

    List<BlockPos> pedestals = new ArrayList<>();
    @NotNull
    private ItemStack item = ItemStack.EMPTY;
    private int ritualTick = 0;
    private boolean lit = false;

    SoulWeaverRecipe cachedRecipe = null;

    public SoulWeaverBlockEntity(BlockPos pos, BlockState state) {
        super(ConjuringCommon.SOUL_WEAVER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        loadPedestals(tag, pedestals);
        NbtCompound item = tag.getCompound("Item");
        if (!item.isEmpty()) {
            this.item = ItemStack.fromNbt(tag.getCompound("Item"));
        } else {
            this.item = ItemStack.EMPTY;
        }
        ritualTick = tag.getInt("RitualTick");
        lit = tag.getBoolean("Lit");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        savePedestals(tag, pedestals);
        NbtCompound itemTag = new NbtCompound();
        if (!item.isEmpty()) item.writeNbt(itemTag);
        tag.put("Item", itemTag);
        tag.putInt("RitualTick", ritualTick);
        tag.putBoolean("Lit", lit);
        return super.writeNbt(tag);
    }

    public boolean linkPedestal(BlockPos pedestal) {
        if (pedestals.size() >= 4) return false;

        if (!pedestals.contains(pedestal)) pedestals.add(pedestal);
        if (world.isClient) {
            ClientParticles.setParticleCount(25);
            ClientParticles.spawnLine(ParticleTypes.WITCH, world, Vec3d.of(pos).add(0.5, 0.4, 0.5), Vec3d.of(pedestal).add(0.5, 0.75, 0.5), 0);
        }
        this.markDirty();
        return true;
    }

    public boolean removePedestal(BlockPos pedestal, boolean pedestalActive) {
        boolean returnValue = pedestals.remove(pedestal);
        this.markDirty();

        if (!world.isClient()) {
            ServerParticles.issueEvent((ServerWorld) world, pos, new Identifier("conjuring", "unlink_weaver"), buffer -> {
                buffer.writeBlockPos(pedestal);
            });
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
    public boolean tryStartRitual(PlayerEntity player) {

        if (!verifyRecipe() || !isLit()) return false;

        if (world.random.nextDouble() < 0.014) {
            world.playSound(null, pos, ConjuringCommon.WEEE, SoundCategory.BLOCKS, 1, 1);
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

        if (!world.isClient()) {
            sync();
        }
    }

    @Override
    public void fromClientTag(NbtCompound compoundTag) {
        readNbt(compoundTag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound compoundTag) {
        return writeNbt(compoundTag);
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

    public void tickClient() {
        if (ritualTick > 0 && ritualTick < 5) {

            for (BlockPos pos : pedestals) {
                if (!(world.getBlockEntity(pos) instanceof BlackstonePedestalBlockEntity)) continue;
                BlockPos pVector = pos.subtract(this.pos);

                ClientParticles.setVelocity(new Vec3d(pVector.getX() * 0.115, 0, pVector.getZ() * 0.115));
                ClientParticles.spawnWithMaxAge(ParticleTypes.SOUL_FIRE_FLAME, Vec3d.of(this.pos).add(0.5, 0.4, 0.5), 10);
            }

        } else if (ritualTick == 10) {

            ClientParticles.setParticleCount(16);
            ClientParticles.persist();

            for (BlockPos pedestal : pedestals) {
                ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.LAVA, world, pedestal, new Vec3d(0.5, 1.25, 0.5), 0.1);
            }

            ClientParticles.reset();

        } else if (ritualTick > 10 && ritualTick < 165) {

            if (ritualTick % 2 == 0) {
                if (ritualTick < 140) {
                    for (BlockPos pos : pedestals) {
                        if (!(world.getBlockEntity(pos) instanceof BlackstonePedestalBlockEntity)) continue;
                        if (!((BlackstonePedestalBlockEntity) world.getBlockEntity(pos)).isActive()) continue;

                        BlockPos p = pos.add(0, 1, 0);
                        BlockPos pVector = pos.subtract(this.pos);
                        Vec3d particleOrigin = VectorRandomUtils.getRandomOffset(world, new Vec3d(0.5, 0.3, 0.5).add(Vec3d.of(p)), 0.1);

                        ClientParticles.setVelocity(new Vec3d(pVector.getX() * -0.055f, -0.05f, pVector.getZ() * -0.05f));
                        ClientParticles.spawnWithMaxAge(ParticleTypes.SOUL, particleOrigin, 30);
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
            try {
                MinecraftClient.getInstance().getSoundManager().stopSounds(new Identifier("minecraft", "block.beacon.ambient"), SoundCategory.BLOCKS);
            } catch (Exception ignored) {
            }

            ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.GILDED_BLACKSTONE.getDefaultState());

            ClientParticles.setParticleCount(30);
            ClientParticles.spawnWithOffsetFromBlock(particle, world, pos, new Vec3d(0.5, 1.3, 0.5), 0.2);

            ClientParticles.setParticleCount(40);
            ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.LAVA, world, pos, new Vec3d(0.5, 1.25, 0.5), 0.15);

            ritualTick = 0;
        }

        if (ritualTick > 0) ritualTick++;
    }

    public void tickServer() {
        if (ritualTick == 10) {
            for (BlockPos pedestal : pedestals) {
                ((BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal)).setActive(true);
                ((BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal)).setItem(ItemStack.EMPTY);
            }

        } else if (ritualTick > 10 && ritualTick < 165) {

            if (ritualTick == 15) {
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 1.5f, 0.7f);
            }

            if (ritualTick == 127) {
                world.playSound(null, pos, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.BLOCKS, 0.15f, 2f);
            }

        } else if (ritualTick > 165) {

            world.playSound(null, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.BLOCKS, 1, 0);

            for (BlockPos pedestal : pedestals) {
                ((BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal)).setActive(false);
            }

            if (cachedRecipe != null) {
                if (cachedRecipe.transferTag) {
                    ItemStack output = cachedRecipe.getOutput();
                    output.setNbt(getItem().getOrCreateNbt());
                    setItem(output);
                } else {
                    setItem(cachedRecipe.getOutput());
                }
            }

            setLit(false);
            cachedRecipe = null;
            ritualTick = 0;
        }

        if (ritualTick > 0) ritualTick++;
    }

    public void onBroken() {
        if (!item.isEmpty()) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1.25, pos.getZ(), item);
        }
        cancelRitual();
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
