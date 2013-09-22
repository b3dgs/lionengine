package com.b3dgs.lionengine.example.d_rts.f_warcraft.type;

/**
 * List of collision types.
 */
public enum TypeCollision
{
    /** Ground collision. */
    GROUND0(TypeCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND1(TypeCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND2(TypeCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND3(TypeCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND4(TypeCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND5(TypeCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND6(TypeCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND7(TypeCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND8(TypeCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND9(TypeCollisionGroup.GROUND),
    /** Tree collision. */
    TREE_BORDER(TypeCollisionGroup.TREE),
    /** Tree collision. */
    TREE(TypeCollisionGroup.TREE),
    /** Water collision. */
    WATER(TypeCollisionGroup.WATER),
    /** Border collision. */
    BORDER(TypeCollisionGroup.BORDER),
    /** No collision. */
    NONE(TypeCollisionGroup.NONE);

    /** Collision group. */
    private final TypeCollisionGroup group;

    /**
     * Constructor.
     * 
     * @param group The collision group.
     */
    private TypeCollision(TypeCollisionGroup group)
    {
        this.group = group;
    }

    /**
     * Get the collision group.
     * 
     * @return The collision group.
     */
    public TypeCollisionGroup getGroup()
    {
        return group;
    }
}
