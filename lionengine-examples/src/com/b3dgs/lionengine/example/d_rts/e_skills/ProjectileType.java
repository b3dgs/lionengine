package com.b3dgs.lionengine.example.d_rts.e_skills;

import java.util.Locale;

/**
 * List of projectile type.
 */
public enum ProjectileType
{
    /** Spear projectile. */
    SPEAR;

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
