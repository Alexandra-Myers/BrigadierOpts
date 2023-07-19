package ivorius.brigadieropts.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ivorius.brigadieropts.enums.ResourceLocationType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ResourceLocationArgument;

public class WrappedResourceLocationArgumentType extends WrappedArgumentType {
    public final ResourceLocationType type;
    public WrappedResourceLocationArgumentType(ArgumentType<?> wrapped, ResourceLocationType resourceLocationType) {
        super(wrapped);
        type = resourceLocationType;
    }

    @Override
    public Object getArgument(CommandContext<CommandSource> commandContext, String argumentName) throws CommandSyntaxException {
        switch (type) {
            case RECIPE:
                return ResourceLocationArgument.getRecipe(commandContext, argumentName);
            case ATTRIBUTE:
                return ResourceLocationArgument.getAttribute(commandContext, argumentName);
            case ADVANCEMENT:
                return ResourceLocationArgument.getAdvancement(commandContext, argumentName);
            case LOOT_CONDITION:
                return ResourceLocationArgument.getPredicate(commandContext, argumentName);
            default:
                return ResourceLocationArgument.getId(commandContext, argumentName);
        }
    }
}
