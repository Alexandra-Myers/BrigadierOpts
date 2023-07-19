package ivorius.brigadieropts.commands;

public class ArgumentBranch {
    public final String name;
    public static final ArgumentBranch DEFAULT = new ArgumentBranch("default");
    public ArgumentBranch(String name) {
        this.name = name;
    }
}
