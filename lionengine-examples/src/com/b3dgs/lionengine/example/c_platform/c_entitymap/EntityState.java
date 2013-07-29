package com.b3dgs.lionengine.example.c_platform.c_entitymap;

/**
 * List of entity states.
 */
enum EntityState
{
    /** Idle state. */
    IDLE,
    /** Walk state. */
    WALK,
    /** turn state. */
    TURN,
    /** Jump state. */
    JUMP;
    
    /** Animation name. */
    private final String animationName;

    /**
     * Constructor.
     */
    private EntityState()
    {
        animationName = name().toLowerCase();
    }

    /**
     * Get the animation name.
     * 
     * @return The animation name.
     */
    public String getAnimationName()
    {
        return animationName;
    }
}
