package ivorius.brigadieropts.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraftforge.server.command.EnumArgument;

public class WrappedEnumArgumentType<T extends Enum<T>> extends WrappedArgumentType {
    public final Class<T> enumClass;
    public WrappedEnumArgumentType(Class<T> enumClass) {
        super(EnumArgument.enumArgument(enumClass));
        this.enumClass = enumClass;
    }

    @Override
    public Object getArgument(CommandContext<CommandSource> commandContext, String argumentName) throws CommandSyntaxException {
        return commandContext.getArgument(argumentName, enumClass);
    }
}
