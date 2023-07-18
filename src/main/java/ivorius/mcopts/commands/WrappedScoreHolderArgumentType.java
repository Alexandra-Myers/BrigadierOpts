package ivorius.mcopts.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ivorius.mcopts.enums.ScoreHolderType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ScoreHolderArgument;

public class WrappedScoreHolderArgumentType extends WrappedArgumentType {
    public final ScoreHolderType type;
    public WrappedScoreHolderArgumentType(ArgumentType<?> wrapped, ScoreHolderType holderType) {
        super(wrapped);
        type = holderType;
    }

    @Override
    public Object getArgument(CommandContext<CommandSource> commandContext, String argumentName) throws CommandSyntaxException {
        switch (type) {
            case SINGLE_NAME:
                return ScoreHolderArgument.getName(commandContext, argumentName);
            case ALL_NAMES:
                return ScoreHolderArgument.getNames(commandContext, argumentName);
            default:
                return ScoreHolderArgument.getNamesWithDefaultWildcard(commandContext, argumentName);
        }
    }
}
