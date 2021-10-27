package com.glisco.conjuring_testmod.mixin;

import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Optional;

@Mixin(StructureManager.class)
public interface StructureManagerAccessor {

    @Accessor("structures")
    Map<Identifier, Optional<Structure>> getStructures();

}
