package com.glisco.conjuring.blocks.conjurer;

import com.glisco.conjuring.items.ConjuringFocus;
import com.glisco.conjuring.items.charms.HasteCharm;
import com.glisco.conjuring.items.charms.IgnoranceCharm;
import com.glisco.conjuring.items.charms.PlentifulnessCharm;
import com.glisco.conjuring.items.charms.ScopeCharm;
import net.minecraft.item.ItemStack;
import net.minecraft.world.MobSpawnerEntry;

import java.util.ArrayList;
import java.util.List;

public class ConjurerHelper {

    public static void updateConjurerProperties(ConjurerBlockEntity conjurer) {
        int requiredPlayerRange = 16;
        int spawnCount = 4;
        int maxSpawnDelay = 800;
        int maxNearbyEntities = 6;

        ItemStack focus = conjurer.getStack(0);
        ItemStack hasteCharms = conjurer.getStack(1);
        ItemStack plentifulnessCharms = conjurer.getStack(2);
        ItemStack scopeCharms = conjurer.getStack(3);
        ItemStack ignoranceCharms = conjurer.getStack(4);

        if (focus.getItem() instanceof ConjuringFocus) {
            MobSpawnerEntry entry = new MobSpawnerEntry(1, focus.getTag().getCompound("Entity"));
            List<MobSpawnerEntry> entries = new ArrayList<>();
            entries.add(entry);

            conjurer.getLogic().setSpawnPotentials(entries);
            conjurer.getLogic().setSpawnEntry(entry);
            conjurer.setActive(true);
        } else {
            conjurer.getLogic().setRequiredPlayerRange(0);
            conjurer.setActive(false);
            return;
        }

        if (hasteCharms.getItem() instanceof HasteCharm) {
            maxSpawnDelay = Math.round(800 - hasteCharms.getCount() * 93.75f);
        }

        if (plentifulnessCharms.getItem() instanceof PlentifulnessCharm) {
            spawnCount = 4 + plentifulnessCharms.getCount() * 2;
        }

        if (scopeCharms.getItem() instanceof ScopeCharm) {
            requiredPlayerRange = 16 + scopeCharms.getCount() * 6;
        }

        if (ignoranceCharms.getItem() instanceof IgnoranceCharm) {
            maxNearbyEntities = 6 + ignoranceCharms.getCount() * 2;
        }

        conjurer.getLogic().setRequiredPlayerRange(requiredPlayerRange);
        conjurer.getLogic().setMaxNearbyEntities(maxNearbyEntities);
        conjurer.getLogic().setMaxSpawnDelay(maxSpawnDelay);
        conjurer.getLogic().setMinSpawnDelay(maxSpawnDelay / 4);
        conjurer.getLogic().setSpawnCount(spawnCount);
        conjurer.getLogic().updateSpawns();
    }
}
