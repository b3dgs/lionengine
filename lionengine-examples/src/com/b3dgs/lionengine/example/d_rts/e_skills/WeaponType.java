package com.b3dgs.lionengine.example.d_rts.e_skills;

import java.util.Locale;

/**
 * List of weapon types.
 */
public enum WeaponType
{
    /** Axe weapon. */
    AXE,
    /** Spear weapon. */
    SPEAR;

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
