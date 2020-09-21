package com.glisco.conjuring.mixin;

import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(MobSpawnerLogic.class)
public interface ConjurerLogic {

    @Accessor("requiredPlayerRange")
    public void setRequiredPlayerRange(int requiredPlayerRange);

    @Accessor("minSpawnDelay")
    public void setMinSpawnDelay(int minSpawnDelay);

    @Accessor("maxSpawnDelay")
    public void setMaxSpawnDelay(int maxSpawnDelay);

    @Accessor("spawnCount")
    public void setSpawnCount(int spawnCount);

    @Accessor("maxNearbyEntities")
    public void setMaxNearbyEntities(int maxNearbyEntities);

    @Accessor("spawnPotentials")
    public void setSpawnPotentials(List<MobSpawnerEntry> spawnPotentials);

    @Accessor("spawnEntry")
    public void setSpawnEntry(MobSpawnerEntry spawnEntry);
}
