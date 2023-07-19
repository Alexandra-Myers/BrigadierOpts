package ivorius.brigadieropts.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ObjectiveArgument;

public class WrappedObjectiveArgumentType extends WrappedArgumentType {
    public final boolean isWritable;
    public WrappedObjectiveArgumentType(ArgumentType<?> wrapped, boolean writable) {
        super(wrapped);
        isWritable = writable;
    }

    @Override
    public Object getArgument(CommandContext<CommandSource> commandContext, String argumentName) throws CommandSyntaxException {
        if(isWritable)
            return ObjectiveArgument.getWritableObjective(commandContext, argumentName);
        return ObjectiveArgument.getObjective(commandContext, argumentName);
    }
}
