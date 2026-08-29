package io.github.darzizalol.focusfarm.model;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/** The six crop varieties available in Focus Farm. */
public enum CropType {
    /** Carrot crop. */
    CARROT("Carrot"),
    /** Tomato crop. */
    TOMATO("Tomato"),
    /** Corn crop. */
    CORN("Corn"),
    /** Strawberry crop. */
    STRAWBERRY("Strawberry"),
    /** Pumpkin crop. */
    PUMPKIN("Pumpkin"),
    /** Cabbage crop. */
    CABBAGE("Cabbage");

    private final String displayName;

    CropType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the crop name shown to users.
     *
     * @return the display name
     */
    public String displayName() {
        return displayName;
    }

    /**
     * Parses a crop name without regard to case.
     *
     * @param value crop name entered by the user
     * @return the matching crop type
     * @throws FarmException if the name is not supported
     */
    public static CropType parse(String value) throws FarmException {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        try {
            return CropType.valueOf(normalized);
        } catch (IllegalArgumentException exception) {
            throw new FarmException("Unknown crop '" + value + "'. Available crops: " + availableCrops() + ".",
                    exception);
        }
    }

    /**
     * Returns all crop names as a comma-separated string.
     *
     * @return the available crop names
     */
    public static String availableCrops() {
        return Arrays.stream(values()).map(CropType::displayName).collect(Collectors.joining(", "));
    }
}
