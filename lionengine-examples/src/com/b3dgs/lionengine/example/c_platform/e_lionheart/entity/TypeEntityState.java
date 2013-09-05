package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

/**
 * List of entity states.
 */
public enum TypeEntityState
{
    /** Idle state. */
    IDLE("idle"),
    /** Crouch state. */
    CROUCH("crouch"),
    /** Die state. */
    DIE("die"),
    /** Die state. */
    DEAD("dead"),
    /** Fallen state. */
    FALLEN("fallen"),
    /** Walk state. */
    WALK("walk"),
    /** Turning state. */
    TURN("turn"),
    /** Jumping state. */
    JUMP("jump"),
    /** Falling state. */
    FALL("fall"),
    /** Border state. */
    BORDER("border"),
    /** Preparing attack. */
    ATTACK_PREPARING("attack_preparing"),
    /** Preparing attack down. */
    ATTACK_PREPARING_DOWN("attack_preparing_down"),
    /** Preparing attack. */
    ATTACK_PREPARED("attack_prepared"),
    /** Preparing attack down. */
    ATTACK_PREPARED_DOWN("attack_prepared_down"),
    /** Attack up. */
    ATTACK_UP("attack_up"),
    /** Attack horizontal. */
    ATTACK_HORIZONTAL("attack_horizontal"),
    /** Attack turning. */
    ATTACK_TURNING("attack_turning"),
    /** Attack down leg. */
    ATTACK_DOWN_LEG("attack_down_leg"),
    /** Attack while jumping. */
    ATTACK_JUMP("attack_jump"),
    /** Attack while falling. */
    ATTACK_FALL("attack_fall");

    /** Animation name. */
    private final String animationName;

    /**
     * Constructor.
     * 
     * @param name The animation name.
     */
    private TypeEntityState(String name)
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
