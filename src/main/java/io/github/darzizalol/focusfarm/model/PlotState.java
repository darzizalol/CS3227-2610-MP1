package io.github.darzizalol.focusfarm.model;

/** Lifecycle states for a farm plot. */
public enum PlotState {
    /** Plot has no crop. */
    EMPTY,
    /** Crop is planted but not watered. */
    PLANTED,
    /** Watered crop is counting down. */
    GROWING,
    /** Crop can be harvested. */
    READY
}
