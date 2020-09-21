package com.glisco.conjuring;

import com.glisco.conjuring.blocks.ConjurerBlock;
import com.glisco.conjuring.blocks.ConjurerBlockEntity;
import com.glisco.conjuring.items.ConjuringFocus;
import com.glisco.conjuring.items.ConjuringRod;
import com.glisco.conjuring.items.charms.HasteCharm;
import com.glisco.conjuring.items.charms.IgnoranceCharm;
import com.glisco.conjuring.items.charms.PlentifulnessCharm;
import com.glisco.conjuring.items.charms.ScopeCharm;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ConjuringCommon implements ModInitializer {

    public static final ItemGroup CONJURING_GROUP = FabricItemGroupBuilder.build(
            new Identifier("conjuring", "general"),
            () -> new ItemStack(Blocks.SPAWNER));

    public static final Block CONJURER_BLOCK = new ConjurerBlock();
    public static BlockEntityType<ConjurerBlockEntity> CONJURER_BLOCK_ENTITY;

    public static final ScreenHandlerType<ConjurerScreenHandler> CONJURER_SCREEN_HANDLER_TYPE;

    static {
        CONJURER_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(new Identifier("conjuring", "conjurer"), ConjurerScreenHandler::new);
    }

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("conjuring", "conjurer"), new BlockItem(CONJURER_BLOCK, new Item.Settings().group(ConjuringCommon.CONJURING_GROUP).rarity(Rarity.UNCOMMON)));

        Registry.register(Registry.ITEM, new Identifier("conjuring", "conjuring_rod"), new ConjuringRod());
        Registry.register(Registry.ITEM, new Identifier("conjuring", "conjuring_focus"), new ConjuringFocus());

        Registry.register(Registry.ITEM, new Identifier("conjuring", "haste_charm"), new HasteCharm());
        Registry.register(Registry.ITEM, new Identifier("conjuring", "ignorance_charm"), new IgnoranceCharm());
        Registry.register(Registry.ITEM, new Identifier("conjuring", "plentifulness_charm"), new PlentifulnessCharm());
        Registry.register(Registry.ITEM, new Identifier("conjuring", "scope_charm"), new ScopeCharm());


        Registry.register(Registry.BLOCK, new Identifier("conjuring", "conjurer"), CONJURER_BLOCK);
        CONJURER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "conjuring:conjurer", BlockEntityType.Builder.create(ConjurerBlockEntity::new, CONJURER_BLOCK).build(null));
    }
}
