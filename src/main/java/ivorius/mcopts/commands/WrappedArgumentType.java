package ivorius.mcopts.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.*;

public class WrappedArgumentType implements AbstractedArgumentType {
    public final ArgumentType<?> argumentType;
    public WrappedArgumentType(ArgumentType<?> wrapped) {
        argumentType = wrapped;
    }
    @Override
    public Object getArgument(CommandContext<CommandSource> commandContext, String argumentName) throws CommandSyntaxException {
        if (argumentType instanceof AngleArgument)
            return AngleArgument.getAngle(commandContext, argumentName);
        if (argumentType instanceof BlockPosArgument)
            return BlockPosArgument.getOrLoadBlockPos(commandContext, argumentName);
        if (argumentType instanceof BlockPredicateArgument)
            return BlockPredicateArgument.getBlockPredicate(commandContext, argumentName);
        if (argumentType instanceof BlockStateArgument)
            return BlockStateArgument.getBlock(commandContext, argumentName);
        if (argumentType instanceof BoolArgumentType)
            return BoolArgumentType.getBool(commandContext, argumentName);
        if (argumentType instanceof ColorArgument)
            return ColorArgument.getColor(commandContext, argumentName);
        if (argumentType instanceof ColumnPosArgument)
            return ColumnPosArgument.getColumnPos(commandContext, argumentName);
        if (argumentType instanceof ComponentArgument)
            return ComponentArgument.getComponent(commandContext, argumentName);
        if (argumentType instanceof DimensionArgument)
            return DimensionArgument.getDimension(commandContext, argumentName);
        if (argumentType instanceof AbstractedArgumentType)
            return ((AbstractedArgumentType) argumentType).getArgument(commandContext, argumentName);
        return null;
    }
}
