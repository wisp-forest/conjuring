package com.glisco.conjuring.items;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.items.soul_alloy_tools.*;
import com.glisco.owo.registration.annotations.AssignedName;
import com.glisco.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class ConjuringItems implements ItemRegistryContainer {

    public static Item CONJURING_SCEPTER = new ConjuringScepter();
    public static final Item SUPERIOR_CONJURING_SCEPTER = new SuperiorConjuringScepter();
    public static final Item CONJURING_FOCUS = new ConjuringFocus(false);
    public static final Item STABILIZED_CONJURING_FOCUS = new ConjuringFocus(true);
    public static final Item ENCHIRIDION = new EnchiridionItem();

    public static final Item SOUL_ALLOY_SWORD = new SoulAlloySword();
    public static final Item SOUL_ALLOY_PICKAXE = new SoulAlloyPickaxe();
    public static final Item SOUL_ALLOY_HATCHET = new SoulAlloyHatchet();
    public static final Item SOUL_ALLOY_SHOVEL = new SoulAlloyShovel();

    public static final Item SOUL_ALLOY = createWithRarity(Rarity.UNCOMMON);
    public static Item SOUL_SLICE = createWithRarity(Rarity.RARE);
    public static Item SOUL_ROD = new SoulRod();
    public static final Item CONJURATION_ESSENCE = new ConjurationEssence();
    public static final Item LESSER_CONJURATION_ESSENCE = createWithRarity(Rarity.RARE);
    public static final Item DISTILLED_SPIRIT = new DistilledSpiritItem();

    public static Item GEM_SOCKET = createWithRarity(Rarity.UNCOMMON);
    public static final Item HASTE_CHARM = createCharm();
    public static final Item IGNORANCE_CHARM = createCharm();
    @AssignedName("plentifulness_charm")
    public static final Item ABUNDANCE_CHARM = createCharm();
    public static final Item SCOPE_CHARM = createCharm();
    public static Item HASTE_GEM = new GemItem(SoulAlloyTool.SoulAlloyModifier.HASTE);
    public static Item IGNORANCE_GEM = new GemItem(SoulAlloyTool.SoulAlloyModifier.IGNORANCE);
    public static Item ABUNDANCE_GEM = new GemItem(SoulAlloyTool.SoulAlloyModifier.ABUNDANCE);
    public static Item SCOPE_GEM = new GemItem(SoulAlloyTool.SoulAlloyModifier.SCOPE);

    public static final Item PIZZA = new PizzaItem();

    private static Item createCharm() {
        return new Item(createSettings().maxCount(8).rarity(Rarity.UNCOMMON));
    }

    private static Item createWithRarity(Rarity rarity) {
        return new Item(createSettings().rarity(rarity));
    }

    private static Item.Settings createSettings() {
        return new Item.Settings().group(Conjuring.CONJURING_GROUP);
    }
}
