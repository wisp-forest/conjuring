package com.glisco.conjuring.util;

import com.glisco.conjuring.items.ConjuringFocus;
import com.glisco.conjuring.items.ConjuringItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CreateConjuringFocusCommand {

    private static final SimpleCommandExceptionType INVALID_ENTITY_TYPE = new SimpleCommandExceptionType(Text.of("Invalid entity type"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(literal("create_conjuring_focus")
                .then(argument("entity_type", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(context -> execute(context, false))
                        .then(argument("stabilized", BoolArgumentType.bool())
                                .executes(context -> execute(context, BoolArgumentType.getBool(context, "stabilized"))))));
    }

    private static int execute(CommandContext<ServerCommandSource> context, boolean stabilized) throws CommandSyntaxException {
        var stack = new ItemStack(stabilized ? ConjuringItems.STABILIZED_CONJURING_FOCUS : ConjuringItems.CONJURING_FOCUS);
        ConjuringFocus.writeData(stack, Registry.ENTITY_TYPE.getOrEmpty(EntitySummonArgumentType.getEntitySummon(context, "entity_type"))
                .orElseThrow(INVALID_ENTITY_TYPE::create));
        context.getSource().getPlayer().getInventory().offerOrDrop(stack);

        return 0;
    }

}
