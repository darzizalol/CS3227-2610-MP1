package io.github.darzizalol.focusfarm.model;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/** The six crop varieties available in Focus Farm. */
public enum CropType {
    CARROT("Carrot"),
    TOMATO("Tomato"),
    CORN("Corn"),
    STRAWBERRY("Strawberry"),
    PUMPKIN("Pumpkin"),
    CABBAGE("Cabbage");

    private final String displayName;

    CropType(String displayName) {
        this.displayName = displayName;
    }

    /** Returns the human-readable crop name. */
    public String displayName() {
        return displayName;
    }

    /** Parses a crop name without regard to case. */
    public static CropType parse(String value) throws FarmException {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        try {
            return CropType.valueOf(normalized);
        } catch (IllegalArgumentException exception) {
            throw new FarmException("Unknown crop '" + value + "'. Available crops: " + availableCrops() + ".",
                    exception);
        }
    }

    /** Returns the available crop names as a comma-separated string. */
    public static String availableCrops() {
        return Arrays.stream(values()).map(CropType::displayName).collect(Collectors.joining(", "));
    }
}
