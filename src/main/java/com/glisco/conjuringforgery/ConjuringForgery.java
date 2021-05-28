package com.glisco.conjuringforgery;

import com.glisco.conjuringforgery.blocks.BlackstonePedestalBlock;
import com.glisco.conjuringforgery.blocks.BlackstonePedestalTileEntity;
import com.glisco.conjuringforgery.blocks.SoulFunnelBlock;
import com.glisco.conjuringforgery.blocks.SoulFunnelTileEntity;
import com.glisco.conjuringforgery.blocks.conjurer.ConjurerBlock;
import com.glisco.conjuringforgery.blocks.conjurer.ConjurerTileEntity;
import com.glisco.conjuringforgery.blocks.gem_tinkerer.GemTinkererBlock;
import com.glisco.conjuringforgery.blocks.gem_tinkerer.GemTinkererBlockEntity;
import com.glisco.conjuringforgery.blocks.gem_tinkerer.GemTinkererRecipe;
import com.glisco.conjuringforgery.blocks.gem_tinkerer.GemTinkererRecipeSerializer;
import com.glisco.conjuringforgery.blocks.soul_weaver.SoulWeaverBlock;
import com.glisco.conjuringforgery.blocks.soul_weaver.SoulWeaverRecipe;
import com.glisco.conjuringforgery.blocks.soul_weaver.SoulWeaverRecipeSerializer;
import com.glisco.conjuringforgery.blocks.soul_weaver.SoulWeaverTileEntity;
import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeBlock;
import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeRecipe;
import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeRecipeSerializer;
import com.glisco.conjuringforgery.blocks.soulfireForge.SoulfireForgeTileEntity;
import com.glisco.conjuringforgery.client.*;
import com.glisco.conjuringforgery.compat.config.ConjuringConfig;
import com.glisco.conjuringforgery.entities.*;
import com.glisco.conjuringforgery.items.*;
import com.glisco.conjuringforgery.items.soul_alloy_tools.*;
import com.glisco.owo.ServerParticles;
import com.glisco.owo.VectorSerializer;
import com.glisco.owo.WorldOps;
import com.glisco.owo.client.ClientParticles;
import com.google.gson.JsonObject;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;

@Mod("conjuring")
public class ConjuringForgery {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "conjuring";

