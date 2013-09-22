package com.b3dgs.lionengine.example.c_platform.d_opponent;

import java.util.Locale;

/**
 * List of entity types.
 */
enum EntityType
{
    /** Mario. */
    MARIO,
    /** Goomba. */
    GOOMBA;

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
