package com.glisco.conjuring.blocks.conjurer;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.items.CharmItem;
import com.glisco.conjuring.items.ConjuringFocus;
import com.glisco.conjuring.items.SoulAlloyTool;
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
        ItemStack abundanceCharms = conjurer.getStack(2);
        ItemStack scopeCharms = conjurer.getStack(3);
        ItemStack ignoranceCharms = conjurer.getStack(4);

        if (focus.getItem() instanceof ConjuringFocus) {
            MobSpawnerEntry entry = new MobSpawnerEntry(1, focus.getTag().getCompound("Entity"));
            List<MobSpawnerEntry> entries = new ArrayList<>();
            entries.add(entry);

            conjurer.getLogic().setSpawnPotentials(entries);
            conjurer.getLogic().setSpawnEntry(entry);
            conjurer.setActive(true);

            conjurer.setRequiresPlayer(focus.getItem() != ConjuringCommon.STABILIZED_CONJURING_FOCUS);

        } else {
            conjurer.setActive(false);
            conjurer.setRequiresPlayer(true);
            return;
        }

        //TODO replace this with item checks
        if (((CharmItem)hasteCharms.getItem()).modifier == SoulAlloyTool.SoulAlloyModifier.HASTE) {
            maxSpawnDelay = Math.max(10, Math.round(800 - hasteCharms.getCount() * ConjuringCommon.CONFIG.haste_multiplier));
        }

        if (((CharmItem)hasteCharms.getItem()).modifier == SoulAlloyTool.SoulAlloyModifier.ABUNDANCE) {
            spawnCount = 4 + abundanceCharms.getCount() * ConjuringCommon.CONFIG.abundance_multiplier;
        }

        if (((CharmItem)hasteCharms.getItem()).modifier == SoulAlloyTool.SoulAlloyModifier.SCOPE) {
            requiredPlayerRange = 16 + scopeCharms.getCount() * ConjuringCommon.CONFIG.scope_multiplier;
        }

        if (((CharmItem)hasteCharms.getItem()).modifier == SoulAlloyTool.SoulAlloyModifier.IGNORANCE) {
            maxNearbyEntities = 6 + ignoranceCharms.getCount() * ConjuringCommon.CONFIG.ignorance_multiplier;
        }

        conjurer.getLogic().setRequiredPlayerRange(requiredPlayerRange);
        conjurer.getLogic().setMaxNearbyEntities(maxNearbyEntities);
        conjurer.getLogic().setMaxSpawnDelay(maxSpawnDelay);
        conjurer.getLogic().setMinSpawnDelay(maxSpawnDelay / 4);
        conjurer.getLogic().setSpawnCount(spawnCount);
        conjurer.getLogic().updateSpawns();
    }
}
