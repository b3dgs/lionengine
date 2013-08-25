package com.b3dgs.lionengine.example.c_platform.d_opponent;

import java.util.Locale;

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
    JUMP,
    /** Dead state. */
    DEAD;

    /** Animation name. */
    private final String animationName;

    /**
     * Constructor.
     */
    private EntityState()
    {
        animationName = name().toLowerCase(Locale.ENGLISH);
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
