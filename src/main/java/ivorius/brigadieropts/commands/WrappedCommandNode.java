package ivorius.brigadieropts.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;

public class WrappedCommandNode extends LiteralCommandNode<CommandSource> {
    public final ArgumentBranch branch;
    public final LiteralArgumentBuilder<CommandSource> builder;
    public WrappedCommandNode(ArgumentBranch branch, LiteralCommandNode<CommandSource> original, LiteralArgumentBuilder<CommandSource> builder) {
        super(original.getLiteral(), original.getCommand(), original.getRequirement(), original.getRedirect(), original.getRedirectModifier(), original.isFork());
        this.branch = branch;
        this.builder = builder;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> createBuilder() {
        return builder;
    }
}
