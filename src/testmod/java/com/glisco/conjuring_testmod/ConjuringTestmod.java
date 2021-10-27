package com.glisco.conjuring_testmod;

import com.glisco.conjuring_testmod.mixin.StructureManagerAccessor;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ConjuringTestmod implements ModInitializer {

    private static final SuggestionProvider<ServerCommandSource> STRUCTURES =
            (context, builder) -> CommandSource.suggestIdentifiers(((StructureManagerAccessor) context.getSource().getServer().getStructureManager()).getStructures().keySet(), builder);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("export_structure").then(argument("structure", IdentifierArgumentType.identifier()).suggests(STRUCTURES).executes(context -> {
                System.out.println(context.getSource().getServer().getStructureManager().getStructure(IdentifierArgumentType.getIdentifier(context, "structure")).get().writeNbt(new NbtCompound()).asString());
                return 0;
            })));
        });
    }
}
