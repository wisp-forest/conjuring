package com.glisco.conjuring.blocks;

import com.glisco.conjuring.items.ConjuringFocus;
import com.glisco.conjuring.items.charms.HasteCharm;
import com.glisco.conjuring.items.charms.IgnoranceCharm;
import com.glisco.conjuring.items.charms.PlentifulnessCharm;
import com.glisco.conjuring.items.charms.ScopeCharm;
import com.glisco.conjuring.mixin.ConjurerLogic;
import net.minecraft.item.ItemStack;
import net.minecraft.world.MobSpawnerEntry;

import java.util.ArrayList;
import java.util.List;

public class ConjurerHelper {

    public static void updateConjurerProperties(ConjurerBlockEntity conjurer) {
        int requiredPlayerRange = 16;
        int spawnCount = 4;
        int maxSpawnDelay = 800;
        int maxNearbyEntites = 6;

        ItemStack focus = conjurer.getStack(0);
        ItemStack hasteCharms = conjurer.getStack(1);
        ItemStack plentifulnessCharms = conjurer.getStack(2);
        ItemStack scopeCharms = conjurer.getStack(3);
        ItemStack ignoranceCharms = conjurer.getStack(4);

        if (focus.getItem() instanceof ConjuringFocus) {
            MobSpawnerEntry entry = new MobSpawnerEntry(1, focus.getTag().getCompound("Entity"));
            List<MobSpawnerEntry> entries = new ArrayList<>();
            entries.add(entry);

            ((ConjurerLogic) conjurer.getLogic()).setSpawnPotentials(entries);
            ((ConjurerLogic) conjurer.getLogic()).setSpawnEntry(entry);
            conjurer.setActive(true);
        } else {
            ((ConjurerLogic) conjurer.getLogic()).setRequiredPlayerRange(0);
            conjurer.setActive(false);
            return;
        }

        if (hasteCharms.getItem() instanceof HasteCharm) {
            maxSpawnDelay = 800 - hasteCharms.getCount() * 100;
        }

        if (plentifulnessCharms.getItem() instanceof PlentifulnessCharm) {
            spawnCount = 4 + plentifulnessCharms.getCount() * 2;
        }

        if (scopeCharms.getItem() instanceof ScopeCharm) {
            requiredPlayerRange = 16 + scopeCharms.getCount() * 6;
        }

        if (ignoranceCharms.getItem() instanceof IgnoranceCharm) {
            maxNearbyEntites = 6 + ignoranceCharms.getCount() * 2;
        }

        ((ConjurerLogic) conjurer.getLogic()).setRequiredPlayerRange(requiredPlayerRange);
        ((ConjurerLogic) conjurer.getLogic()).setMaxNearbyEntities(maxNearbyEntites);
        ((ConjurerLogic) conjurer.getLogic()).setMaxSpawnDelay(maxSpawnDelay);
        ((ConjurerLogic) conjurer.getLogic()).setMinSpawnDelay(maxSpawnDelay / 4);
        ((ConjurerLogic) conjurer.getLogic()).setSpawnCount(spawnCount);
    }
}
