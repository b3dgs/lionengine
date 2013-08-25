package com.b3dgs.lionengine.game;

/**
 * List of available orientations.
 */
public enum Orientation
{
    /** Looking north. */
    NORTH,
    /** Looking north east. */
    NORTH_EAST,
    /** Looking east. */
    EAST,
    /** Looking south east. */
    SOUTH_EAST,
    /** Looking down. */
    SOUTH,
    /** Looking south west. */
    SOUTH_WEST,
    /** Looking west. */
    WEST,
    /** Looking north west. */
    NORTH_WEST;

    /** Orientations number. */
    public static final int ORIENTATIONS_NUMBER = Orientation.values().length;
    /** Number of elements on one side. */
    public static final int ORIENTATIONS_NUMBER_HALF = Orientation.ORIENTATIONS_NUMBER / 2;

    /**
     * Get the next orientation from the source plus an offset.
     * 
     * @param from The source orientation.
     * @param offset The offset to apply.
     * @return The next orientation from the source.
     */
    public static Orientation next(Orientation from, int offset)
    {
        return Orientation.values()[(from.ordinal() + offset) % Orientation.ORIENTATIONS_NUMBER];
    }
}
