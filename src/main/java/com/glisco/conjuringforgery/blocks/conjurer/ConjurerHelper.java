package com.glisco.conjuringforgery.blocks.conjurer;

import com.glisco.conjuringforgery.items.ConjuringFocus;
import com.glisco.conjuringforgery.items.charms.HasteCharm;
import com.glisco.conjuringforgery.items.charms.IgnoranceCharm;
import com.glisco.conjuringforgery.items.charms.PlentifulnessCharm;
import com.glisco.conjuringforgery.items.charms.ScopeCharm;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedSpawnerEntity;

import java.util.ArrayList;
import java.util.List;

public class ConjurerHelper {

    public static void updateConjurerProperties(ConjurerTileEntity conjurer) {
        int requiredPlayerRange = 16;
        int spawnCount = 4;
        int maxSpawnDelay = 800;
        int maxNearbyEntities = 6;

        ItemStack focus = conjurer.getInventory().getStackInSlot(0);
        ItemStack hasteCharms = conjurer.getInventory().getStackInSlot(1);
        ItemStack plentifulnessCharms = conjurer.getInventory().getStackInSlot(2);
        ItemStack scopeCharms = conjurer.getInventory().getStackInSlot(3);
        ItemStack ignoranceCharms = conjurer.getInventory().getStackInSlot(4);

        if (focus.getItem() instanceof ConjuringFocus) {
            WeightedSpawnerEntity entry = new WeightedSpawnerEntity(1, focus.getTag().getCompound("Entity"));
            List<WeightedSpawnerEntity> entries = new ArrayList<>();
            entries.add(entry);

            conjurer.getLogic().setSpawnPotentials(entries);
            conjurer.getLogic().setNextSpawnData(entry);
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
        conjurer.getLogic().resetTimer();
    }
}
