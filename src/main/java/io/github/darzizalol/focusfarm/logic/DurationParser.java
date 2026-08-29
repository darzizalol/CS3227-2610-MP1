package io.github.darzizalol.focusfarm.logic;

import java.time.Duration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.darzizalol.focusfarm.model.FarmException;

/** Parses short countdown duration values such as 10s and 5m. */
public final class DurationParser {
    private static final Pattern DURATION_PATTERN = Pattern.compile("^(\\d+)([sm])$");

    private DurationParser() {
    }

    /** Parses seconds or minutes into a Java duration. */
    public static Duration parse(String input) throws FarmException {
        Matcher matcher = DURATION_PATTERN.matcher(input.toLowerCase(Locale.ROOT));
        if (!matcher.matches()) {
            throw new FarmException("Invalid duration '" + input + "'. Use seconds or minutes, for example 10s or 1m.");
        }
        try {
            long amount = Long.parseLong(matcher.group(1));
            return matcher.group(2).equals("s") ? Duration.ofSeconds(amount) : Duration.ofMinutes(amount);
        } catch (ArithmeticException | NumberFormatException exception) {
            throw new FarmException("Duration is too large.", exception);
        }
    }
}
