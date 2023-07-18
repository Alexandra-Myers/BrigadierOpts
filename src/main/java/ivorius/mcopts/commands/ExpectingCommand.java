package ivorius.mcopts.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ExpectingCommand {
    public final int permission;
    public final String commandName;
    public final List<ArgumentName> argumentNames;
    public final Map<ArgumentName, WrappedArgumentType> arguments;
    public ExpectingCommand(int permissionLevel, String name, Map<ArgumentName, WrappedArgumentType> argumentTypeMap) {
        permission = permissionLevel;
        commandName = name;
        arguments = argumentTypeMap;
        argumentNames = new ArrayList<>(argumentTypeMap.keySet());
    }
    public void register(CommandDispatcher<CommandSource> commandDispatcher) {
        List<ArgumentName> queued = new ArrayList<>();
        for(ArgumentName argumentName : argumentNames) {
            if(argumentName.required) {
                queued.add(argumentName);
            }
        }
        LiteralCommandNode<CommandSource> originNode = changeForRequired(commandDispatcher, queued);
        for(int i = 0; i < argumentNames.size(); i++) {
            List<ArgumentName> accepted = new ArrayList<>();
            ArgumentName currentName = argumentNames.get(i);
            accepted.add(currentName);
            LiteralCommandNode<CommandSource> currentNode = commandDispatcher.register(originNode.createBuilder()
                    .then(!currentName.isLiteral ? Commands.argument(currentName.name, arguments.get(currentName).argumentType) : Commands.literal(currentName.name))
                    .executes(context -> buildArgsAndExecute(context, accepted)));
            List<Integer> startingList = new ArrayList<>();
            startingList.add(i);
            build(commandDispatcher, startingList, currentNode, accepted);
        }
    }
    public void build(CommandDispatcher<CommandSource> commandDispatcher, List<Integer> i, LiteralCommandNode<CommandSource> currentNode, List<ArgumentName> accepted) {
        for(int a = 0; a < argumentNames.size(); a++) {
            if(i.contains(a))
                continue;
            List<ArgumentName> nextAccepted = new ArrayList<>();
            nextAccepted.addAll(accepted);
            ArgumentName currentName = argumentNames.get(a);
            nextAccepted.add(currentName);
            LiteralCommandNode<CommandSource> nextNode = commandDispatcher.register(currentNode.createBuilder()
                    .then(!currentName.isLiteral ? Commands.argument(currentName.name, arguments.get(currentName).argumentType) : Commands.literal(currentName.name))
                    .executes(context -> buildArgsAndExecute(context, nextAccepted)));
            List<Integer> nextList = new ArrayList<>();
            nextList.add(a);
            nextList.addAll(i);
            build(commandDispatcher, nextList, nextNode, nextAccepted);
        }
    }
    abstract int execute(CommandSource commandSource, Map<ArgumentName, Object> arguments);
    public int buildArgsAndExecute(CommandContext<CommandSource> context, List<ArgumentName> acceptedArguments) throws CommandSyntaxException {
        Map<ArgumentName, Object> args = new HashMap<>();
        for(ArgumentName name : acceptedArguments) {
            if (name.isLiteral)
                continue;
            WrappedArgumentType wrapped = arguments.get(name);
            Object o = wrapped.getArgument(context, name.name);
            if(o != null)
                args.put(name, o);
        }
        return execute(context.getSource(), args);
    }
    public LiteralCommandNode<CommandSource> changeForRequired(CommandDispatcher<CommandSource> commandDispatcher, List<ArgumentName> required) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal(commandName).requires((commandSource) -> commandSource.hasPermission(permission));
        for (ArgumentName name : required) {
            builder = builder.then(!name.isLiteral ? Commands.argument(name.name, arguments.get(name).argumentType) : Commands.literal(name.name));
        }
        builder = builder.executes(context -> buildArgsAndExecute(context, required));
        argumentNames.removeAll(required);
        return commandDispatcher.register(builder);
    }
}
