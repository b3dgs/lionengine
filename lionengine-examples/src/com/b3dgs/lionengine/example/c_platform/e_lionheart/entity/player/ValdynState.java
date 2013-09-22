package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import java.util.Locale;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.State;

/**
 * List of entity states.
 */
enum ValdynState implements State
{
    /** Crouch state. */
    CROUCH,
    /** Border state. */
    BORDER,
    /** Slide state. */
    SLIDE,
    /** Slide fast state. */
    SLIDE_FAST,
    /** Slide slow state. */
    SLIDE_SLOW,
    /** Liana idle. */
    LIANA_IDLE,
    /** Liana walk. */
    LIANA_WALK,
    /** Liana slide. */
    LIANA_SLIDE,
    /** Preparing attack. */
    ATTACK_PREPARING,
    /** Preparing attack down. */
    ATTACK_PREPARING_DOWN,
    /** Preparing attack. */
    ATTACK_PREPARED,
    /** Preparing attack down. */
    ATTACK_PREPARED_DOWN,
    /** Attack up. */
    ATTACK_UP,
    /** Attack horizontal. */
    ATTACK_HORIZONTAL,
    /** Attack turning. */
    ATTACK_TURNING,
    /** Attack down leg. */
    ATTACK_DOWN_LEG,
    /** Attack while jumping. */
    ATTACK_JUMP,
    /** Attack while falling. */
    ATTACK_FALL,
    /** Attack on liana. */
    ATTACK_LIANA,
    /** Attack while sliding. */
    ATTACK_SLIDE;

    /** Animation name. */
    private final String animationName;

    /**
     * Constructor.
     */
    private ValdynState()
    {
        animationName = name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String getAnimationName()
    {
        return animationName;
    }
}
