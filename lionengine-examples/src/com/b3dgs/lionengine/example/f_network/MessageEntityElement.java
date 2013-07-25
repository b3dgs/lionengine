package com.b3dgs.lionengine.example.f_network;

/**
 * List of actions shared.
 */
public enum MessageEntityElement
{
    /** Up flag (boolean). */
    UP,
    /** Down flag (boolean). */
    DOWN,
    /** Right flag (boolean). */
    RIGHT,
    /** Left flag (boolean). */
    LEFT,
    /** Location adjuster x (integer). */
    LOCATION_X,
    /** Location adjuster y (integer). */
    LOCATION_Y,
    /** Call jump method. */
    JUMP,
    /** Call die method. */
    DIE;

    /** Values. */
    private static final MessageEntityElement[] values = MessageEntityElement.values();

    /**
     * Get the message type from its ordinal.
     * 
     * @param ordinal The ordinal.
     * @return The enum.
     */
    public static MessageEntityElement fromOrdinal(int ordinal)
    {
        return MessageEntityElement.values[ordinal];
    }
}
