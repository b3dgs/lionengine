package com.b3dgs.lionengine.example.f_network;

/**
 * Networked messages enum type.
 */
enum TypeMessage
{
    /** Chat message. */
    MESSAGE_CHAT,
    /** Entity message. */
    MESSAGE_ENTITY,
    /** Factory message. */
    MESSAGE_FACTORY;

    /** Values. */
    private static final TypeMessage[] VALUES = TypeMessage.values();

    /**
     * Get the message type from its ordinal.
     * 
     * @param ordinal The ordinal.
     * @return The enum.
     */
    public static TypeMessage fromOrdinal(int ordinal)
    {
        return TypeMessage.VALUES[ordinal];
    }
}
