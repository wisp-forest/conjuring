package com.glisco.conjuring.blocks.conjurer;

import com.glisco.conjuring.util.ConjuringParticleEvents;
import io.wispforest.owo.particles.ServerParticles;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

/**
 * Modified version of Minecraft's MobSpawnerLogic
 * <p>
 * Used to provide control over the logics properties,
 * to make it usable for a player-modifiable spawner
 */
public abstract class ConjurerLogic {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int field_30951 = 1;
    private int spawnDelay = 20;
    private DataPool<MobSpawnerEntry> spawnPotentials = DataPool.<MobSpawnerEntry>empty();
    private MobSpawnerEntry spawnEntry = new MobSpawnerEntry();
    private double field_9161;
    private double field_9159;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    @Nullable
    private Entity renderedEntity;
    private int maxNearbyEntities = 6;
    private int requiredPlayerRange = 16;
    private int spawnRange = 4;
    private final Random random = new Random();
    private boolean requiresPlayer = true;
    private boolean active = false;

    public void setEntityId(EntityType<?> type) {
        this.spawnEntry.getNbt().putString("id", Registry.ENTITY_TYPE.getId(type).toString());
    }

    public boolean isPlayerInRange(World world, BlockPos pos) {
        return world.getReceivedRedstonePower(pos) == 0 && (!requiresPlayer || world.isPlayerInRange((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, (double) this.requiredPlayerRange));
    }

    public void clientTick(World world, BlockPos pos) {
        // Check if active
        if (!this.isPlayerInRange(world, pos) || !active) {
            this.field_9159 = this.field_9161;
        } else {
            double $$2 = (double) pos.getX() + world.random.nextDouble();
            double $$3 = (double) pos.getY() + world.random.nextDouble();
            double $$4 = (double) pos.getZ() + world.random.nextDouble();

            //These particles have been changed to reflect the custom spawner version
            world.addParticle(ParticleTypes.ENCHANTED_HIT, $$2, $$3, $$4, 0.0D, 1.0D, 0.0D);
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, $$2, $$3, $$4, 0.0D, 0.0D, 0.0D);

            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            }

            this.field_9159 = this.field_9161;
            this.field_9161 = (this.field_9161 + (double) (1000.0F / ((float) this.spawnDelay + 200.0F))) % 360.0;
        }

    }

    public void serverTick(ServerWorld world, BlockPos pos) {
        // Check if active
        if (this.isPlayerInRange(world, pos) && active) {
            if (this.spawnDelay == -1) {
                this.updateSpawns(world, pos);
            }

            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            } else {
                boolean $$2 = false;

                for (int $$3 = 0; $$3 < this.spawnCount; ++$$3) {
                    NbtCompound $$4 = this.spawnEntry.getNbt();
                    Optional<EntityType<?>> $$5 = EntityType.fromNbt($$4);
                    if ($$5.isEmpty()) {
                        this.updateSpawns(world, pos);
                        return;
                    }

                    NbtList $$6 = $$4.getList("Pos", 6);
                    int $$7 = $$6.size();
                    double $$8 = $$7 >= 1 ? $$6.getDouble(0) : (double) pos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double) this.spawnRange + 0.5;
                    double $$9 = $$7 >= 2 ? $$6.getDouble(1) : (double) (pos.getY() + world.random.nextInt(3) - 1);
                    double $$10 = $$7 >= 3 ? $$6.getDouble(2) : (double) pos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double) this.spawnRange + 0.5;
                    if (world.isSpaceEmpty(((EntityType) $$5.get()).createSimpleBoundingBox($$8, $$9, $$10))) {
                        BlockPos $$11 = new BlockPos($$8, $$9, $$10);
                        if (this.spawnEntry.getCustomSpawnRules().isPresent()) {
                            if (!((EntityType) $$5.get()).getSpawnGroup().isPeaceful() && world.getDifficulty() == Difficulty.PEACEFUL) {
                                continue;
                            }

                            MobSpawnerEntry.CustomSpawnRules $$12 = (MobSpawnerEntry.CustomSpawnRules) this.spawnEntry.getCustomSpawnRules().get();
                            if (!$$12.blockLightLimit().contains(world.getLightLevel(LightType.BLOCK, $$11)) || !$$12.skyLightLimit().contains(world.getLightLevel(LightType.SKY, $$11))) {
                                continue;
                            }
                        } else if (!SpawnRestriction.canSpawn((EntityType) $$5.get(), world, SpawnReason.SPAWNER, $$11, world.getRandom())) {
                            continue;
                        }

                        Entity $$13 = EntityType.loadEntityWithPassengers($$4, world, $$3x -> {
                            $$3x.refreshPositionAndAngles($$8, $$9, $$10, $$3x.getYaw(), $$3x.getPitch());
                            return $$3x;
                        });
                        if ($$13 == null) {
                            this.updateSpawns(world, pos);
                            return;
                        }

                        int $$14 = world.getNonSpectatingEntities($$13.getClass(), (new Box((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), (double) (pos.getX() + 1), (double) (pos.getY() + 1), (double) (pos.getZ() + 1))).expand((double) this.spawnRange)).size();
                        if ($$14 >= this.maxNearbyEntities) {
                            this.updateSpawns(world, pos);
                            return;
                        }

                        $$13.refreshPositionAndAngles($$13.getX(), $$13.getY(), $$13.getZ(), world.random.nextFloat() * 360.0F, 0.0F);
                        if ($$13 instanceof MobEntity) {
                            MobEntity $$15 = (MobEntity) $$13;
                            if (this.spawnEntry.getCustomSpawnRules().isEmpty() && !$$15.canSpawn(world, SpawnReason.SPAWNER) || !$$15.canSpawn(world)) {
                                continue;
                            }

                            if (this.spawnEntry.getNbt().getSize() == 1 && this.spawnEntry.getNbt().contains("id", 8)) {
                                ((MobEntity) $$13).initialize(world, world.getLocalDifficulty($$13.getBlockPos()), SpawnReason.SPAWNER, (EntityData) null, (NbtCompound) null);
                            }
                        }

                        if (!world.spawnNewEntityAndPassengers($$13)) {
                            this.updateSpawns(world, pos);
                            return;
                        }

                        //This worldEvent has different ID to work in conjunction with a mixin in the Client's WorldRenderer
                        ServerParticles.issueEvent(world, Vec3d.of(pos), ConjuringParticleEvents.CONJURER_SUMMON);
                        if ($$13 instanceof MobEntity) {
                            ((MobEntity) $$13).playSpawnEffects();
                        }

                        $$2 = true;
                    }
                }

                if ($$2) {
                    this.updateSpawns(world, pos);
                }

            }
        }
    }

    public void updateSpawns(World world, BlockPos pos) {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            this.spawnDelay = this.minSpawnDelay + this.random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        }

        this.spawnPotentials.getOrEmpty(this.random).ifPresent($$2 -> this.setSpawnEntry(world, pos, (MobSpawnerEntry) $$2.getData()));
        this.sendStatus(world, pos, 1);
    }

    public void readNbt(@Nullable World world, BlockPos pos, NbtCompound nbt) {
        this.spawnDelay = nbt.getShort("Delay");
        boolean $$3 = nbt.contains("SpawnPotentials", 9);
        boolean $$4 = nbt.contains("SpawnData", 10);
        if (!$$3) {
            MobSpawnerEntry $$5;
            if ($$4) {
                $$5 = MobSpawnerEntry.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("SpawnData")).resultOrPartial($$0 -> LOGGER.warn("Invalid SpawnData: {}", $$0)).orElseGet(MobSpawnerEntry::new);
            } else {
                $$5 = new MobSpawnerEntry();
            }

            this.spawnPotentials = DataPool.of($$5);
            this.setSpawnEntry(world, pos, $$5);
        } else {
            NbtList $$7 = nbt.getList("SpawnPotentials", 10);
            this.spawnPotentials = MobSpawnerEntry.DATA_POOL_CODEC.parse(NbtOps.INSTANCE, $$7).resultOrPartial($$0 -> LOGGER.warn("Invalid SpawnPotentials list: {}", $$0)).orElseGet(() -> DataPool.<MobSpawnerEntry>empty());
            if ($$4) {
                MobSpawnerEntry $$8 = (MobSpawnerEntry) MobSpawnerEntry.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("SpawnData")).resultOrPartial($$0 -> LOGGER.warn("Invalid SpawnData: {}", $$0)).orElseGet(MobSpawnerEntry::new);
                this.setSpawnEntry(world, pos, $$8);
            } else {
                this.spawnPotentials.getOrEmpty(this.random).ifPresent($$2 -> this.setSpawnEntry(world, pos, (MobSpawnerEntry) $$2.getData()));
            }
        }

        if (nbt.contains("MinSpawnDelay", 99)) {
            this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
            this.spawnCount = nbt.getShort("SpawnCount");
        }

        if (nbt.contains("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = nbt.getShort("RequiredPlayerRange");
        }

        if (nbt.contains("SpawnRange", 99)) {
            this.spawnRange = nbt.getShort("SpawnRange");
        }

        // Read custom values
        if (nbt.contains("RequiresPlayer")) {
            this.requiresPlayer = nbt.getBoolean("RequiresPlayer");
        }

        if (nbt.contains("Active")) {
            this.active = nbt.getBoolean("Active");
        }

        this.renderedEntity = null;
    }

    public NbtCompound writeNbt(NbtCompound $$0) {
        $$0.putShort("Delay", (short) this.spawnDelay);
        $$0.putShort("MinSpawnDelay", (short) this.minSpawnDelay);
        $$0.putShort("MaxSpawnDelay", (short) this.maxSpawnDelay);
        $$0.putShort("SpawnCount", (short) this.spawnCount);
        $$0.putShort("MaxNearbyEntities", (short) this.maxNearbyEntities);
        $$0.putShort("RequiredPlayerRange", (short) this.requiredPlayerRange);
        $$0.putShort("SpawnRange", (short) this.spawnRange);
        $$0.put("SpawnData", (NbtElement) MobSpawnerEntry.CODEC.encodeStart(NbtOps.INSTANCE, this.spawnEntry).result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData")));
        $$0.put("SpawnPotentials", (NbtElement) MobSpawnerEntry.DATA_POOL_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).result().orElseThrow());

        // Write custom values
        $$0.putBoolean("RequiresPlayer", requiresPlayer);
        $$0.putBoolean("Active", active);

        return $$0;
    }

    @Nullable
    public Entity getRenderedEntity(World world) {
        if (this.renderedEntity == null) {
            this.renderedEntity = EntityType.loadEntityWithPassengers(this.spawnEntry.getNbt(), world, Function.identity());
            if (this.spawnEntry.getNbt().getSize() == 1 && this.spawnEntry.getNbt().contains("id", 8) && this.renderedEntity instanceof MobEntity) {
            }
        }

        return this.renderedEntity;
    }

    public boolean method_8275(World $$0, int $$1) {
        if ($$1 == 1) {
            if ($$0.isClient) {
                this.spawnDelay = this.minSpawnDelay;
            }

            return true;
        } else {
            return false;
        }
    }

    public void setSpawnEntry(@Nullable World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
        this.spawnEntry = spawnEntry;
    }

    public abstract void sendStatus(World world, BlockPos pos, int i);

    public double method_8278() {
        return this.field_9161;
    }

    public double method_8279() {
        return this.field_9159;
    }

    //Custom methods to control the logics properties
    public void setRequiredPlayerRange(int requiredPlayerRange) {
        this.requiredPlayerRange = requiredPlayerRange;
    }

    public void setMinSpawnDelay(int minSpawnDelay) {
        this.minSpawnDelay = minSpawnDelay;
    }

    public void setMaxSpawnDelay(int maxSpawnDelay) {
        this.maxSpawnDelay = maxSpawnDelay;
    }

    public void setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
    }

    public void setMaxNearbyEntities(int maxNearbyEntities) {
        this.maxNearbyEntities = maxNearbyEntities;
    }

    public void setEnty(MobSpawnerEntry entry) {
        this.spawnPotentials = DataPool.of(entry);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setRequiresPlayer(boolean requiresPlayer) {
        this.requiresPlayer = requiresPlayer;
    }

    public boolean isActive() {
        return active;
    }
}
