package ivorius.brigadieropts.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;

public interface AbstractedArgumentType {
    Object getArgument(CommandContext<CommandSource> commandContext, String argumentName) throws CommandSyntaxException;
}
