package com.glisco.conjuring.blocks.soul_weaver;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.blocks.BlackstonePedestalBlockEntity;
import com.glisco.conjuring.blocks.ConjuringBlocks;
import com.glisco.conjuring.blocks.RitualCore;
import com.glisco.owo.blockentity.LinearProcess;
import com.glisco.owo.blockentity.LinearProcessExecutor;
import com.glisco.owo.blockentity.SimpleSerializableBlockEntity;
import com.glisco.owo.ops.ItemOps;
import com.glisco.owo.particles.ClientParticles;
import com.glisco.owo.particles.ServerParticles;
import com.glisco.owo.util.VectorRandomUtils;
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
import net.minecraft.nbt.NbtElement;
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

public class SoulWeaverBlockEntity extends BlockEntity implements RitualCore, SimpleSerializableBlockEntity {

    public static final BlockEntityTicker<SoulWeaverBlockEntity> TICKER = (world1, pos1, state, blockEntity) -> blockEntity.ritualExecutor.tick();
    private static final LinearProcess<SoulWeaverBlockEntity> PROCESS = new LinearProcess<>(165);

    private final List<BlockPos> pedestals = new ArrayList<>();

    @NotNull
    private ItemStack item = ItemStack.EMPTY;
    private boolean lit = false;

    private final LinearProcessExecutor<SoulWeaverBlockEntity> ritualExecutor;
    private SoulWeaverRecipe cachedRecipe = null;

    public SoulWeaverBlockEntity(BlockPos pos, BlockState state) {
        super(ConjuringBlocks.Entities.SOUL_WEAVER, pos, state);
        this.ritualExecutor = PROCESS.createExecutor(this);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        PROCESS.configureExecutor(ritualExecutor, world.isClient);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        loadPedestals(tag, pedestals);
        ritualExecutor.readState(tag);

        this.item = ItemOps.get(tag, "Item");
        if (tag.contains("CachedRecipe", NbtElement.STRING_TYPE) && world != null) {
            this.cachedRecipe = (SoulWeaverRecipe) world.getRecipeManager().get(new Identifier(tag.getString("CachedRecipe"))).orElse(null);
        }

        lit = tag.getBoolean("Lit");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        savePedestals(tag, pedestals);
        ritualExecutor.writeState(tag);

        ItemOps.store(item, tag, "Item");
        if (cachedRecipe != null) tag.putString("CachedRecipe", cachedRecipe.getId().toString());

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

        if (!world.isClient())
            ServerParticles.issueEvent((ServerWorld) world, Vec3d.of(pos), Conjuring.id("unlink_weaver"), buffer -> buffer.writeBlockPos(pedestal));

        ritualExecutor.cancel();

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
            world.playSound(null, pos, Conjuring.WEEE, SoundCategory.BLOCKS, 1, 1);
        } else {
            world.playSound(null, pos, SoundEvents.ENTITY_EVOKER_CAST_SPELL, SoundCategory.BLOCKS, 1, 0);
        }

        ritualExecutor.begin();
        markDirty();
        return true;
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (!world.isClient()) sync();
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

        if (recipeOptional.isEmpty()) {
            this.cachedRecipe = null;
            return false;
        }

        this.cachedRecipe = recipeOptional.get();

