package ivorius.mcopts.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;

public class WrappedEntityArgumentType extends WrappedArgumentType {
    public final boolean optional;
    public WrappedEntityArgumentType(ArgumentType<?> wrapped, boolean getOptionals) {
        super(wrapped);
        optional = getOptionals;
    }

    @Override
    public Object getArgument(CommandContext<CommandSource> commandContext, String argumentName) throws CommandSyntaxException {
        if(argumentType.equals(EntityArgument.entities())) {
            if(optional)
                return EntityArgument.getOptionalEntities(commandContext, argumentName);
            return EntityArgument.getEntities(commandContext, argumentName);
        }
        if(argumentType.equals(EntityArgument.entity())) {
            return EntityArgument.getEntity(commandContext, argumentName);
        }
        if(argumentType.equals(EntityArgument.players())) {
            if(optional)
                return EntityArgument.getOptionalPlayers(commandContext, argumentName);
            return EntityArgument.getPlayers(commandContext, argumentName);
        }
        if(argumentType.equals(EntityArgument.player())) {
            return EntityArgument.getPlayer(commandContext, argumentName);
        }
        return EntityArgument.getOptionalEntities(commandContext, argumentName);
    }
}