    public static ConjuringConfig CONFIG;

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static final ItemGroup CONJURING_GROUP = new ItemGroup("conjuring.general") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.SPAWNER);
        }

        @Override
        public void fill(NonNullList<ItemStack> itemStacks) {

            itemStacks.add(new ItemStack(CONJURER_ITEM.get()));
            itemStacks.add(new ItemStack(SOULFIRE_FORGE_ITEM.get()));
            itemStacks.add(new ItemStack(BLACKSTONE_PEDESTAL_ITEM.get()));
            itemStacks.add(new ItemStack(SOUL_FUNNEL_ITEM.get()));
            itemStacks.add(new ItemStack(SOuL_WEAVER_ITEM.get()));
            itemStacks.add(new ItemStack(GEM_TINKERER_ITEM.get()));

            for (int i = 0; i < 3; i++) {
                itemStacks.add(ItemStack.EMPTY);
            }

            itemStacks.add(new ItemStack(CONJURING_SCEPTER.get()));
            itemStacks.add(new ItemStack(SUPERIOR_CONJURING_SCEPTER.get()));
            itemStacks.add(new ItemStack(CONJURING_FOCUS.get()));
            itemStacks.add(new ItemStack(STABILIZED_FOCUS.get()));
            itemStacks.add(new ItemStack(ENCHIRIDION.get()));

            for (int i = 0; i < 4; i++) {
                itemStacks.add(ItemStack.EMPTY);
            }

            itemStacks.add(new ItemStack(SOUL_ALLOY_SWORD.get()));
            itemStacks.add(new ItemStack(SOUL_ALLOY_PICKAXE.get()));
            itemStacks.add(new ItemStack(SOUL_ALLOY_HATCHET.get()));
            itemStacks.add(new ItemStack(SOUL_ALLOY_SHOVEL.get()));

            for (int i = 0; i < 5; i++) {
                itemStacks.add(ItemStack.EMPTY);
            }

            itemStacks.add(new ItemStack(SOUL_ALLOY.get()));
            itemStacks.add(new ItemStack(SOUL_SLICE.get()));
            itemStacks.add(new ItemStack(SOUL_ROD.get()));
            itemStacks.add(new ItemStack(CONJURATION_ESSENCE.get()));
            itemStacks.add(new ItemStack(LESSER_CONJURATION_ESSENCE.get()));

            for (int i = 0; i < 4; i++) {
                itemStacks.add(ItemStack.EMPTY);
            }

            itemStacks.add(new ItemStack(GEM_SOCKET.get()));
            itemStacks.add(new ItemStack(HASTE_CHARM.get()));
            itemStacks.add(new ItemStack(IGNORANCE_CHARM.get()));
            itemStacks.add(new ItemStack(PLENTIFULNESS_CHARM.get()));
            itemStacks.add(new ItemStack(SCOPE_CHARM.get()));
            itemStacks.add(new ItemStack(HASTE_GEM.get()));
            itemStacks.add(new ItemStack(IGNORANCE_GEM.get()));
            itemStacks.add(new ItemStack(ABUNDANCE_GEM.get()));
            itemStacks.add(new ItemStack(SCOPE_GEM.get()));
        }
    };

    public static final RegistryObject<Item> SCOPE_GEM = ITEMS.register("scope_gem", () -> new GemItem(SoulAlloyTool.SoulAlloyModifier.SCOPE));
    public static final RegistryObject<Item> ABUNDANCE_GEM = ITEMS.register("abundance_gem", () -> new GemItem(SoulAlloyTool.SoulAlloyModifier.ABUNDANCE));
    public static final RegistryObject<Item> IGNORANCE_GEM = ITEMS.register("ignorance_gem", () -> new GemItem(SoulAlloyTool.SoulAlloyModifier.IGNORANCE));
    public static final RegistryObject<Item> HASTE_GEM = ITEMS.register("haste_gem", () -> new GemItem(SoulAlloyTool.SoulAlloyModifier.HASTE));

    public static final RegistryObject<Item> SCOPE_CHARM = ITEMS.register("scope_charm", CharmItem::new);
    public static final RegistryObject<Item> PLENTIFULNESS_CHARM = ITEMS.register("plentifulness_charm", CharmItem::new);
    public static final RegistryObject<Item> HASTE_CHARM = ITEMS.register("haste_charm", CharmItem::new);
    public static final RegistryObject<Item> IGNORANCE_CHARM = ITEMS.register("ignorance_charm", CharmItem::new);
    public static final RegistryObject<Item> GEM_SOCKET = ITEMS.register("gem_socket", GemSocket::new);

    public static final RegistryObject<Item> CONJURATION_ESSENCE = ITEMS.register("conjuration_essence", ConjurationEssence::new);
    public static final RegistryObject<Item> LESSER_CONJURATION_ESSENCE = ITEMS.register("lesser_conjuration_essence", LesserConjurationEssence::new);
    public static final RegistryObject<Item> SOUL_ALLOY = ITEMS.register("soul_alloy", SoulAlloy::new);
    public static final RegistryObject<Item> SOUL_ROD = ITEMS.register("soul_rod", SoulRod::new);

    public static final RegistryObject<Item> ENCHIRIDION = ITEMS.register("enchiridion", EnchiridionItem::new);
    public static final RegistryObject<Item> PIZZA = ITEMS.register("pizza", PizzaItem::new);
    public static final RegistryObject<Item> SOUL_SLICE = ITEMS.register("soul_slice", SoulSlice::new);

    public static final RegistryObject<Item> SOUL_ALLOY_HATCHET = ITEMS.register("soul_alloy_hatchet", SoulAlloyHatchet::new);
    public static final RegistryObject<Item> SOUL_ALLOY_SWORD = ITEMS.register("soul_alloy_sword", SoulAlloySword::new);
    public static final RegistryObject<Item> SOUL_ALLOY_PICKAXE = ITEMS.register("soul_alloy_pickaxe", SoulAlloyPickaxe::new);
    public static final RegistryObject<Item> SOUL_ALLOY_SHOVEL = ITEMS.register("soul_alloy_shovel", SoulAlloyShovel::new);

    public static final RegistryObject<Item> CONJURING_FOCUS = ITEMS.register("conjuring_focus", ConjuringFocus::new);
    public static final RegistryObject<Item> STABILIZED_FOCUS = ITEMS.register("stabilized_conjuring_focus", () -> new ConjuringFocus() {
        @Override
        public boolean hasEffect(ItemStack stack) {
            return true;
        }
    });
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

    public static final RegistryObject<Block> SOUL_WEAVER = BLOCKS.register("soul_weaver", SoulWeaverBlock::new);
    public static final RegistryObject<Item> SOuL_WEAVER_ITEM = ITEMS.register("soul_weaver", () -> new BlockItem(SOUL_WEAVER.get(), new Item.Properties().group(CONJURING_GROUP)));
    public static final RegistryObject<TileEntityType<SoulWeaverTileEntity>> SOUL_WEAVER_TILE = TILES.register("soul_weaver", () -> TileEntityType.Builder.create(SoulWeaverTileEntity::new, SOUL_WEAVER.get()).build(null));

    public static final RegistryObject<Block> GEM_TINKERER = BLOCKS.register("gem_tinkerer", GemTinkererBlock::new);
    public static final RegistryObject<Item> GEM_TINKERER_ITEM = ITEMS.register("gem_tinkerer", () -> new BlockItem(GEM_TINKERER.get(), new Item.Properties().group(CONJURING_GROUP)));
    public static final RegistryObject<TileEntityType<GemTinkererBlockEntity>> GEM_TINKERER_TILE = TILES.register("gem_tinkerer", () -> TileEntityType.Builder.create(GemTinkererBlockEntity::new, GEM_TINKERER.get()).build(null));

    public static final RegistryObject<SoundEvent> WEEE = SOUNDS.register("block.soul_weaver.weee", () -> new SoundEvent(new ResourceLocation("conjuring", "block.soul_weaver.weee")));

    public static final RegistryObject<EntityType<SoulProjectileEntity>> SOUL_PROJECTILE = ENTITIES.register("soul_projectile", () -> EntityType.Builder.<SoulProjectileEntity>create(SoulProjectileEntity::new, EntityClassification.MISC)
            .size(0.25f, 0.25f)
            .updateInterval(1)
            .build("soul_projectile"));

    public static final RegistryObject<EntityType<SoulFellerEntity>> SOUL_FELLER = ENTITIES.register("soul_feller", () -> EntityType.Builder.<SoulFellerEntity>create(SoulFellerEntity::new, EntityClassification.MISC)
            .size(0.25f, 0.25f)
            .updateInterval(1)
            .build("soul_feller"));

    public static final RegistryObject<EntityType<SoulMagnetEntity>> SOUL_MAGNET = ENTITIES.register("soul_magnet", () -> EntityType.Builder.<SoulMagnetEntity>create(SoulMagnetEntity::new, EntityClassification.MISC)
            .size(0.25f, 0.25f)
            .updateInterval(1)
            .build("soul_magnet"));

    public static final RegistryObject<EntityType<SoulDiggerEntity>> SOUL_DIGGER = ENTITIES.register("soul_digger", () -> EntityType.Builder.<SoulDiggerEntity>create(SoulDiggerEntity::new, EntityClassification.MISC)
            .size(0.25f, 0.25f)
            .updateInterval(1)
            .build("soul_digger"));

    public static final GemTinkeringCriterion GEM_TINKERING_CRITERION = new GemTinkeringCriterion();
    public static final ExtractionRitualCriterion EXTRACTION_RITUAL_CRITERION = new ExtractionRitualCriterion();

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
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (minecraft, screen) -> AutoConfig.getConfigScreen(ConjuringConfig.class, screen).get());
    }

    private void setup(final FMLCommonSetupEvent event) {
        AutoConfig.register(ConjuringConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ConjuringConfig.class).getConfig();

        NETWORK_CHANNEL.registerMessage(0, ChangeToolModePacket.class, ChangeToolModePacket::encode, ChangeToolModePacket::decode, ChangeToolModePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));

        CriteriaTriggers.register(GEM_TINKERING_CRITERION);
        CriteriaTriggers.register(EXTRACTION_RITUAL_CRITERION);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ConjuringForgeryClient.clientInit(event);
    }

    public static class RegistryEvents {
        @SubscribeEvent
        public void onRecipeRegistry(RegistryEvent.Register<IRecipeSerializer<?>> event) {
            Registry.register(Registry.RECIPE_TYPE, SoulfireForgeRecipe.Type.ID, SoulfireForgeRecipe.Type.INSTANCE);
            event.getRegistry().register(SoulfireForgeRecipeSerializer.INSTANCE.setRegistryName(SoulfireForgeRecipeSerializer.ID));

            Registry.register(Registry.RECIPE_TYPE, GemTinkererRecipe.Type.ID, GemTinkererRecipe.Type.INSTANCE);
            event.getRegistry().register(GemTinkererRecipeSerializer.INSTANCE.setRegistryName(GemTinkererRecipeSerializer.ID));

            Registry.register(Registry.RECIPE_TYPE, SoulWeaverRecipe.Type.ID, SoulWeaverRecipe.Type.INSTANCE);
            event.getRegistry().register(SoulWeaverRecipeSerializer.INSTANCE.setRegistryName(SoulWeaverRecipeSerializer.ID));
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

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public void onEndTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            if (!ConjuringForgeryClient.TOGGLE_TOOL_MODE_BIND.isPressed()) return;
            NETWORK_CHANNEL.sendToServer(new ChangeToolModePacket());
        }

        @SubscribeEvent
        public void onWorldTick(TickEvent.WorldTickEvent event) {
            if (event.side != LogicalSide.SERVER) return;
            if (event.phase != TickEvent.Phase.START) return;
            BlockCrawler.tick(event.world);
        }

        @SubscribeEvent
        public void onScopeAttack(LivingDamageEvent event) {
            final DamageSource source = event.getSource();
            if (source instanceof CopycatPlayerDamageSource) return;
            if (!source.getDamageType().equals("player")) return;
            if (!(source instanceof EntityDamageSource)) return;

            final PlayerEntity player = (PlayerEntity) source.getTrueSource();

            if (!SoulAlloyToolAbilities.canAoeHit(player)) return;

            final int scopeLevel = SoulAlloyTool.getModifierLevel(player.getHeldItemMainhand(), SoulAlloyTool.SoulAlloyModifier.SCOPE);
            final int range = 2 + scopeLevel;

            final Entity entity = event.getEntity();
            final World world = entity.world;
            List<Entity> entities = world.getEntitiesInAABBexcluding(entity, new AxisAlignedBB(entity.getPositionVec().subtract(range, 1, range), entity.getPositionVec().add(range, 1, range)), entity1 -> entity1 instanceof LivingEntity);
            entities.remove(player);

            for (int i = 0; i < ConjuringForgery.CONFIG.tools_config.sword_scope_max_entities && i < entities.size(); i++) {
                entities.get(i).attackEntityFrom(new CopycatPlayerDamageSource(player), event.getAmount() * ConjuringForgery.CONFIG.tools_config.sword_scope_damage_multiplier * scopeLevel);
                player.getHeldItemMainhand().damageItem(4 * scopeLevel, player, playerEntity -> player.sendBreakAnimation(Hand.MAIN_HAND));

                JsonObject object = new JsonObject();
                VectorSerializer.toJson(entity.getPositionVec().add(0, 0.25 + world.rand.nextDouble(), 0), object, "start");
                VectorSerializer.toJson(entities.get(i).getPositionVec().add(0, 0.25 + world.rand.nextDouble(), 0), object, "end");

                if (!world.isRemote()) {
                    ServerParticles.issueEvent((ServerWorld) world, entity.getPosition(), new ResourceLocation("conjuring", "line"), packetBuffer -> {
                        packetBuffer.writeString(ServerParticles.NETWORK_GSON.toJson(object));
                    });
                }
            }
        }

        @SubscribeEvent
        public void onIgnoranceAttack(LivingDamageEvent event) {
            final DamageSource source = event.getSource();
            if (source instanceof CopycatPlayerDamageSource) return;
            if (!source.getDamageType().equals("player")) return;
            if (!(source instanceof EntityDamageSource)) return;

            final PlayerEntity player = (PlayerEntity) source.getTrueSource();

            if (!SoulAlloyToolAbilities.canArmorPierce(player)) return;

            float pierceDamage = SoulAlloyTool.getModifierLevel(player.getHeldItemMainhand(), SoulAlloyTool.SoulAlloyModifier.IGNORANCE) * ConjuringForgery.CONFIG.tools_config.sword_ignorance_multiplier * event.getAmount();
            event.setAmount(event.getAmount() - pierceDamage);

            event.getEntity().attackEntityFrom(new CopycatPlayerDamageSource(player).pierceArmor(), pierceDamage);
        }

        @SubscribeEvent
        public void onAoeDig(BlockEvent.BreakEvent event) {
            final PlayerEntity player = event.getPlayer();
            if (!SoulAlloyToolAbilities.canAoeDig(player)) return;
            for (BlockPos pos : SoulAlloyToolAbilities.getBlocksToDig(player)) {
                WorldOps.breakBlockWithItem((World) event.getWorld(), pos, player.getHeldItemMainhand());

                player.getHeldItemMainhand().damageItem(SoulAlloyTool.getModifierLevel(player.getHeldItemMainhand(), SoulAlloyTool.SoulAlloyModifier.SCOPE) * 2, player, p -> p.sendBreakAnimation(Hand.MAIN_HAND));
            }
        }
    }
}
