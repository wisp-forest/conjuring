package com.glisco.conjuring.blocks.conjurer;

import com.glisco.conjuring.util.ConjuringParticleEvents;
import com.glisco.owo.particles.ServerParticles;
import com.google.common.collect.Lists;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
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
    private static Pool<MobSpawnerEntry> field_30952 = Pool.empty();
    private int spawnDelay = 20;
    private Pool<MobSpawnerEntry> spawnPotentials;
    private MobSpawnerEntry spawnEntry;
    private double field_9161;
    private double field_9159;
    private int minSpawnDelay;
    private int maxSpawnDelay;
    private int spawnCount;
    @Nullable
    private Entity renderedEntity;
    private int maxNearbyEntities;
    private int requiredPlayerRange;
    private int spawnRange;
    private final Random random;
    private boolean requiresPlayer = true;
    private boolean active = false;

    public ConjurerLogic() {
        this.spawnPotentials = field_30952;
        this.spawnEntry = new MobSpawnerEntry();
        this.minSpawnDelay = 200;
        this.maxSpawnDelay = 800;
        this.spawnCount = 4;
        this.maxNearbyEntities = 6;
        this.requiredPlayerRange = 16;
        this.spawnRange = 4;
        this.random = new Random();
    }

    @Nullable
    private Identifier getEntityId(@Nullable World world, BlockPos pos) {
        String string = this.spawnEntry.getEntityNbt().getString("id");

        try {
            return ChatUtil.isEmpty(string) ? null : new Identifier(string);
        } catch (InvalidIdentifierException var5) {
            LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", string, world != null ? world.getRegistryKey().getValue() : "<null>", pos.getX(), pos.getY(), pos.getZ());
            return null;
        }
    }

    public void setEntityId(EntityType<?> type) {
        this.spawnEntry.getEntityNbt().putString("id", Registry.ENTITY_TYPE.getId(type).toString());
    }

    public boolean isPlayerInRange(World world, BlockPos pos) {
        return world.getReceivedRedstonePower(pos) == 0 && (!requiresPlayer || world.isPlayerInRange((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, this.requiredPlayerRange));
    }

    public void clientTick(World world, BlockPos pos) {
        if (!this.isPlayerInRange(world, pos) || !active) {
            this.field_9159 = this.field_9161;
        } else {
            double d = (double) pos.getX() + world.random.nextDouble();
            double e = (double) pos.getY() + world.random.nextDouble();
            double f = (double) pos.getZ() + world.random.nextDouble();

            //These particles have been changed to reflect the custom spawner version
            world.addParticle(ParticleTypes.ENCHANTED_HIT, d, e, f, 0.0D, 1.0D, 0.0D);
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d, e, f, 0.0D, 0.0D, 0.0D);
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            }

            this.field_9159 = this.field_9161;
            this.field_9161 = (this.field_9161 + (double) (1000.0F / ((float) this.spawnDelay + 200.0F))) % 360.0D;
        }

    }

    public void serverTick(ServerWorld world, BlockPos pos) {
        if (this.isPlayerInRange(world, pos) && active) {
            if (this.spawnDelay == -1) {
                this.updateSpawns(world, pos);
            }

            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            } else {
                boolean bl = false;

                for (int i = 0; i < this.spawnCount; ++i) {
                    NbtCompound nbtCompound = this.spawnEntry.getEntityNbt();
                    Optional<EntityType<?>> optional = EntityType.fromNbt(nbtCompound);
                    if (!optional.isPresent()) {
                        this.updateSpawns(world, pos);
                        return;
                    }

                    NbtList nbtList = nbtCompound.getList("Pos", 6);
                    int j = nbtList.size();
                    double d = j >= 1 ? nbtList.getDouble(0) : (double) pos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double) this.spawnRange + 0.5D;
                    double e = j >= 2 ? nbtList.getDouble(1) : (double) (pos.getY() + world.random.nextInt(3) - 1);
                    double f = j >= 3 ? nbtList.getDouble(2) : (double) pos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double) this.spawnRange + 0.5D;
                    if (world.isSpaceEmpty(optional.get().createSimpleBoundingBox(d, e, f)) && SpawnRestriction.canSpawn((EntityType) optional.get(), world, SpawnReason.SPAWNER, new BlockPos(d, e, f), world.getRandom())) {
                        Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, world, (entityx) -> {
                            entityx.refreshPositionAndAngles(d, e, f, entityx.getYaw(), entityx.getPitch());
                            return entityx;
                        });
                        if (entity == null) {
                            this.updateSpawns(world, pos);
                            return;
                        }

                        int k = world.getNonSpectatingEntities(entity.getClass(), (new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)).expand(this.spawnRange)).size();
                        if (k >= this.maxNearbyEntities) {
                            this.updateSpawns(world, pos);
                            return;
                        }

                        entity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), world.random.nextFloat() * 360.0F, 0.0F);
                        if (entity instanceof MobEntity) {
                            MobEntity mobEntity = (MobEntity) entity;
                            if (!mobEntity.canSpawn(world, SpawnReason.SPAWNER) || !mobEntity.canSpawn(world)) {
                                continue;
                            }

                            if (this.spawnEntry.getEntityNbt().getSize() == 1 && this.spawnEntry.getEntityNbt().contains("id", 8)) {
                                ((MobEntity) entity).initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.SPAWNER, null, null);
                            }
                        }

                        if (!world.shouldCreateNewEntityWithPassenger(entity)) {
                            this.updateSpawns(world, pos);
                            return;
                        }

                        //This worldEvent has different ID to work in conjunction with a mixin in the Client's WorldRenderer
                        ServerParticles.issueEvent(world, Vec3d.of(pos), ConjuringParticleEvents.CONJURER_SUMMON);
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

    public void updateSpawns(World world, BlockPos pos) {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            this.spawnDelay = this.minSpawnDelay + this.random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        }

        this.spawnPotentials.getOrEmpty(this.random).ifPresent((mobSpawnerEntry) -> {
            this.setSpawnEntry(world, pos, mobSpawnerEntry);
        });
        this.sendStatus(world, pos, 1);
    }

    public void readNbt(@Nullable World world, BlockPos pos, NbtCompound nbt) {
        this.spawnDelay = nbt.getShort("Delay");
        List<MobSpawnerEntry> list = Lists.newArrayList();
        if (nbt.contains("SpawnPotentials", 9)) {
            NbtList nbtList = nbt.getList("SpawnPotentials", 10);

            for (int i = 0; i < nbtList.size(); ++i) {
                list.add(new MobSpawnerEntry(nbtList.getCompound(i)));
            }
        }

        this.spawnPotentials = Pool.of((List) list);
        if (nbt.contains("SpawnData", 10)) {
            this.setSpawnEntry(world, pos, new MobSpawnerEntry(1, nbt.getCompound("SpawnData")));
        } else if (!list.isEmpty()) {
            this.spawnPotentials.getOrEmpty(this.random).ifPresent((mobSpawnerEntry) -> {
                this.setSpawnEntry(world, pos, mobSpawnerEntry);
            });
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

        if (nbt.contains("RequiresPlayer")) {
            this.requiresPlayer = nbt.getBoolean("RequiresPlayer");
        }

        if (nbt.contains("Active")) {
            this.active = nbt.getBoolean("Active");
        }

        this.renderedEntity = null;
    }

    public NbtCompound writeNbt(@Nullable World world, BlockPos pos, NbtCompound nbt) {
        Identifier identifier = this.getEntityId(world, pos);
        if (identifier == null) {
            return nbt;
        } else {
            nbt.putShort("Delay", (short) this.spawnDelay);
            nbt.putShort("MinSpawnDelay", (short) this.minSpawnDelay);
            nbt.putShort("MaxSpawnDelay", (short) this.maxSpawnDelay);
            nbt.putShort("SpawnCount", (short) this.spawnCount);
            nbt.putShort("MaxNearbyEntities", (short) this.maxNearbyEntities);
            nbt.putShort("RequiredPlayerRange", (short) this.requiredPlayerRange);
            nbt.putShort("SpawnRange", (short) this.spawnRange);
            nbt.put("SpawnData", this.spawnEntry.getEntityNbt().copy());
            nbt.putBoolean("RequiresPlayer", requiresPlayer);
            nbt.putBoolean("Active", active);

            NbtList nbtList = new NbtList();
            if (this.spawnPotentials.isEmpty()) {
                nbtList.add(this.spawnEntry.toNbt());
            } else {

                for (MobSpawnerEntry mobSpawnerEntry : this.spawnPotentials.getEntries()) {
                    nbtList.add(mobSpawnerEntry.toNbt());
                }
            }

            nbt.put("SpawnPotentials", nbtList);
            return nbt;
        }
    }

    @Nullable
    public Entity getRenderedEntity(World world) {
        if (this.renderedEntity == null) {
            this.renderedEntity = EntityType.loadEntityWithPassengers(this.spawnEntry.getEntityNbt(), world, Function.identity());
            if (this.spawnEntry.getEntityNbt().getSize() == 1 && this.spawnEntry.getEntityNbt().contains("id", 8) && this.renderedEntity instanceof MobEntity) {
            }
        }

        return this.renderedEntity;
    }

    public boolean method_8275(World world, int i) {
        if (i == 1) {
            if (world.isClient) {
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

    public void setSpawnPotentials(List<MobSpawnerEntry> spawnPotentials) {
        this.spawnPotentials = Pool.empty();
        this.spawnPotentials = Pool.of(spawnPotentials);
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
