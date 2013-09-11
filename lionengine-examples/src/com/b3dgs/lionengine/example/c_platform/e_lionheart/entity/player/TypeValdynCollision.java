package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import java.util.Locale;

/**
 * List of valdyn collision types.
 */
public enum TypeValdynCollision
{
    /** Stand collision. */
    STAND,
    /** Crouch collision. */
    CROUCH;

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
