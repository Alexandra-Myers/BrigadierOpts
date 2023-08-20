package ivorius.brigadieropts.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
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
    public final List<ArgumentName> queued = new ArrayList<>();
    public final List<ArgumentName> literals = new ArrayList<>();
    public ExpectingCommand(CommandDispatcher<CommandSource> commandDispatcher) {
        permission = permissionLevel();
        commandName = commandName();
        arguments = typeMap();
        argumentNames = new ArrayList<>(arguments.keySet());
        branches = getBranches();
        register(commandDispatcher);
    }
    public void register(CommandDispatcher<CommandSource> commandDispatcher) {
        for(ArgumentName argumentName : argumentNames) {
            if(argumentName.isLiteral) {
                literals.add(argumentName);
            }
            if(argumentName.required && !argumentName.isLiteral) {
                queued.add(argumentName);
            }
        }
        List<WrappedCommandNode> nodes = changeForRequired(commandDispatcher);
        for (WrappedCommandNode originNode : nodes) {
            for (int i = 0; i < argumentNames.size(); i++) {
                if (branches.get(argumentNames.get(i)) == originNode.branch || literals.isEmpty()) {
                    List<ArgumentName> accepted = new ArrayList<>();
                    ArgumentName currentName = argumentNames.get(i);
                    accepted.add(currentName);
                    LiteralArgumentBuilder<CommandSource> builder = originNode.createBuilder()
                            .then(!currentName.isLiteral ? Commands.argument(currentName.name, arguments.get(currentName)) : Commands.literal(currentName.name))
                            .executes(context -> buildArgsAndExecute(context, accepted));
                    WrappedCommandNode currentNode = new WrappedCommandNode(originNode.branch, commandDispatcher.register(builder), builder);
                    List<Integer> startingList = new ArrayList<>();
                    startingList.add(i);
                    build(commandDispatcher, startingList, currentNode, accepted, builder);
                }
            }
        }
    }
    public void build(CommandDispatcher<CommandSource> commandDispatcher, List<Integer> i, WrappedCommandNode currentNode, List<ArgumentName> accepted, LiteralArgumentBuilder<CommandSource> currentBuilder) {
        for(int a = 0; a < argumentNames.size(); a++) {
            if(i.contains(a) || currentNode.branch != branches.get(argumentNames.get(a)))
                continue;
            List<ArgumentName> nextAccepted = new ArrayList<>(accepted);
            ArgumentName currentName = argumentNames.get(a);
            nextAccepted.add(currentName);
            LiteralArgumentBuilder<CommandSource> nextBuilder = currentBuilder
                    .then(!currentName.isLiteral ? Commands.argument(currentName.name, arguments.get(currentName)) : Commands.literal(currentName.name))
                    .executes(context -> buildArgsAndExecute(context, nextAccepted));
            WrappedCommandNode nextNode = new WrappedCommandNode(currentNode.branch, commandDispatcher.register(nextBuilder), nextBuilder);
            List<Integer> nextList = new ArrayList<>();
            nextList.add(a);
            nextList.addAll(i);
            build(commandDispatcher, nextList, nextNode, nextAccepted, nextBuilder);
        }
    }
    public abstract int execute(CommandSource commandSource, Map<String, Object> arguments);
    public int buildArgsAndExecute(CommandContext<CommandSource> context, List<ArgumentName> acceptedArguments) {
        Map<String, Object> args = new HashMap<>();
        for(ArgumentName name : acceptedArguments) {
            if (name.isLiteral)
                continue;
            Object o = name.function.apply(context, name.name);
            if(o != null)
                args.put(name.name, o);
        }
        return execute(context.getSource(), args);
    }
    public List<WrappedCommandNode> changeForRequired(CommandDispatcher<CommandSource> commandDispatcher) {
        LiteralArgumentBuilder<CommandSource> baseBuilder = Commands.literal(commandName).requires((commandSource) -> commandSource.hasPermission(permission));
        List<ArgumentName> defaultBranchRequired = new ArrayList<>();
        for (ArgumentName name : queued) {
            if (branches.get(name) == ArgumentBranch.DEFAULT) {
                baseBuilder = baseBuilder.then(Commands.argument(name.name, arguments.get(name)));
                defaultBranchRequired.add(name);
            }
        }
        argumentNames.removeAll(queued);
        ArrayList<WrappedCommandNode> ret = new ArrayList<>();
        if (!literals.isEmpty()) {
            for (ArgumentName literal : literals) {
                LiteralArgumentBuilder<CommandSource> branchBuilder = baseBuilder;
                List<ArgumentName> thisBranch = new ArrayList<>();
                for (ArgumentName name : queued) {
                    if(branches.get(name) == branches.get(literal)) {
                        branchBuilder = branchBuilder.then(Commands.argument(name.name, arguments.get(name)));
                        thisBranch.add(name);
                    }
                }
                thisBranch.addAll(defaultBranchRequired);
                branchBuilder = branchBuilder.executes(context -> buildArgsAndExecute(context, thisBranch));
                ret.add(new WrappedCommandNode(branches.get(literal), commandDispatcher.register(branchBuilder), branchBuilder));
            }
            argumentNames.removeAll(literals);
            return ret;
        }
        baseBuilder = baseBuilder.executes(context -> buildArgsAndExecute(context, queued));
        ret.add(new WrappedCommandNode(ArgumentBranch.DEFAULT, commandDispatcher.register(baseBuilder), baseBuilder));
        return ret;
    }
    public abstract int permissionLevel();
    public abstract String commandName();
    public abstract Map<ArgumentName, ArgumentType<?>> typeMap();
    public Map<ArgumentName, ArgumentBranch> getBranches() {
        Map<ArgumentName, ArgumentBranch> branches = new HashMap<>();
        for(ArgumentName argumentName : argumentNames) {
            branches.put(argumentName, ArgumentBranch.DEFAULT);
        }
        return branches;
    }
}
