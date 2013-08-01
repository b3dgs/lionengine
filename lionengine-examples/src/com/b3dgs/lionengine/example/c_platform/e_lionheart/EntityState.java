package com.b3dgs.lionengine.example.c_platform.e_lionheart;

/**
 * List of entity states.
 */
public enum EntityState
{
    /** Idle state. */
    IDLE("idle"),
    /** Die state. */
    DIE("die"),
    /** Die state. */
    DEAD("dead"),
    /** Fallen state. */
    FALLEN("fallen"),
    /** Walk state. */
    WALK("walk"),
    /** Turning state. */
    TURN("walk"),
    /** Jumping state. */
    JUMP("jump"),
    /** Falling state. */
    FALL("fall"),
    /** Border state. */
    BORDER("border");

    /** Animation name. */
    private final String animationName;

    /**
     * Constructor.
     * 
     * @param name The animation name.
     */
    private EntityState(String name)
    {
        animationName = name;
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
