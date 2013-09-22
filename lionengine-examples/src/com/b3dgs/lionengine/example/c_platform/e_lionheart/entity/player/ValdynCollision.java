package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import java.util.Locale;

/**
 * List of valdyn collision types.
 */
enum ValdynCollision
{
    /** Stand collision. */
    STAND,
    /** Crouch collision. */
    CROUCH,
    /** Fall attack collision. */
    ATTACK_FALL;

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
