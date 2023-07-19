package ivorius.brigadieropts.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
    public final Map<ArgumentName, ArgumentType<?>> arguments;
    public final Map<ArgumentName, ArgumentBranch> branches;
    public ExpectingCommand(CommandDispatcher<CommandSource> commandDispatcher) {
        permission = permissionLevel();
        commandName = commandName();
        arguments = typeMap();
        argumentNames = new ArrayList<>(arguments.keySet());
        branches = getBranches();
        register(commandDispatcher);
    }
    public void register(CommandDispatcher<CommandSource> commandDispatcher) {
        List<ArgumentName> queued = new ArrayList<>();
        List<ArgumentName> literals = new ArrayList<>();
        for(ArgumentName argumentName : argumentNames) {
            if(argumentName.isLiteral) {
                literals.add(argumentName);
            }
        }
        for(ArgumentName argumentName : argumentNames) {
            if(argumentName.required && !argumentName.isLiteral) {
                queued.add(argumentName);
            }
        }
        List<WrappedCommandNode> nodes = changeForRequired(commandDispatcher, queued, literals);
        for (WrappedCommandNode originNode : nodes) {
            for (int i = 0; i < argumentNames.size(); i++) {
                if (branches.get(argumentNames.get(i)) == originNode.branch || literals.isEmpty()) {
                    List<ArgumentName> accepted = new ArrayList<>();
                    ArgumentName currentName = argumentNames.get(i);
                    accepted.add(currentName);
                    WrappedCommandNode currentNode = new WrappedCommandNode(originNode.branch, commandDispatcher.register(originNode.createBuilder()
                            .then(!currentName.isLiteral ? Commands.argument(currentName.name, arguments.get(currentName)) : Commands.literal(currentName.name))
                            .executes(context -> buildArgsAndExecute(context, accepted))));
                    List<Integer> startingList = new ArrayList<>();
                    startingList.add(i);
                    build(commandDispatcher, startingList, currentNode, accepted);
                }
            }
        }
    }
    public void build(CommandDispatcher<CommandSource> commandDispatcher, List<Integer> i, WrappedCommandNode currentNode, List<ArgumentName> accepted) {
        for(int a = 0; a < argumentNames.size(); a++) {
            if(i.contains(a) || currentNode.branch != branches.get(argumentNames.get(a)))
                continue;
            List<ArgumentName> nextAccepted = new ArrayList<>();
            nextAccepted.addAll(accepted);
            ArgumentName currentName = argumentNames.get(a);
            nextAccepted.add(currentName);
            WrappedCommandNode nextNode = new WrappedCommandNode(currentNode.branch, commandDispatcher.register(currentNode.createBuilder()
                    .then(!currentName.isLiteral ? Commands.argument(currentName.name, arguments.get(currentName)) : Commands.literal(currentName.name))
                    .executes(context -> buildArgsAndExecute(context, nextAccepted))));
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
            Object o = name.function.apply(context, name.name);
            if(o != null)
                args.put(name, o);
        }
        return execute(context.getSource(), args);
    }
    public List<WrappedCommandNode> changeForRequired(CommandDispatcher<CommandSource> commandDispatcher, List<ArgumentName> required, List<ArgumentName> literals) {
        LiteralArgumentBuilder<CommandSource> baseBuilder = Commands.literal(commandName).requires((commandSource) -> commandSource.hasPermission(permission));
        List<ArgumentName> defaultBranchRequired = new ArrayList<>();
        for (ArgumentName name : required) {
            if (branches.get(name) == ArgumentBranch.DEFAULT) {
                baseBuilder = baseBuilder.then(Commands.argument(name.name, arguments.get(name)));
                defaultBranchRequired.add(name);
            }
        }
        argumentNames.removeAll(required);
        ArrayList<WrappedCommandNode> ret = new ArrayList<>();
        if (!literals.isEmpty()) {
            for (ArgumentName literal : literals) {
                LiteralArgumentBuilder<CommandSource> branchBuilder = baseBuilder;
                List<ArgumentName> thisBranch = new ArrayList<>();
                for (ArgumentName name : required) {
                    if(branches.get(name) == branches.get(literal)) {
                        branchBuilder = branchBuilder.then(Commands.argument(name.name, arguments.get(name)));
                        thisBranch.add(name);
                    }
                }
                thisBranch.addAll(defaultBranchRequired);
                ret.add(new WrappedCommandNode(branches.get(literal), commandDispatcher.register(branchBuilder.executes(context -> buildArgsAndExecute(context, thisBranch)))));
            }
            return ret;
        }
        baseBuilder = baseBuilder.executes(context -> buildArgsAndExecute(context, required));
        ret.add(new WrappedCommandNode(ArgumentBranch.DEFAULT, commandDispatcher.register(baseBuilder)));
        return ret;
    }
    abstract int permissionLevel();
    abstract String commandName();
    abstract Map<ArgumentName, ArgumentType<?>> typeMap();
    public Map<ArgumentName, ArgumentBranch> getBranches() {
        Map<ArgumentName, ArgumentBranch> branches = new HashMap<>();
        for(ArgumentName argumentName : argumentNames) {
            branches.put(argumentName, ArgumentBranch.DEFAULT);
        }
        return branches;
    }
}
