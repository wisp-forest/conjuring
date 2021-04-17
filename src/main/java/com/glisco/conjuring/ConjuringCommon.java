package com.glisco.conjuring;

import com.glisco.conjuring.blocks.BlackstonePedestalBlock;
import com.glisco.conjuring.blocks.BlackstonePedestalBlockEntity;
import com.glisco.conjuring.blocks.SoulFunnelBlock;
import com.glisco.conjuring.blocks.SoulFunnelBlockEntity;
import com.glisco.conjuring.blocks.conjurer.ConjurerBlock;
import com.glisco.conjuring.blocks.conjurer.ConjurerBlockEntity;
import com.glisco.conjuring.blocks.soulfireForge.SoulfireForgeBlock;
import com.glisco.conjuring.blocks.soulfireForge.SoulfireForgeBlockEntity;
import com.glisco.conjuring.blocks.soulfireForge.SoulfireForgeRecipe;
import com.glisco.conjuring.blocks.soulfireForge.SoulfireForgeRecipeSerializer;
import com.glisco.conjuring.compat.config.ConjuringConfig;
import com.glisco.conjuring.entities.SoulProjectile;
import com.glisco.conjuring.items.*;
import com.glisco.conjuring.items.charms.HasteCharm;
import com.glisco.conjuring.items.charms.IgnoranceCharm;
import com.glisco.conjuring.items.charms.PlentifulnessCharm;
import com.glisco.conjuring.items.charms.ScopeCharm;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ConjuringCommon implements ModInitializer {

    public static ConjuringConfig CONFIG;

    public static final ItemGroup CONJURING_GROUP = FabricItemGroupBuilder.build(
            new Identifier("conjuring", "general"),
            () -> new ItemStack(Blocks.SPAWNER));

    public static Item CONJURING_SCEPTER = new ConjuringScepter();
    public static Item SUPERIOR_CONJURING_SCEPTER = new SuperiorConjuringScepter();
    public static Item CONJURATION_ESSENCE = new ConjurationEssence();
    public static Item LESSER_CONJURATION_ESSENCE = new LesserConjurationEssence();
    public static Item CONJURING_FOCUS = new ConjuringFocus();
    public static Item SOUL_ROD = new SoulRod();
    public static Item SOUL_ALLOY = new SoulAlloy();
    public static Item GEM_SOCKET = new GemSocket();

    public static final Block CONJURER_BLOCK = new ConjurerBlock();
    public static BlockEntityType<ConjurerBlockEntity> CONJURER_BLOCK_ENTITY;

    public static final Block SOULFIRE_FORGE_BLOCK = new SoulfireForgeBlock();
    public static BlockEntityType<SoulfireForgeBlockEntity> SOULFIRE_FORGE_BLOCK_ENTITY;

    public static final Block BLACKSTONE_PEDESTAL = new BlackstonePedestalBlock();
    public static BlockEntityType<BlackstonePedestalBlockEntity> BLACKSTONE_PEDSTAL_BLOCK_ENTITY;

    public static final Block SOUL_FUNNEL = new SoulFunnelBlock();
    public static BlockEntityType<SoulFunnelBlockEntity> SOUL_FUNNEL_BLOCK_ENTITY;

    public static final ScreenHandlerType<ConjurerScreenHandler> CONJURER_SCREEN_HANDLER_TYPE;
    public static final ScreenHandlerType<SoulfireForgeScreenHandler> SOULFIRE_FORGE_SCREEN_HANDLER_TYPE;

    public static final EntityType<SoulProjectile> SOUL_PROJECTILE;

    static {
        CONJURER_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(new Identifier("conjuring", "conjurer"), ConjurerScreenHandler::new);
        SOULFIRE_FORGE_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(new Identifier("conjuring", "soulfire_forge"), SoulfireForgeScreenHandler::new);
        SOUL_PROJECTILE = FabricEntityTypeBuilder.<SoulProjectile>create(SpawnGroup.MISC, SoulProjectile::new).dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build();
    }

    private static final Identifier SPAWNER_LOOT_TABLE_ID = new Identifier("minecraft", "blocks/spawner");

    @Override
    public void onInitialize() {

        Registry.register(Registry.ITEM, new Identifier("conjuring", "conjuring_scepter"), CONJURING_SCEPTER);
        Registry.register(Registry.ITEM, new Identifier("conjuring", "superior_conjuring_scepter"), SUPERIOR_CONJURING_SCEPTER);
        Registry.register(Registry.ITEM, new Identifier("conjuring", "conjuration_essence"), CONJURATION_ESSENCE);
        Registry.register(Registry.ITEM, new Identifier("conjuring", "lesser_conjuration_essence"), LESSER_CONJURATION_ESSENCE);
        Registry.register(Registry.ITEM, new Identifier("conjuring", "conjuring_focus"), CONJURING_FOCUS);
        Registry.register(Registry.ITEM, new Identifier("conjuring", "soul_rod"), SOUL_ROD);
        Registry.register(Registry.ITEM, new Identifier("conjuring", "soul_alloy"), SOUL_ALLOY);
        Registry.register(Registry.ITEM, new Identifier("conjuring", "gem_socket"), GEM_SOCKET);

        Registry.register(Registry.ITEM, new Identifier("conjuring", "haste_charm"), new HasteCharm());
        Registry.register(Registry.ITEM, new Identifier("conjuring", "ignorance_charm"), new IgnoranceCharm());
        Registry.register(Registry.ITEM, new Identifier("conjuring", "plentifulness_charm"), new PlentifulnessCharm());
        Registry.register(Registry.ITEM, new Identifier("conjuring", "scope_charm"), new ScopeCharm());

        CONJURER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "conjuring:conjurer", BlockEntityType.Builder.create(ConjurerBlockEntity::new, CONJURER_BLOCK).build(null));
        Registry.register(Registry.BLOCK, new Identifier("conjuring", "conjurer"), CONJURER_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("conjuring", "conjurer"), new BlockItem(CONJURER_BLOCK, new Item.Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON)));

        SOULFIRE_FORGE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "conjuring:soulfire_forge", BlockEntityType.Builder.create(SoulfireForgeBlockEntity::new, SOULFIRE_FORGE_BLOCK).build(null));
        Registry.register(Registry.BLOCK, new Identifier("conjuring", "soulfire_forge"), SOULFIRE_FORGE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("conjuring", "soulfire_forge"), new BlockItem(SOULFIRE_FORGE_BLOCK, new Item.Settings().group(ConjuringCommon.CONJURING_GROUP)));

        BLACKSTONE_PEDSTAL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "conjuring:blackstone_pedestal", BlockEntityType.Builder.create(BlackstonePedestalBlockEntity::new, BLACKSTONE_PEDESTAL).build(null));
        Registry.register(Registry.BLOCK, new Identifier("conjuring", "blackstone_pedestal"), BLACKSTONE_PEDESTAL);
        Registry.register(Registry.ITEM, new Identifier("conjuring", "blackstone_pedestal"), new BlockItem(BLACKSTONE_PEDESTAL, new Item.Settings().group(ConjuringCommon.CONJURING_GROUP)));

        SOUL_FUNNEL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "conjuring:soul_funnel", BlockEntityType.Builder.create(SoulFunnelBlockEntity::new, SOUL_FUNNEL).build(null));
        Registry.register(Registry.BLOCK, new Identifier("conjuring", "soul_funnel"), SOUL_FUNNEL);
        Registry.register(Registry.ITEM, new Identifier("conjuring", "soul_funnel"), new BlockItem(SOUL_FUNNEL, new Item.Settings().group(ConjuringCommon.CONJURING_GROUP)));

        Registry.register(Registry.RECIPE_SERIALIZER, SoulfireForgeRecipeSerializer.ID, SoulfireForgeRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, SoulfireForgeRecipe.Type.ID, SoulfireForgeRecipe.Type.INSTANCE);

        Registry.register(Registry.ENTITY_TYPE, new Identifier("conjuring", "soul_projectile"), SOUL_PROJECTILE);

        AutoConfig.register(ConjuringConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ConjuringConfig.class).getConfig();

        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if (SPAWNER_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(CONJURATION_ESSENCE).build());

                supplier.withPool(poolBuilder.build());
            } else if (new Identifier("minecraft", "chests/simple_dungeon").equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(CONJURATION_ESSENCE).conditionally(RandomChanceLootCondition.builder(0.35f)).build());

                supplier.withPool(poolBuilder.build());
            } else if (new Identifier("minecraft", "chests/bastion_treasure").equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(CONJURATION_ESSENCE).conditionally(RandomChanceLootCondition.builder(0.175f)).build());

                supplier.withPool(poolBuilder.build());
            } else if (new Identifier("minecraft", "chests/desert_pyramid").equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(CONJURATION_ESSENCE).conditionally(RandomChanceLootCondition.builder(0.2f)).build());

                supplier.withPool(poolBuilder.build());
            } else if (new Identifier("minecraft", "chests/stronghold_corridor").equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(CONJURATION_ESSENCE).conditionally(RandomChanceLootCondition.builder(0.2f)).build());

                supplier.withPool(poolBuilder.build());
            } else if (new Identifier("minecraft", "chests/stronghold_library").equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(CONJURATION_ESSENCE).conditionally(RandomChanceLootCondition.builder(0.05f)).build());

                supplier.withPool(poolBuilder.build());
            }
        });
    }
}
