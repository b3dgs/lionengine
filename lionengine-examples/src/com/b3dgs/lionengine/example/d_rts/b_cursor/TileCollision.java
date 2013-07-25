package com.b3dgs.lionengine.example.d_rts.b_cursor;

/**
 * Collision type.
 */
enum TileCollision
{
    /** Ground collision. */
    GROUND("Ground"),
    /** Tree collision. */
    TREE("Tree"),
    /** Water collision. */
    WATER("Water"),
    /** Border collision. */
    BORDER("Border"),
    /** No collision. */
    NONE("None");

    /** Enum name. */
    private final String name;

    /**
     * Constructor.
     * 
     * @param name The name.
     */
    private TileCollision(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
