package ivorius.brigadieropts.commands;

public class ArgumentName {
    public final String name;
    public final boolean required;
    public final boolean isLiteral;
    public ArgumentName(String name, boolean isRequired, boolean literal) {
        this.name = name;
        required = isRequired || literal;
        isLiteral = literal;
    }
}
