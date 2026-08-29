package io.github.darzizalol.focusfarm.logic;

/** UI event emitted after a command completes successfully. */
public enum FarmEvent {
    /** A crop was planted. */
    PLANTED,
    /** A crop was watered. */
    WATERED,
    /** A crop was fertilized. */
    FERTILIZED,
    /** A crop was harvested. */
    HARVESTED,
    /** Farm status was requested. */
    STATUS,
    /** Command help was requested. */
    HELP,
    /** Application exit was requested. */
    EXIT
}
