package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.util.SoulfireForgeScreenHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoRegistry;

public class ConjuringCommonPlugin implements REIServerPlugin {

    public static final CategoryIdentifier<SoulfireForgeDisplay> SOULFIRE_FORGE = CategoryIdentifier.of(Conjuring.MOD_ID, "soulfire_forge");
    public static final CategoryIdentifier<GemTinkeringDisplay> GEM_TINKERING = CategoryIdentifier.of(Conjuring.MOD_ID, "gem_tinkering");
    public static final CategoryIdentifier<SoulWeavingDisplay> SOUL_WEAVING = CategoryIdentifier.of(Conjuring.MOD_ID, "soul_weaving");

    @Override
    public void registerMenuInfo(MenuInfoRegistry registry) {
        registry.register(SOULFIRE_FORGE, SoulfireForgeScreenHandler.class, new SoulfireForgeMenuInfo());
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(SOULFIRE_FORGE, new SoulfireForgeDisplay.SoulfireForgeDisplaySerializer());
    }
}
