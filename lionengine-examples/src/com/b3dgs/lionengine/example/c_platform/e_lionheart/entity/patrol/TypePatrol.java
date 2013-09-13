package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol;

/**
 * List of entity movements.
 */
public enum TypePatrol
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
    private static final TypePatrol[] VALUES = TypePatrol.values();

    /**
     * Get the type from its index.
     * 
     * @param index The index.
     * @return The type.
     */
    public static TypePatrol get(int index)
    {
        return TypePatrol.VALUES[index];
    }

    /** Index value. */
    private final int index;

    /**
     * Constructor.
     * 
     * @param index The index value.
     */
    private TypePatrol(int index)
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
