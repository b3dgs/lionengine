package com.b3dgs.lionengine.example.f_network;

/**
 * List of entity types. Lower case is preferred, as the name has to be the same with its corresponding files.
 */
public enum TypeEntity
{
    /** Mario. */
    mario,
    /** Goomba. */
    goomba;

    /** Values. */
    private static final TypeEntity[] values = TypeEntity.values();

    /**
     * Get the message type from its ordinal.
     * 
     * @param ordinal The ordinal.
     * @return The enum.
     */
    public static TypeEntity fromOrdinal(int ordinal)
    {
        return TypeEntity.values[ordinal];
    }
}
