package com.b3dgs.lionengine.example.d_rts.c_controlpanel;

import java.util.Locale;

/**
 * List of entity types.
 */
enum EntityType
{
    /** Peon unit. */
    PEON;

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
