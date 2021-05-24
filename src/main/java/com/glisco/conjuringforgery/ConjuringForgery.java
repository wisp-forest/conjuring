package com.glisco.conjuringforgery;

import com.glisco.conjuringforgery.blocks.BlackstonePedestalBlock;
import com.glisco.conjuringforgery.blocks.BlackstonePedestalTileEntity;
import com.glisco.conjuringforgery.blocks.SoulFunnelBlock;
import com.glisco.conjuringforgery.blocks.SoulFunnelTileEntity;
import com.glisco.conjuringforgery.blocks.conjurer.ConjurerBlock;
import com.glisco.conjuringforgery.blocks.conjurer.ConjurerTileEntity;
import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeBlock;
import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeRecipe;
import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeRecipeSerializer;
import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeTileEntity;
import com.glisco.conjuringforgery.client.*;
import com.glisco.conjuringforgery.entities.SoulProjectile;
import com.glisco.conjuringforgery.entities.SoulProjectileEntityRenderer;
import com.glisco.conjuringforgery.items.*;
import com.glisco.conjuringforgery.items.charms.HasteCharm;
import com.glisco.conjuringforgery.items.charms.IgnoranceCharm;
import com.glisco.conjuringforgery.items.charms.PlentifulnessCharm;
import com.glisco.conjuringforgery.items.charms.ScopeCharm;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("conjuring-forgery")
public class ConjuringForgery {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "conjuring";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    public static final ItemGroup CONJURING_GROUP = new ItemGroup("conjuring.general") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.SPAWNER);
        }
    };

    public static final RegistryObject<Item> SCOPE_CHARM = ITEMS.register("scope_charm", ScopeCharm::new);
    public static final RegistryObject<Item> PLENTIFULNESS_CHARM = ITEMS.register("plentifulness_charm", PlentifulnessCharm::new);
    public static final RegistryObject<Item> HASTE_CHARM = ITEMS.register("haste_charm", HasteCharm::new);
    public static final RegistryObject<Item> IGNORANCE_CHARM = ITEMS.register("ignorance_charm", IgnoranceCharm::new);
    public static final RegistryObject<Item> GEM_SOCKET = ITEMS.register("gem_socket", GemSocket::new);

    public static final RegistryObject<Item> CONJURATION_ESSENCE = ITEMS.register("conjuration_essence", ConjurationEssence::new);
    public static final RegistryObject<Item> LESSER_CONJURATION_ESSENCE = ITEMS.register("lesser_conjuration_essence", LesserConjurationEssence::new);
    public static final RegistryObject<Item> SOUL_ALLOY = ITEMS.register("soul_alloy", SoulAlloy::new);
    public static final RegistryObject<Item> SOUL_ROD = ITEMS.register("soul_rod", SoulRod::new);

    public static final RegistryObject<Item> CONJURING_FOCUS = ITEMS.register("conjuring_focus", ConjuringFocus::new);
    public static final RegistryObject<Item> CONJURING_SCEPTER = ITEMS.register("conjuring_scepter", ConjuringScepter::new);
    public static final RegistryObject<Item> SUPERIOR_CONJURING_SCEPTER = ITEMS.register("superior_conjuring_scepter", SuperiorConjuringScepter::new);

    public static final RegistryObject<Block> CONJURER = BLOCKS.register("conjurer", ConjurerBlock::new);
    public static final RegistryObject<Item> CONJURER_ITEM = ITEMS.register("conjurer", () -> new BlockItem(CONJURER.get(), new Item.Properties().group(CONJURING_GROUP)));
    public static final RegistryObject<TileEntityType<ConjurerTileEntity>> CONJURER_TILE = TILES.register("conjurer", () -> TileEntityType.Builder.create(ConjurerTileEntity::new, CONJURER.get()).build(null));
    public static final RegistryObject<ContainerType<ConjurerContainer>> CONJURER_CONTAINER_TYPE = CONTAINERS.register("conjurer", () -> IForgeContainerType.create((windowId, inv, data) -> new ConjurerContainer(windowId, inv, inv.player.getEntityWorld(), data.readBlockPos())));

    public static final RegistryObject<Block> SOULFIRE_FORGE = BLOCKS.register("soulfire_forge", SoulfireForgeBlock::new);
    public static final RegistryObject<Item> SOULFIRE_FORGE_ITEM = ITEMS.register("soulfire_forge", () -> new BlockItem(SOULFIRE_FORGE.get(), new Item.Properties().group(CONJURING_GROUP)));
    public static final RegistryObject<TileEntityType<SoulfireForgeTileEntity>> SOULFIRE_FORGE_TILE = TILES.register("soulfire_forge", () -> TileEntityType.Builder.create(SoulfireForgeTileEntity::new, SOULFIRE_FORGE.get()).build(null));
    public static final RegistryObject<ContainerType<SoulfireForgeContainer>> SOULFIRE_FORGE_CONTAINER_TYPE = CONTAINERS.register("soulfire_forge", () -> IForgeContainerType.create((windowId, inv, data) -> new SoulfireForgeContainer(windowId, inv, inv.player.getEntityWorld(), data.readBlockPos())));

    public static final RegistryObject<Block> BLACKSTONE_PEDESTAL = BLOCKS.register("blackstone_pedestal", BlackstonePedestalBlock::new);
    public static final RegistryObject<Item> BLACKSTONE_PEDESTAL_ITEM = ITEMS.register("blackstone_pedestal", () -> new BlockItem(BLACKSTONE_PEDESTAL.get(), new Item.Properties().group(CONJURING_GROUP)));
    public static final RegistryObject<TileEntityType<BlackstonePedestalTileEntity>> BLACKSTONE_PEDESTAL_TILE = TILES.register("blackstone_pedestal", () -> TileEntityType.Builder.create(BlackstonePedestalTileEntity::new, BLACKSTONE_PEDESTAL.get()).build(null));

    public static final RegistryObject<Block> SOUL_FUNNEL = BLOCKS.register("soul_funnel", SoulFunnelBlock::new);
    public static final RegistryObject<Item> SOUL_FUNNEL_ITEM = ITEMS.register("soul_funnel", () -> new BlockItem(SOUL_FUNNEL.get(), new Item.Properties().group(CONJURING_GROUP)));
    public static final RegistryObject<TileEntityType<SoulFunnelTileEntity>> SOUL_FUNNEL_TILE = TILES.register("soul_funnel", () -> TileEntityType.Builder.create(SoulFunnelTileEntity::new, SOUL_FUNNEL.get()).build(null));

    public static final RegistryObject<EntityType<SoulProjectile>> SOUL_PROJECTILE = ENTITIES.register("soul_projectile", () -> EntityType.Builder.<SoulProjectile>create(SoulProjectile::new, EntityClassification.MISC)
            .size(0.25f, 0.25f)
            .trackingRange(4)
            .func_233608_b_(10)
            .build("soul_projectile"));

    public ConjuringForgery() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        FMLJavaModLoadingContext.get().getModEventBus().register(new RegistryEvents());

        MinecraftForge.EVENT_BUS.register(new GameEvents());

        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(CONJURER_CONTAINER_TYPE.get(), ConjurerScreen::new);
        ScreenManager.registerFactory(SOULFIRE_FORGE_CONTAINER_TYPE.get(), SoulfireForgeScreen::new);

        RenderTypeLookup.setRenderLayer(CONJURER.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(SOULFIRE_FORGE.get(), RenderType.getCutout());

        ItemModelsProperties.registerProperty(CONJURING_FOCUS.get(), new ResourceLocation(MODID, "has_soul"), (stack, world, entity) -> {
            return stack.getOrCreateTag().contains("Entity") ? 1.0f : 0f;
        });

        ClientRegistry.bindTileEntityRenderer(CONJURER_TILE.get(), ConjurerTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BLACKSTONE_PEDESTAL_TILE.get(), BlackstonePedestalTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(SOUL_FUNNEL_TILE.get(), SoulFunnelTileEntityRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(SOUL_PROJECTILE.get(), SoulProjectileEntityRenderer::new);
    }

    public static class RegistryEvents {
        @SubscribeEvent
        public void onRecipeRegistry(RegistryEvent.Register<IRecipeSerializer<?>> event) {
            Registry.register(Registry.RECIPE_TYPE, SoulfireForgeRecipe.Type.ID, SoulfireForgeRecipe.Type.INSTANCE);
            event.getRegistry().register(SoulfireForgeRecipeSerializer.INSTANCE.setRegistryName(SoulfireForgeRecipeSerializer.ID));
        }
    }

    public static class GameEvents {

        @SubscribeEvent
        public void onLootTableLoad(LootTableLoadEvent event) {

            if (!event.getName().getNamespace().equalsIgnoreCase("minecraft")) return;

            switch (event.getName().getPath()) {
                case "blocks/spawner":
                    event.getTable().addPool(new LootPool.Builder().addEntry(ItemLootEntry.builder(CONJURATION_ESSENCE.get())).name("conjuration_essence").build());
                    break;
                case "chests/simple_dungeon":
                    event.getTable().addPool(new LootPool.Builder().addEntry(ItemLootEntry.builder(CONJURATION_ESSENCE.get())).acceptCondition(RandomChance.builder(0.35f)).name("conjuration_essence").build());
                    break;
                case "chests/bastion_treasure":
                    event.getTable().addPool(new LootPool.Builder().addEntry(ItemLootEntry.builder(CONJURATION_ESSENCE.get())).acceptCondition(RandomChance.builder(0.175f)).name("conjuration_essence").build());
                    break;
                case "chests/desert_pyramid":
                case "chests/stronghold_corridor":
                    event.getTable().addPool(new LootPool.Builder().addEntry(ItemLootEntry.builder(CONJURATION_ESSENCE.get())).acceptCondition(RandomChance.builder(0.2f)).name("conjuration_essence").build());
                    break;
                case "chests/stronghold_library":
                    event.getTable().addPool(new LootPool.Builder().addEntry(ItemLootEntry.builder(CONJURATION_ESSENCE.get())).acceptCondition(RandomChance.builder(0.05f)).name("conjuration_essence").build());
                    break;
            }
        }
    }
}
