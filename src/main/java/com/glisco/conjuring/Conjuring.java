package com.glisco.conjuring;

import com.glisco.conjuring.blocks.ConjuringBlocks;
import com.glisco.conjuring.blocks.gem_tinkerer.GemTinkererRecipe;
import com.glisco.conjuring.blocks.gem_tinkerer.GemTinkererRecipeSerializer;
import com.glisco.conjuring.blocks.soul_weaver.SoulWeaverRecipe;
import com.glisco.conjuring.blocks.soul_weaver.SoulWeaverRecipeSerializer;
import com.glisco.conjuring.blocks.soulfire_forge.SoulfireForgeRecipe;
import com.glisco.conjuring.blocks.soulfire_forge.SoulfireForgeRecipeSerializer;
import com.glisco.conjuring.compat.config.ConjuringConfig;
import com.glisco.conjuring.entities.SoulDiggerEntity;
import com.glisco.conjuring.entities.SoulFellerEntity;
import com.glisco.conjuring.entities.SoulMagnetEntity;
import com.glisco.conjuring.entities.SoulProjectileEntity;
import com.glisco.conjuring.items.ConjuringItems;
import com.glisco.conjuring.items.soul_alloy_tools.BlockCrawler;
import com.glisco.conjuring.items.soul_alloy_tools.ChangeToolModePacket;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyToolAbilities;
import com.glisco.conjuring.util.ConjurerScreenHandler;
import com.glisco.conjuring.util.ExtractionRitualCriterion;
import com.glisco.conjuring.util.GemTinkeringCriterion;
import com.glisco.conjuring.util.SoulfireForgeScreenHandler;
import com.glisco.owo.itemgroup.OwoItemGroup;
import com.glisco.owo.registration.reflect.FieldRegistrationHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public class Conjuring implements ModInitializer {

    public static final String MOD_ID = "conjuring";

    public static ConjuringConfig CONFIG;

    public static final OwoItemGroup CONJURING_GROUP = new OwoItemGroup(Conjuring.id("general")) {
        @Override
        protected void setup() {
            setCustomTexture(Conjuring.id("textures/gui/group.png"));
        }

        @Override
        public void appendStacks(DefaultedList<ItemStack> stacks) {
            super.appendStacks(stacks);
            for (int i = 0; i < 3; i++) stacks.add(6, ItemStack.EMPTY);
            for (int i = 0; i < 4; i++) stacks.add(14, ItemStack.EMPTY);
            for (int i = 0; i < 5; i++) stacks.add(22, ItemStack.EMPTY);
            for (int i = 0; i < 3; i++) stacks.add(33, ItemStack.EMPTY);
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ConjuringBlocks.CONJURER);
        }
    };

    public static final ScreenHandlerType<ConjurerScreenHandler> CONJURER_SCREEN_HANDLER_TYPE;
    public static final ScreenHandlerType<SoulfireForgeScreenHandler> SOULFIRE_FORGE_SCREEN_HANDLER_TYPE;

    public static final EntityType<SoulProjectileEntity> SOUL_PROJECTILE;
    public static final EntityType<SoulDiggerEntity> SOUL_DIGGER;
    public static final EntityType<SoulFellerEntity> SOUL_FELLER;
    public static final EntityType<SoulMagnetEntity> SOUL_MAGNET;

    public static final SoundEvent WEEE = new SoundEvent(Conjuring.id("block.soul_weaver.weee"));

    public static final ExtractionRitualCriterion EXTRACTION_RITUAL_CRITERION = new ExtractionRitualCriterion();
    public static final GemTinkeringCriterion GEM_TINKERING_CRITERION = new GemTinkeringCriterion();

    static {
        CONJURER_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(Conjuring.id("conjurer"), ConjurerScreenHandler::new);
        SOULFIRE_FORGE_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(Conjuring.id("soulfire_forge"), SoulfireForgeScreenHandler::new);

        SOUL_PROJECTILE = FabricEntityTypeBuilder.<SoulProjectileEntity>create(SpawnGroup.MISC, SoulProjectileEntity::new).dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build();
        SOUL_DIGGER = FabricEntityTypeBuilder.<SoulDiggerEntity>create(SpawnGroup.MISC, SoulDiggerEntity::new).dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build();
        SOUL_FELLER = FabricEntityTypeBuilder.<SoulFellerEntity>create(SpawnGroup.MISC, SoulFellerEntity::new).dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build();
        SOUL_MAGNET = FabricEntityTypeBuilder.<SoulMagnetEntity>create(SpawnGroup.MISC, SoulMagnetEntity::new).dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build();
    }

    @Override
    public void onInitialize() {

        FieldRegistrationHandler.register(ConjuringBlocks.class, MOD_ID, false);
        FieldRegistrationHandler.register(ConjuringBlocks.Entities.class, MOD_ID, false);

        FieldRegistrationHandler.register(ConjuringItems.class, MOD_ID, false);

        Registry.register(Registry.RECIPE_SERIALIZER, SoulfireForgeRecipeSerializer.ID, SoulfireForgeRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, SoulfireForgeRecipe.Type.ID, SoulfireForgeRecipe.Type.INSTANCE);

        Registry.register(Registry.RECIPE_SERIALIZER, SoulWeaverRecipeSerializer.ID, SoulWeaverRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, SoulWeaverRecipe.Type.ID, SoulWeaverRecipe.Type.INSTANCE);

        Registry.register(Registry.RECIPE_SERIALIZER, GemTinkererRecipeSerializer.ID, GemTinkererRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, GemTinkererRecipe.Type.ID, GemTinkererRecipe.Type.INSTANCE);

        Registry.register(Registry.ENTITY_TYPE, Conjuring.id("soul_projectile"), SOUL_PROJECTILE);
        Registry.register(Registry.ENTITY_TYPE, Conjuring.id("soul_feller"), SOUL_FELLER);
        Registry.register(Registry.ENTITY_TYPE, Conjuring.id("soul_digger"), SOUL_DIGGER);
        Registry.register(Registry.ENTITY_TYPE, Conjuring.id("soul_magnet"), SOUL_MAGNET);

        AutoConfig.register(ConjuringConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ConjuringConfig.class).getConfig();

        CONJURING_GROUP.initialize();

        ServerTickEvents.END_WORLD_TICK.register(BlockCrawler::tick);
        ServerPlayNetworking.registerGlobalReceiver(ChangeToolModePacket.ID, ChangeToolModePacket::onPacket);

        Registry.register(Registry.SOUND_EVENT, Conjuring.id("block.soul_weaver.weee"), WEEE);

        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if (new Identifier("minecraft", "blocks/spawner").equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).withEntry(ItemEntry.builder(ConjuringItems.CONJURATION_ESSENCE).build());

                supplier.withPool(poolBuilder.build());
            } else if (new Identifier("minecraft", "chests/simple_dungeon").equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).withEntry(ItemEntry.builder(ConjuringItems.CONJURATION_ESSENCE).conditionally(RandomChanceLootCondition.builder(0.35f)).build());

                supplier.withPool(poolBuilder.build());
            } else if (new Identifier("minecraft", "chests/bastion_treasure").equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).withEntry(ItemEntry.builder(ConjuringItems.CONJURATION_ESSENCE).conditionally(RandomChanceLootCondition.builder(0.175f)).build());

                supplier.withPool(poolBuilder.build());
            } else if (new Identifier("minecraft", "chests/desert_pyramid").equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).withEntry(ItemEntry.builder(ConjuringItems.CONJURATION_ESSENCE).conditionally(RandomChanceLootCondition.builder(0.2f)).build());

                supplier.withPool(poolBuilder.build());
            } else if (new Identifier("minecraft", "chests/stronghold_corridor").equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).withEntry(ItemEntry.builder(ConjuringItems.CONJURATION_ESSENCE).conditionally(RandomChanceLootCondition.builder(0.2f)).build());

                supplier.withPool(poolBuilder.build());
            } else if (new Identifier("minecraft", "chests/stronghold_library").equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).withEntry(ItemEntry.builder(ConjuringItems.CONJURATION_ESSENCE).conditionally(RandomChanceLootCondition.builder(0.05f)).build());

                supplier.withPool(poolBuilder.build());
            }
        });

        CriterionRegistry.register(EXTRACTION_RITUAL_CRITERION);
        CriterionRegistry.register(GEM_TINKERING_CRITERION);

        SoulAlloyToolAbilities.registerCommonEvents();
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
