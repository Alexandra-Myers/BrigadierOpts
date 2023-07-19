package ivorius.brigadieropts.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ivorius.brigadieropts.enums.FunctionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.FunctionArgument;

import java.util.Objects;

public class WrappedFunctionArgumentType extends WrappedArgumentType {
    public final FunctionType functionType;
    public WrappedFunctionArgumentType(ArgumentType<?> wrapped, FunctionType type) {
        super(wrapped);
        functionType = type;
    }

    @Override
    public Object getArgument(CommandContext<CommandSource> commandContext, String argumentName) throws CommandSyntaxException {
        if (Objects.requireNonNull(functionType) == FunctionType.FUNCTION_OR_TAG) {
            return FunctionArgument.getFunctionOrTag(commandContext, argumentName);
        }
        return FunctionArgument.getFunctions(commandContext, argumentName);
    }
}