        return true;
    }

    public void onBroken() {
        if (!item.isEmpty()) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1.25, pos.getZ(), item);
        }
        ritualExecutor.cancel();
    }

    public double getShakeScaleFactor() {
        return ritualExecutor.getProcessTick() > 40 ? ritualExecutor.getProcessTick() * 0.001 : 0;
    }

    public boolean isLit() {
        return lit;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
        markDirty();
    }

    public boolean isRunning() {
        return ritualExecutor.running();
    }

    static {
        PROCESS.addClientStep(0, 5, (executor, weaver) -> {
            for (BlockPos pos : weaver.pedestals) {
                if (!(weaver.world.getBlockEntity(pos) instanceof BlackstonePedestalBlockEntity)) continue;
                BlockPos pVector = pos.subtract(weaver.pos);

                ClientParticles.setVelocity(new Vec3d(pVector.getX() * 0.115, 0, pVector.getZ() * 0.115));
                ClientParticles.spawnWithMaxAge(ParticleTypes.SOUL_FIRE_FLAME, Vec3d.of(weaver.pos).add(0.5, 0.4, 0.5), 10);
            }
        });

        PROCESS.addClientEvent(10, (executor, weaver) -> {
            ClientParticles.setParticleCount(16);
            ClientParticles.persist();

            for (BlockPos pedestal : weaver.pedestals) {
                ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.LAVA, weaver.world, pedestal, new Vec3d(0.5, 1.25, 0.5), 0.1);
            }

            ClientParticles.reset();
        });

        PROCESS.addClientStep(10, 155, (executor, weaver) -> {
            final var world = weaver.world;
            final var pos = weaver.pos;

            if (executor.getProcessTick() % 2 == 0) {
                if (executor.getProcessTick() < 140) {
                    for (BlockPos pedestalPos : weaver.pedestals) {
                        if (!(world.getBlockEntity(pedestalPos) instanceof BlackstonePedestalBlockEntity)) continue;
                        if (!((BlackstonePedestalBlockEntity) world.getBlockEntity(pedestalPos)).isActive()) continue;

                        BlockPos p = pedestalPos.add(0, 1, 0);
                        BlockPos pVector = pedestalPos.subtract(weaver.pos);
                        Vec3d particleOrigin = VectorRandomUtils.getRandomOffset(world, new Vec3d(0.5, 0.3, 0.5).add(Vec3d.of(p)), 0.1);

                        ClientParticles.setVelocity(new Vec3d(pVector.getX() * -0.055f, -0.05f, pVector.getZ() * -0.05f));
                        ClientParticles.spawnWithMaxAge(ParticleTypes.SOUL, particleOrigin, 30);
                    }
                }

                if (executor.getProcessTick() > 33) {

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
        });

        PROCESS.whenFinishedClient((executor, weaver) -> {
            try {
                MinecraftClient.getInstance().getSoundManager().stopSounds(new Identifier("minecraft", "block.beacon.ambient"), SoundCategory.BLOCKS);
            } catch (Exception ignored) {
            }

            ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.GILDED_BLACKSTONE.getDefaultState());

            ClientParticles.setParticleCount(30);
            ClientParticles.spawnWithOffsetFromBlock(particle, weaver.world, weaver.pos, new Vec3d(0.5, 1.3, 0.5), 0.2);

            ClientParticles.setParticleCount(40);
            ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.LAVA, weaver.world, weaver.pos, new Vec3d(0.5, 1.25, 0.5), 0.15);
        });

        PROCESS.addServerEvent(10, (executor, weaver) -> {
            for (BlockPos pedestal : weaver.pedestals) {
                ((BlackstonePedestalBlockEntity) weaver.world.getBlockEntity(pedestal)).setActive(true);
                ((BlackstonePedestalBlockEntity) weaver.world.getBlockEntity(pedestal)).setItem(ItemStack.EMPTY);
            }
        });

        PROCESS.addServerEvent(15, (executor, weaver) -> {
            weaver.world.playSound(null, weaver.pos, SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 1.5f, 0.7f);
        });

        PROCESS.addServerEvent(127, (executor, weaver) -> {
            weaver.world.playSound(null, weaver.pos, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.BLOCKS, 0.15f, 2f);
        });

        PROCESS.whenFinishedServer((executor, weaver) -> {
            final var cachedRecipe = weaver.cachedRecipe;

            weaver.world.playSound(null, weaver.pos, SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.BLOCKS, 1, 0);

            for (BlockPos pedestal : weaver.pedestals) {
                ((BlackstonePedestalBlockEntity) weaver.world.getBlockEntity(pedestal)).setActive(false);
            }

            if (cachedRecipe != null) {
                if (cachedRecipe.transferTag) {
                    ItemStack output = cachedRecipe.getOutput();
                    output.setNbt(weaver.getItem().getOrCreateNbt());
                    weaver.setItem(output);
                } else {
                    weaver.setItem(cachedRecipe.getOutput());
                }
            }

            weaver.setLit(false);
            weaver.cachedRecipe = null;
        });

        PROCESS.onCancelledServer((executor, weaver) -> {
            final var world = weaver.world;
            final var pos = weaver.pos;

            for (BlockPos pedestal : weaver.pedestals) {
                ((BlackstonePedestalBlockEntity) world.getBlockEntity(pedestal)).setActive(false);
            }

            ((ServerWorld) world).getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 25, world.getRegistryKey(),
                    new ParticleS2CPacket(ParticleTypes.LARGE_SMOKE, false, pos.getX() + 0.5, pos.getY() + 1.15, pos.getZ() + 0.5, 0.15f, 0.15f, 0.15f, 0.05f, 15));

            ((ServerWorld) world).getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 25, world.getRegistryKey(),
                    new StopSoundS2CPacket(new Identifier("minecraft", "block.beacon.ambient"), SoundCategory.BLOCKS));

            world.playSound(null, pos, SoundEvents.ENTITY_WITHER_HURT, SoundCategory.BLOCKS, 1f, 0f);

            weaver.cachedRecipe = null;
            weaver.setLit(false);
            weaver.setItem(ItemStack.EMPTY);
        });

        PROCESS.finish();
    }
}
