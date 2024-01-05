package com.glisco.conjuring.blocks.conjurer;


import com.glisco.conjuring.util.ConjuringParticleEvents;
import com.mojang.logging.LogUtils;
import net.minecraft.block.spawner.MobSpawnerEntry;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Function;

/**
 * Modified version of Minecraft's MobSpawnerLogic
 * <p>
 * Used to provide control over the logics properties,
 * to make it usable for a player-modifiable spawner
 */
public abstract class ConjurerLogic {
    private static final Logger LOGGER = LogUtils.getLogger();
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

    // These fields are appended
    private boolean requiresPlayer = true;
    private boolean active = false;

    // This method is made public
    public boolean isPlayerInRange(World world, BlockPos pos) {
        return world.getReceivedRedstonePower(pos) == 0 && (!requiresPlayer || world.isPlayerInRange((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, (double) this.requiredPlayerRange));
    }

    public void clientTick(World world, BlockPos pos) {
        if (!this.isPlayerInRange(world, pos)  || !active) {
            this.field_9159 = this.field_9161;
        } else {
            Random random = world.getRandom();
            double d = (double) pos.getX() + random.nextDouble();
            double e = (double) pos.getY() + random.nextDouble();
            double f = (double) pos.getZ() + random.nextDouble();

            //These particles have been changed to reflect the custom spawner version
            world.addParticle(ParticleTypes.ENCHANTED_HIT, d, e, f, 0.0, 0.0, 0.0);
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d, e, f, 0.0, 0.0, 0.0);

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
                boolean bl = false;

                for (int i = 0; i < this.spawnCount; ++i) {
                    NbtCompound nbtCompound = this.spawnEntry.getNbt();
                    Optional<EntityType<?>> optional = EntityType.fromNbt(nbtCompound);
                    if (optional.isEmpty()) {
                        this.updateSpawns(world, pos);
                        return;
                    }

                    NbtList nbtList = nbtCompound.getList("Pos", NbtElement.DOUBLE_TYPE);
                    int j = nbtList.size();
                    Random random = world.getRandom();
                    double d = j >= 1 ? nbtList.getDouble(0) : (double) pos.getX() + (random.nextDouble() - random.nextDouble()) * (double) this.spawnRange + 0.5;
                    double e = j >= 2 ? nbtList.getDouble(1) : (double) (pos.getY() + random.nextInt(3) - 1);
                    double f = j >= 3 ? nbtList.getDouble(2) : (double) pos.getZ() + (random.nextDouble() - random.nextDouble()) * (double) this.spawnRange + 0.5;
                    if (world.isSpaceEmpty((optional.get()).createSimpleBoundingBox(d, e, f))) {
                        BlockPos blockPos = BlockPos.ofFloored(d, e, f);
                        if (this.spawnEntry.getCustomSpawnRules().isPresent()) {
                            if (!(optional.get()).getSpawnGroup().isPeaceful() && world.getDifficulty() == Difficulty.PEACEFUL) {
                                continue;
                            }

                            MobSpawnerEntry.CustomSpawnRules customSpawnRules = this.spawnEntry.getCustomSpawnRules().get();
                            if (!customSpawnRules.blockLightLimit().contains(world.getLightLevel(LightType.BLOCK, blockPos))
                                    || !customSpawnRules.skyLightLimit().contains(world.getLightLevel(LightType.SKY, blockPos))) {
                                continue;
                            }
                        } else if (!SpawnRestriction.canSpawn(optional.get(), world, SpawnReason.SPAWNER, blockPos, world.getRandom())) {
                            continue;
                        }

                        Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, world, entityx -> {
                            entityx.refreshPositionAndAngles(d, e, f, entityx.getYaw(), entityx.getPitch());
                            return entityx;
                        });
                        if (entity == null) {
                            this.updateSpawns(world, pos);
                            return;
                        }

                        int k = world.getNonSpectatingEntities(
                                        entity.getClass(),
                                        (new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, (pos.getY() + 1), (pos.getZ() + 1)))
                                                .expand(this.spawnRange)
                                )
                                .size();
                        if (k >= this.maxNearbyEntities) {
                            this.updateSpawns(world, pos);
                            return;
                        }

                        entity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), random.nextFloat() * 360.0F, 0.0F);
                        if (entity instanceof MobEntity) {
                            MobEntity mobEntity = (MobEntity) entity;
                            if (this.spawnEntry.getCustomSpawnRules().isEmpty() && !mobEntity.canSpawn(world, SpawnReason.SPAWNER) || !mobEntity.canSpawn(world)) {
                                continue;
                            }

                            if (this.spawnEntry.getNbt().getSize() == 1 && this.spawnEntry.getNbt().contains("id", NbtElement.STRING_TYPE)) {
                                ((MobEntity) entity).initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.SPAWNER, (EntityData) null, (NbtCompound) null);
                            }
                        }

                        if (!world.spawnNewEntityAndPassengers(entity)) {
                            this.updateSpawns(world, pos);
                            return;
                        }

                        world.syncWorldEvent(WorldEvents.SPAWNER_SPAWNS_MOB, pos, 0);
                        //This worldEvent instead emits a conjuring particle event
                        ConjuringParticleEvents.CONJURER_SUMMON.spawn(world, Vec3d.of(pos), null);
                        if (entity instanceof MobEntity) {
                            ((MobEntity) entity).playSpawnEffects();
                        }

                        bl = true;
                    }
                }

                if (bl) {
                    this.updateSpawns(world, pos);
                }

            }
        }
    }

    // This method is made public
    public void updateSpawns(World world, BlockPos pos) {
        Random random = world.random;
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            this.spawnDelay = this.minSpawnDelay + random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        }

        this.spawnPotentials.getOrEmpty(random).ifPresent(present -> this.setSpawnEntry(world, pos, (MobSpawnerEntry) present.getData()));
        this.sendStatus(world, pos, 1);
    }

    public void readNbt(@Nullable World world, BlockPos pos, NbtCompound nbt) {
        this.spawnDelay = nbt.getShort("Delay");
        boolean bl = nbt.contains("SpawnPotentials", NbtElement.LIST_TYPE);
        boolean bl2 = nbt.contains("SpawnData", NbtElement.COMPOUND_TYPE);
        if (!bl) {
            MobSpawnerEntry mobSpawnerEntry;
            if (bl2) {
                mobSpawnerEntry = MobSpawnerEntry.CODEC
                        .parse(NbtOps.INSTANCE, nbt.getCompound("SpawnData"))
                        .resultOrPartial(string -> LOGGER.warn("Invalid SpawnData: {}", string))
                        .orElseGet(MobSpawnerEntry::new);
            } else {
                mobSpawnerEntry = new MobSpawnerEntry();
            }

            this.spawnPotentials = DataPool.of(mobSpawnerEntry);
            this.setSpawnEntry(world, pos, mobSpawnerEntry);
        } else {
            NbtList nbtList = nbt.getList("SpawnPotentials", NbtElement.COMPOUND_TYPE);
            this.spawnPotentials = MobSpawnerEntry.DATA_POOL_CODEC
                    .parse(NbtOps.INSTANCE, nbtList)
                    .resultOrPartial(string -> LOGGER.warn("Invalid SpawnPotentials list: {}", string))
                    .orElseGet(() -> DataPool.<MobSpawnerEntry>empty());
            if (bl2) {
                MobSpawnerEntry mobSpawnerEntry2 = MobSpawnerEntry.CODEC
                        .parse(NbtOps.INSTANCE, nbt.getCompound("SpawnData"))
                        .resultOrPartial(string -> LOGGER.warn("Invalid SpawnData: {}", string))
                        .orElseGet(MobSpawnerEntry::new);
                this.setSpawnEntry(world, pos, mobSpawnerEntry2);
            } else {
                this.spawnPotentials.getOrEmpty(world.getRandom()).ifPresent(present -> this.setSpawnEntry(world, pos, (MobSpawnerEntry) present.getData()));
            }
        }

        if (nbt.contains("MinSpawnDelay", NbtElement.NUMBER_TYPE)) {
            this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
            this.spawnCount = nbt.getShort("SpawnCount");
        }

        if (nbt.contains("MaxNearbyEntities", NbtElement.NUMBER_TYPE)) {
            this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = nbt.getShort("RequiredPlayerRange");
        }

        if (nbt.contains("SpawnRange", NbtElement.NUMBER_TYPE)) {
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

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putShort("Delay", (short) this.spawnDelay);
        nbt.putShort("MinSpawnDelay", (short) this.minSpawnDelay);
        nbt.putShort("MaxSpawnDelay", (short) this.maxSpawnDelay);
        nbt.putShort("SpawnCount", (short) this.spawnCount);
        nbt.putShort("MaxNearbyEntities", (short) this.maxNearbyEntities);
        nbt.putShort("RequiredPlayerRange", (short) this.requiredPlayerRange);
        nbt.putShort("SpawnRange", (short) this.spawnRange);
        nbt.put(
                "SpawnData",
                MobSpawnerEntry.CODEC.encodeStart(NbtOps.INSTANCE, this.spawnEntry).result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData"))
        );
        nbt.put("SpawnPotentials", MobSpawnerEntry.DATA_POOL_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).result().orElseThrow());

        // Write custom values
        nbt.putBoolean("RequiresPlayer", requiresPlayer);
        nbt.putBoolean("Active", active);

        return nbt;
    }

    @Nullable
    public Entity getRenderedEntity(World world) {
        if (this.renderedEntity == null) {
            this.renderedEntity = EntityType.loadEntityWithPassengers(this.spawnEntry.getNbt(), world, Function.identity());
            if (this.spawnEntry.getNbt().getSize() == 1 && this.spawnEntry.getNbt().contains("id", NbtElement.STRING_TYPE) && this.renderedEntity instanceof MobEntity) {
            }
        }

        return this.renderedEntity;
    }

    public void setSpawnEntry(@Nullable World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
        this.spawnEntry = spawnEntry;
    }

    public abstract void sendStatus(World world, BlockPos pos, int status);

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
