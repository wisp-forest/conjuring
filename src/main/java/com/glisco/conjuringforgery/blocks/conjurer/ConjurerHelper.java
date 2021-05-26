package com.glisco.conjuringforgery.blocks.conjurer;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.items.ConjuringFocus;
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

            conjurer.setRequiresPlayer(focus.getItem() != ConjuringForgery.STABILIZED_FOCUS.get());

        } else {
            conjurer.setActive(false);
            conjurer.setRequiresPlayer(true);
            return;
        }

        if (hasteCharms.getItem() == ConjuringForgery.HASTE_CHARM.get()) {
            maxSpawnDelay = Math.max(10, Math.round(800 - hasteCharms.getCount() * ConjuringForgery.CONFIG.conjurer_config.haste_multiplier));
        }

        if (plentifulnessCharms.getItem() == ConjuringForgery.PLENTIFULNESS_CHARM.get()) {
            spawnCount = 4 + plentifulnessCharms.getCount() * ConjuringForgery.CONFIG.conjurer_config.abundance_multiplier;
        }

        if (scopeCharms.getItem() == ConjuringForgery.SCOPE_CHARM.get()) {
            requiredPlayerRange = 16 + scopeCharms.getCount() * ConjuringForgery.CONFIG.conjurer_config.scope_multiplier;
        }

        if (ignoranceCharms.getItem() == ConjuringForgery.IGNORANCE_CHARM.get()) {
            maxNearbyEntities = 6 + ignoranceCharms.getCount() * ConjuringForgery.CONFIG.conjurer_config.ignorance_multiplier;
        }

        conjurer.getLogic().setRequiredPlayerRange(requiredPlayerRange);
        conjurer.getLogic().setMaxNearbyEntities(maxNearbyEntities);
        conjurer.getLogic().setMaxSpawnDelay(maxSpawnDelay);
        conjurer.getLogic().setMinSpawnDelay(maxSpawnDelay / 4);
        conjurer.getLogic().setSpawnCount(spawnCount);
        conjurer.getLogic().resetTimer();
    }
}
