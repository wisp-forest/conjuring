package com.glisco.conjuring.compat.rei;

public class ConjuringClientPlugin /*implements REIClientPlugin*/ {

//    @Override
//    public void registerCategories(CategoryRegistry registry) {
//        registry.add(new GemTinkeringCategory());
//        registry.add(new SoulWeavingCategory());
//        registry.add(new SoulfireForgeCategory());
//
//        registry.addWorkstations(SOULFIRE_FORGE, EntryStacks.of(ConjuringBlocks.SOULFIRE_FORGE));
//        registry.addWorkstations(GEM_TINKERING, EntryStacks.of(ConjuringBlocks.GEM_TINKERER));
//        registry.addWorkstations(SOUL_WEAVING, EntryStacks.of(ConjuringBlocks.SOUL_WEAVER));
//        registry.addWorkstations(SOUL_WEAVING, EntryStacks.of(ConjuringBlocks.BLACKSTONE_PEDESTAL));
//    }
//
//    @Override
//    public void registerScreens(ScreenRegistry registry) {
//        registry.registerClickArea(screen -> new Rectangle(screen.getRootX() + 98, screen.getRootY() + 27, 16, 26),
//                SoulfireForgeScreen.class, SOULFIRE_FORGE);
//    }
//
//    @Override
//    public void registerDisplays(DisplayRegistry registry) {
//        registry.registerFiller(SoulfireForgeRecipe.class, SoulfireForgeDisplay::new);
//        registry.registerFiller(GemTinkererRecipe.class, GemTinkeringDisplay::new);
//        registry.registerFiller(SoulWeaverRecipe.class, SoulWeavingDisplay::new);
//
//        registry.registerVisibilityPredicate((category, display) -> {
//            if (Objects.equals(category.getCategoryIdentifier(), SOULFIRE_FORGE)) {
//                if (display.getOutputEntries().stream().flatMap(List::stream)
//                        .anyMatch(entryStack -> entryStack.getValue() instanceof ItemStack stack && stack.getItem() == ConjuringItems.PIZZA))
//                    return EventResult.interruptFalse();
//            } else if (Objects.equals(category.getCategoryIdentifier(), GEM_TINKERING)) {
//                if (display.getOutputEntries().stream().flatMap(List::stream)
//                        .anyMatch(entryStack -> entryStack.getValue() instanceof ItemStack stack && stack.getItem() == Items.COOKIE))
//                    return EventResult.interruptFalse();
//            }
//            return EventResult.pass();
//        });
//    }
//
//    @Override
//    public void registerEntries(EntryRegistry registry) {
//        registry.removeEntry(EntryStacks.of(ConjuringItems.PIZZA));
//    }
}
