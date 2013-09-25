package com.b3dgs.lionengine.example.d_rts.c_controlpanel;

/**
 * List of collision types.
 */
public enum TileCollision
{
    /** Ground collision. */
    GROUND0(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND1(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND2(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND3(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND4(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND5(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND6(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND7(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND8(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND9(TileCollisionGroup.GROUND),
    /** Tree collision. */
    TREE_BORDER(TileCollisionGroup.TREE),
    /** Tree collision. */
    TREE(TileCollisionGroup.TREE),
    /** Water collision. */
    WATER(TileCollisionGroup.WATER),
    /** Border collision. */
    BORDER(TileCollisionGroup.BORDER),
    /** No collision. */
    NONE(TileCollisionGroup.NONE);

    /** Collision group. */
    private final TileCollisionGroup group;

    /**
     * Constructor.
     * 
     * @param group The collision group.
     */
    private TileCollision(TileCollisionGroup group)
    {
        this.group = group;
    }

    /**
     * Get the collision group.
     * 
     * @return The collision group.
     */
    public TileCollisionGroup getGroup()
    {
        return group;
    }
}
