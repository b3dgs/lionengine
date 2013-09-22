package com.b3dgs.lionengine.example.d_rts.d_ability;

import java.util.Locale;

/**
 * List of projectile types.
 */
public enum ProjectileType
{
    /** Arrow projectile. */
    SPEAR;

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
