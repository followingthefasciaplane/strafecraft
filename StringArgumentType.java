import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringArgumentType implements ArgumentType<String> {
    private static final List<String> VALID_COMMANDS = Arrays.asList(
            "bhoptarget", "bhop", "permissions", "setPermission",
            "airAcceleration", "airStrafeSpeed", "groundSpeed",
            "bunnyHopMultiplier", "maxAirSpeed", "friction",
            "config", "help"
    );

    private StringArgumentType() {
    }

    public static StringArgumentType string() {
        return new StringArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();

        if (remaining.startsWith("bhop")) {
            if (remaining.equals("bhop")) {
                return builder.suggest("bhop", "bhoptarget").buildFuture();
            } else if (remaining.equals("bhopt")) {
                return builder.suggest("bhoptarget").buildFuture();
            }
        } else if (remaining.startsWith("air")) {
            if (remaining.equals("air")) {
                return builder.suggest("airAcceleration", "airStrafeSpeed").buildFuture();
            } else if (remaining.equals("aira")) {
                return builder.suggest("airAcceleration", "airStrafeSpeed").buildFuture();
            } else if (remaining.equals("airac")) {
                return builder.suggest("airAcceleration").buildFuture();
            } else if (remaining.equals("airstr")) {
                return builder.suggest("airStrafeSpeed").buildFuture();
            }
        }

        List<String> suggestions = VALID_COMMANDS.stream()
                .filter(command -> command.startsWith(remaining) && !command.equals("bhop") && !command.equals("bhoptarget") && !command.equals("airAcceleration") && !command.equals("airStrafeSpeed"))
                .collect(Collectors.toList());

        return builder.suggestFromElements(suggestions).buildFuture();
    }
}