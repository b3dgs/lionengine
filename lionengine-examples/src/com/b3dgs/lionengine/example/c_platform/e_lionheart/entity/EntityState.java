package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.util.Locale;

/**
 * List of entity states.
 */
public enum EntityState implements State
{
    /** Idle state. */
    IDLE,
    /** Die state. */
    DIE,
    /** Die state. */
    DEAD,
    /** Fallen state. */
    FALLEN,
    /** Walk state. */
    WALK,
    /** Turning state. */
    TURN,
    /** Prepare jump state. */
    PREPARE_JUMP,
    /** Jumping state. */
    JUMP,
    /** Falling state. */
    FALL,
    /** Hurt state. */
    HURT;

    /** Animation name. */
    private final String animationName;

    /**
     * Constructor.
     */
    private EntityState()
    {
        animationName = name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String getAnimationName()
    {
        return animationName;
    }
}
