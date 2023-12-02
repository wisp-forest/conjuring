package com.glisco.conjuring.items;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.items.soul_alloy_tools.*;
import io.wispforest.lavender.book.LavenderBookItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.annotations.AssignedName;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class ConjuringItems implements ItemRegistryContainer {

    public static final Item CONJURING_SCEPTER = new ConjuringScepter();
    public static final Item SUPERIOR_CONJURING_SCEPTER = new SuperiorConjuringScepter();
    public static final Item CONJURING_FOCUS = new ConjuringFocus(false);
    public static final Item STABILIZED_CONJURING_FOCUS = new ConjuringFocus(true);
    public static final Item ENCHIRIDION = LavenderBookItem.registerForBook(new EnchiridionItem());

    public static final Item SOUL_ALLOY_SWORD = new SoulAlloySword();
    public static final Item SOUL_ALLOY_PICKAXE = new SoulAlloyPickaxe();
    public static final Item SOUL_ALLOY_HATCHET = new SoulAlloyHatchet();
    public static final Item SOUL_ALLOY_SHOVEL = new SoulAlloyShovel();
    public static final Item SOUL_ALLOY_SCYTHE = new SoulAlloyScythe();

    public static final Item SOUL_ALLOY = itemWithRarity(Rarity.UNCOMMON);
    public static final Item SOUL_SLICE = itemWithRarity(Rarity.RARE);
    public static final Item SOUL_ROD = new SoulRod();
    public static final Item CONJURATION_ESSENCE = new ConjurationEssence();
    public static final Item LESSER_CONJURATION_ESSENCE = itemWithRarity(Rarity.RARE);
    public static final Item DISTILLED_SPIRIT = new DistilledSpiritItem();

    public static final Item GEM_SOCKET = itemWithRarity(Rarity.UNCOMMON);
    public static final Item HASTE_CHARM = new CharmItem(SoulAlloyTool.SoulAlloyModifier.HASTE);
    public static final Item IGNORANCE_CHARM = new CharmItem(SoulAlloyTool.SoulAlloyModifier.IGNORANCE);
    @AssignedName("plentifulness_charm")
    public static final Item ABUNDANCE_CHARM = new CharmItem(SoulAlloyTool.SoulAlloyModifier.ABUNDANCE);
    public static final Item SCOPE_CHARM = new CharmItem(SoulAlloyTool.SoulAlloyModifier.SCOPE);
    public static final Item HASTE_GEM = new GemItem(SoulAlloyTool.SoulAlloyModifier.HASTE);
    public static final Item IGNORANCE_GEM = new GemItem(SoulAlloyTool.SoulAlloyModifier.IGNORANCE);
    public static final Item ABUNDANCE_GEM = new GemItem(SoulAlloyTool.SoulAlloyModifier.ABUNDANCE);
    public static final Item SCOPE_GEM = new GemItem(SoulAlloyTool.SoulAlloyModifier.SCOPE);

    public static final Item PIZZA = new PizzaItem();

    private static Item itemWithRarity(Rarity rarity) {
        return new Item(createSettings().rarity(rarity));
    }

    private static Item.Settings createSettings() {
        return new OwoItemSettings().group(Conjuring.CONJURING_GROUP);
    }
}
