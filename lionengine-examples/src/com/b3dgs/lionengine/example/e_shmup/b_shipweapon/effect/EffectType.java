package com.b3dgs.lionengine.example.e_shmup.b_shipweapon.effect;

import java.util.Locale;

/**
 * List of effect types.
 */
public enum EffectType
{
    /** Smoke effect. */
    SMOKE;

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
