package ivorius.brigadieropts.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;

public class WrappedCommandNode extends LiteralCommandNode<CommandSource> {
    public final ArgumentBranch branch;
    public WrappedCommandNode(ArgumentBranch branch, LiteralCommandNode<CommandSource> original) {
        super(original.getLiteral(), original.getCommand(), original.getRequirement(), original.getRedirect(), original.getRedirectModifier(), original.isFork());
        this.branch = branch;
    }
}
