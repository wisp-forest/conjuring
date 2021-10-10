package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.blocks.ConjuringBlocks;
import com.glisco.conjuring.blocks.gem_tinkerer.GemTinkererRecipe;
import com.glisco.conjuring.blocks.soul_weaver.SoulWeaverRecipe;
import com.glisco.conjuring.blocks.soulfire_forge.SoulfireForgeRecipe;
import com.glisco.conjuring.items.ConjuringItems;
import dev.architectury.event.EventResult;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Objects;

public class ConjuringPlugin implements REIClientPlugin {

    public static final CategoryIdentifier<SoulfireForgeDisplay> SOULFIRE_FORGE = CategoryIdentifier.of(Conjuring.MOD_ID, "soulfire_forge");
    public static final CategoryIdentifier<GemTinkeringDisplay> GEM_TINKERING = CategoryIdentifier.of(Conjuring.MOD_ID, "gem_tinkering");
    public static final CategoryIdentifier<SoulWeavingDisplay> SOUL_WEAVING = CategoryIdentifier.of(Conjuring.MOD_ID, "soul_weaving");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new GemTinkeringCategory());
        registry.add(new SoulWeavingCategory());
        registry.add(new SoulfireForgeCategory());

        registry.addWorkstations(SOULFIRE_FORGE, EntryStacks.of(ConjuringBlocks.SOULFIRE_FORGE));
        registry.addWorkstations(GEM_TINKERING, EntryStacks.of(ConjuringBlocks.GEM_TINKERER));
        registry.addWorkstations(SOUL_WEAVING, EntryStacks.of(ConjuringBlocks.SOUL_WEAVER));
        registry.addWorkstations(SOUL_WEAVING, EntryStacks.of(ConjuringBlocks.BLACKSTONE_PEDESTAL));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerFiller(SoulfireForgeRecipe.class, SoulfireForgeDisplay::new);
        registry.registerFiller(GemTinkererRecipe.class, GemTinkeringDisplay::new);
        registry.registerFiller(SoulWeaverRecipe.class, SoulWeavingDisplay::new);

        registry.registerVisibilityPredicate((category, display) -> {
            if (Objects.equals(category.getCategoryIdentifier(), SOULFIRE_FORGE)) {
                if (display.getOutputEntries().stream().flatMap(List::stream)
                        .anyMatch(entryStack -> entryStack.getValue() instanceof ItemStack stack && stack.getItem() == ConjuringItems.PIZZA))
                    return EventResult.interruptFalse();
            } else if (Objects.equals(category.getCategoryIdentifier(), GEM_TINKERING)) {
                if (display.getOutputEntries().stream().flatMap(List::stream)
                        .anyMatch(entryStack -> entryStack.getValue() instanceof ItemStack stack && stack.getItem() == Items.COOKIE))
                    return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        registry.removeEntry(EntryStacks.of(ConjuringItems.PIZZA));
    }
}
