package ivorius.brigadieropts.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class ArgumentName {
    public final String name;
    public final boolean required;
    public final boolean isLiteral;
    public final BiFunction<CommandContext<CommandSource>, String, ?> function;
    public ArgumentName(String name, boolean isRequired, boolean literal, @NotNull BiFunction<CommandContext<CommandSource>, String, ?> argumentFunction) {
        this.name = name;
        required = isRequired || literal;
        isLiteral = literal;
        function = argumentFunction;
    }
}
