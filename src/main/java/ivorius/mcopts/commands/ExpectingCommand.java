package ivorius.mcopts.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
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
    public final List<String> argumentNames;
    public final Map<String, ArgumentType<?>> arguments;
    public ExpectingCommand(int permissionLevel, String name, Map<String, ArgumentType<?>> argumentTypeMap) {
        permission = permissionLevel;
        commandName = name;
        arguments = argumentTypeMap;
        argumentNames = new ArrayList<>(argumentTypeMap.keySet());
    }
    public void register(CommandDispatcher<CommandSource> commandDispatcher) {
        LiteralCommandNode<CommandSource> originNode = commandDispatcher.register(Commands.literal(commandName).requires((commandSource) -> commandSource.hasPermission(permission)).executes(context -> execute(context.getSource(), new HashMap<>())));
        for(int i = 0; i < argumentNames.size(); i++) {
            List<String> accepted = new ArrayList<>();
            accepted.add(argumentNames.get(i));
            LiteralCommandNode<CommandSource> currentNode = commandDispatcher.register(originNode.createBuilder()
                    .then(Commands.argument(argumentNames.get(i), arguments.get(argumentNames.get(i))))
                    .executes(context -> buildArgsAndExecute(context, accepted)));
            List<Integer> startingList = new ArrayList<>();
            startingList.add(i);
            build(commandDispatcher, startingList, currentNode, accepted);
        }
    }
    public void build(CommandDispatcher<CommandSource> commandDispatcher, List<Integer> i, LiteralCommandNode<CommandSource> currentNode, List<String> accepted) {
        for(int a = argumentNames.size(); a > 0; a--) {
            if(i.contains(a))
                continue;
            List<String> nextAccepted = new ArrayList<>();
            nextAccepted.addAll(accepted);
            nextAccepted.add(argumentNames.get(a));
            LiteralCommandNode<CommandSource> nextNode = commandDispatcher.register(currentNode.createBuilder()
                    .then(Commands.argument(argumentNames.get(a), arguments.get(argumentNames.get(a))))
                    .executes(context -> buildArgsAndExecute(context, nextAccepted)));
            List<Integer> nextList = new ArrayList<>();
            nextList.add(a);
            nextList.addAll(i);
            build(commandDispatcher, nextList, nextNode, nextAccepted);
        }
    }
    abstract int execute(CommandSource commandSource, Map<String, Object> arguments);
    public int buildArgsAndExecute(CommandContext<CommandSource> context, List<String> acceptedArguments) throws CommandSyntaxException {
        Map<String, Object> args = new HashMap<>();
        for(String name : acceptedArguments) {
            WrappedArgumentType wrapped = new WrappedArgumentType(arguments.get(name));
            Object o = wrapped.getArgument(context, name);
            if(o != null)
                args.put(name, o);
        }
        return execute(context.getSource(), args);
    }
}
