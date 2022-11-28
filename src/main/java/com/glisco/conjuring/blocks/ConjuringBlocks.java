package com.glisco.conjuring.blocks;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.blocks.conjurer.ConjurerBlock;
import com.glisco.conjuring.blocks.conjurer.ConjurerBlockEntity;
import com.glisco.conjuring.blocks.gem_tinkerer.GemTinkererBlock;
import com.glisco.conjuring.blocks.gem_tinkerer.GemTinkererBlockEntity;
import com.glisco.conjuring.blocks.soul_weaver.SoulWeaverBlock;
import com.glisco.conjuring.blocks.soul_weaver.SoulWeaverBlockEntity;
import com.glisco.conjuring.blocks.soulfire_forge.SoulfireForgeBlock;
import com.glisco.conjuring.blocks.soulfire_forge.SoulfireForgeBlockEntity;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ConjuringBlocks implements BlockRegistryContainer {

    public static final Block CONJURER = new ConjurerBlock();
    public static final Block SOULFIRE_FORGE = new SoulfireForgeBlock();
    public static final Block BLACKSTONE_PEDESTAL = new BlackstonePedestalBlock();
    public static final Block SOUL_FUNNEL = new SoulFunnelBlock();
    public static final Block SOUL_WEAVER = new SoulWeaverBlock();
    public static final Block GEM_TINKERER = new GemTinkererBlock();

    @Override
    public BlockItem createBlockItem(Block block, String identifier) {
        return new BlockItem(block, new OwoItemSettings().group(Conjuring.CONJURING_GROUP));
    }

    public static class Entities implements AutoRegistryContainer<BlockEntityType<?>> {

        public static final BlockEntityType<ConjurerBlockEntity> CONJURER = FabricBlockEntityTypeBuilder.create(ConjurerBlockEntity::new, ConjuringBlocks.CONJURER).build();
        public static final BlockEntityType<SoulfireForgeBlockEntity> SOULFIRE_FORGE = FabricBlockEntityTypeBuilder.create(SoulfireForgeBlockEntity::new, ConjuringBlocks.SOULFIRE_FORGE).build();
        public static final BlockEntityType<BlackstonePedestalBlockEntity> BLACKSTONE_PEDESTAL = FabricBlockEntityTypeBuilder.create(BlackstonePedestalBlockEntity::new, ConjuringBlocks.BLACKSTONE_PEDESTAL).build();
        public static final BlockEntityType<SoulFunnelBlockEntity> SOUL_FUNNEL = FabricBlockEntityTypeBuilder.create(SoulFunnelBlockEntity::new, ConjuringBlocks.SOUL_FUNNEL).build();
        public static final BlockEntityType<SoulWeaverBlockEntity> SOUL_WEAVER = FabricBlockEntityTypeBuilder.create(SoulWeaverBlockEntity::new, ConjuringBlocks.SOUL_WEAVER).build();
        public static final BlockEntityType<GemTinkererBlockEntity> GEM_TINKERER = FabricBlockEntityTypeBuilder.create(GemTinkererBlockEntity::new, ConjuringBlocks.GEM_TINKERER).build();

        @Override
        public Registry<BlockEntityType<?>> getRegistry() {
            return Registry.BLOCK_ENTITY_TYPE;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<BlockEntityType<?>> getTargetFieldType() {
            return (Class<BlockEntityType<?>>) (Object) BlockEntityType.class;
        }
    }
}
