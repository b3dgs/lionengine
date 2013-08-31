package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

/**
 * List of entity movements.
 */
public enum TypeEntityMovement
{
    /** No movement. */
    NONE(0),
    /** Horizontal movement. */
    HORIZONTAL(1),
    /** Vertical movement. */
    VERTICAL(2),
    /** Rotating movement. */
    ROTATING(3);

    /** Values. */
    private static final TypeEntityMovement[] VALUES = TypeEntityMovement.values();

    /**
     * Get the type from its index.
     * 
     * @param index The index.
     * @return The type.
     */
    public static TypeEntityMovement get(int index)
    {
        return TypeEntityMovement.VALUES[index];
    }

    /** Index value. */
    private final int index;

    /**
     * Constructor.
     * 
     * @param index The index value.
     */
    private TypeEntityMovement(int index)
    {
        this.index = index;
    }

    /**
     * Get the index value.
     * 
     * @return The index value.
     */
    public int getIndex()
    {
        return index;
    }
}
